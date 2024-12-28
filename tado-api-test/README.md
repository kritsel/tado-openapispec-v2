# tado-api-test module

This module is the test harness for the tado API spec. 
The goal is to verify whether the API spec is a correct description 
of the actual tado API by executing API calls using an API client which is
generated from the tado API spec.

## Tado secrets required for integration tests

The test set-up expects environment variables to be present which contain your tado username and password.

You can do this with a script like this:
```
#!/bin/bash
export TADO_USERNAME='<fill in your tado username here>'
export TADO_PASSWORD='<fill in your tado password here'
```

## Tado home, zone, etc. identifiers required for integration tests

The tests use tado home, zone, etc. identifiers as specified in
`tado-api-test/src/test/resources/application.yaml`

You need to update those identifiers to execute the tests on your own home.

## Run the tests

You can use the following mvn command to run the tests
(run it from the `tado-openapi-spec-v2` parent directory)

`> mvn clean verify -P local-api-spec`