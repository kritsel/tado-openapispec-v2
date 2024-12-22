# Automated API test

An API test is executed on a weekly basis to verify that the tado API spec as documented here
is still a correct description of the actual API as hosted and operated by tado.

The automated API test uses `kotlin` and `maven` and consists of the following elements:

* `openapi-generator-maven-plugin` is used to generate kotlin API client code based on this OpenAPI specification
*  a set of kotlin integration tests which together cover almost* all operations in the API specification
   (* some destructive operations whose pre-test status are hard or impossible to automatically re-create 
    are not part of the test scope)
* `maven-failsafe-plugin` is used to execute the integration tests


The goal of the API test suite is to identify:

* API operations which are no longer supported
* API GET operations which return a response containing a new (yet unspecified) JSON property
* API GET operations which return a response containing a new (yet unspecified) JSON enum value
* API GET operations which return a response no longer containing a JSON property which used to be returned before
* API PUT/POST operations whose contract has changed, meaning they now fail for a certain input
while the same input used to be successful

# API test results

The [Failsafe Report](failsafe-report.html) lists the results of the most recently executed tests.