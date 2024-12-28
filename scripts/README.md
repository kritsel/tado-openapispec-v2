# How to use the `mvn-*.sh` scripts

## Execute from parent directory

The scripts are set-up in a way that they are expected to be executed from their parent directory.

Example: 

`> ./scripts/mvn-clean-verify-site.sh`

## Tado secrets required for integration tests

The mvn scripts which include the `verify` goal execute the integration tests.
These tests expect environment variables to be present which contain your tado username and password.

1. Lookup your tado username and password
2. In the `scripts` directory, create a file named `set-secrets.sh` which looks like this:
   (see `set-secrets.sh.example` for an example)

```
#!/bin/bash
export TADO_USERNAME='<fill in your tado username here>'
export TADO_PASSWORD='<fill in your tado password here'
```

## Tado home, zone, etc. identifiers required for integration tests

The mvn scripts which include the `verify` goal execute the integration tests.

The tests use tado home, zone, etc. identifiers as specified in
`tado-api-test/src/test/resources/application.yaml`

You need to update those identifiers to execute the tests on your own home.