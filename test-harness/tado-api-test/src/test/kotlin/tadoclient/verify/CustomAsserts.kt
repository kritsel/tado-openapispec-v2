package tadoclient.verify

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import tadoclient.apis.HOME_ID
import kotlin.test.junit5.JUnit5Asserter.fail

// assert that the REST API invocation does not return a response with the given httpStatus
inline fun <R> assertNoHttpErrorStatus(unexpectedHttpErrorStatus: HttpStatus, executable: () -> R): R {
    try {
        return executable.invoke();
    } catch (e:Exception) {
        if (e is RestClientResponseException && e.statusCode==unexpectedHttpErrorStatus) {
            val extraText = if (unexpectedHttpErrorStatus==HttpStatus.FORBIDDEN) ", maybe tado no longer supports this endpoint" else ""
            fail("$unexpectedHttpErrorStatus$extraText")
        } else {
            throw e
        }
    }
}

// assert that the REST API invocation returns an error response with the given http status
inline fun <R> assertHttpErrorStatus(expectedHttpErrorStatus: HttpStatus, executable: () -> R): R? {
    try {
        return executable.invoke();
    } catch (e:Exception) {
        if (e is RestClientResponseException && e.statusCode==expectedHttpErrorStatus) {
            return null
        } else {
            fail(expectedHttpErrorStatus.toString());
        }
    }
}

// assert that the response includes an unknown property, resulting in an exception
// (used for chaos monkey testing, where an arbitrary property is ingested into the response body before deserializing;
//  chaos monkey testing is done to verify that message deserialization is set-up in a strict way,
//  so that the tests fail on (and thus reveal) any response containing new properties
//  not yet known by the generated API client code.)
inline fun <R> assertNoUnknownPropertyInResponse(executable: () -> R): R? {
    //org.springframework.web.client.RestClientException
    // org.springframework.http.converter.HttpMessageNotReadableException
    // com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
    try {
        return executable.invoke();
    } catch (e:RestClientException) {
        if (e.cause != null && e.cause is HttpMessageNotReadableException) {
            if (e.cause!!.cause != null && e.cause!!.cause is UnrecognizedPropertyException) {
                return null
            }
        }
    }
    fail ("expected an UnrecognizedPropertyException to be thrown")
}

// assert that the response does not contain any unknown properties
inline fun <R> assertUnknownPropertyErrorNotThrown(executable: () -> R): R {
    // org.springframework.web.client.RestClientException, caused by ->
    //   org.springframework.http.converter.HttpMessageNotReadableException, caused by ->
    //     com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
    try {
        return executable.invoke();
    } catch (e:RestClientException) {
        if (e.cause != null && e.cause is HttpMessageNotReadableException) {
            if (e.cause!!.cause != null && e.cause!!.cause is UnrecognizedPropertyException) {
                fail (e.cause!!.cause!!.toString())
            }
        }
        throw e
    }
}

inline fun <R> assertCorrectResponse(executable: () -> R): R {
    try {
        return executable.invoke();

    // handle 403
    } catch (e:RestClientResponseException) {
        if (e.statusCode==HttpStatus.FORBIDDEN) {
            fail("${HttpStatus.FORBIDDEN}, maybe tado no longer supports this endpoint")
        }
        throw e

    // handle unexpected JSON response content
    } catch (e:RestClientException) {
        if (e.cause != null && e.cause is HttpMessageNotReadableException) {
            if (e.cause!!.cause != null) {
                // handle unexpected JSON property name
                if (e.cause!!.cause is UnrecognizedPropertyException) {
                    fail(e.cause!!.cause!!.toString())
                // handle unexpected JSON enum value
                } else if (e.cause!!.cause is InvalidFormatException) {
                    fail(e.cause!!.cause!!.toString())
                }
            }
        }
        throw e
    }
}
