# Automated API test

## Why
An API test is executed on a weekly basis to verify that the tado API spec as documented here
is still a correct description of the actual API as hosted and operated by tado.

Tado can make both trivial and breaking changes to their API at any given time, and this test is meant
to identify those changes. When issues are detected, the API definition is corrected a.s.a.p.
The aim is to do this within a week.

The same test is executed whenever a change is made to the API definition.


The goal of the API test is to identify:

* API operations which are no longer supported
* API PUT/POST/DELETE operations whose input contract has changed, 
  meaning that input that used to produce a successful result now fails
* API GET operations which return a response containing a new (yet undocumented) JSON property
* API GET operations which return a response containing a new (yet undocumented) JSON enum value
* API GET operations which return a response no longer containing a JSON property which used to be returned before

## Test limitations

The test is executed by invoking tado API endpoints on an actual tado enabled home. 
This introduces some limitations as to what can be tested.

* Functionality which has a destructive nature which cannot easily be undone programmatically  
  (e.g. removing a device from a room) is not tested.
* Functionality which requires an active Auto-Assist subscription is not tested.
* Functionality which requires a tado controlled air conditioning is not tested.
* Operations which are not supported for tado X enabled homes cannot be identified.


## How
The automated API test is based on `kotlin`, `spring-boot`, `maven` and `GitHub workflows` technology,
and consists of the following elements:

* `openapi-generator-maven-plugin` is used to generate kotlin API client code based on this API definition
*  a set of kotlin integration tests which together cover almost* all operations in the API definition
   *(* see the limitations above)*
* `maven-failsafe-plugin` is used to execute the integration tests
* `maven-surefire-report-plugin` is used to generate a test report based on the integration test results
* `GitHub workflow` is used to publish the test report to the website you are reading now


## API test results

The [API test report](failsafe-report.html) lists the results of the most recently executed API test.