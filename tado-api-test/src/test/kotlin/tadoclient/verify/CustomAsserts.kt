package tadoclient.verify

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import kotlin.test.junit5.JUnit5Asserter.fail

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

// ensures that a meaningful fail message is returned in the following situations:
// * 403 forbidden is returned
// * response contains un unknown JSON property name
// * response contains an unknown enum value
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
