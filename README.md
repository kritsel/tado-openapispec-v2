# About this tado openapi spec

This is a community developed openapi spec for the tado API v2
as available on https://my.tado.com/api/v2.
It is based on the experiences of private persons who are experimenting
with the API to create their own solutions. 

This API specification is not maintained or officially published by the tado company.
The contents of this API specification is a best-effort product.
There are no guarantees that this API specification is a complete and 
correct representation of the API as provided by tado.

## Versioning
As this is a community managed openapi spec for an API which is 
actually offered by another company, a versioning scheme is used
which expresses the date on which the specification was modified.

The version format is `v2.<year>.<month>.<day>.<daily releasenumber>`.

The releasenumber is basically always `0`, unless we are in the unlikely
scenario where there would be multiple releases on the same day.

## Swagger UI
Visit https://kritsel.github.io/tado-openapispec-v2/ to try out the
API via Swagger UI with your own credentials.

# About tado
Tado is a german based company which offers a smart thermostat solution
to control heating and air-conditioning in your home.

Tado (https://www.tado.com/) sells hardware and services, 
which can be controlled and used via a mobile app. 
This mobile app uses the very same API which is documented here.

# Oauth2 authentication
The tado API only supports oauth2 authentication.

Here are some resources to help you to successfully authenticate
to the API.

* [1] https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
* [2] https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/
* [3] https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/

## About `client_id` and `client_secret`
Please note that [2] mentions that you can visit https://my.tado.com/webapp/env.js to
obtain a valid `client_id` and `client_secret`. 

However the official tado article [1] explicitly states
"Also, feel free to share the client credentials with other developers. 
That way we will be able to easily distinguish our own apps from 
third party developers."

So let's help tado and use the `client_id` and `client_secret` as 
mentioned in [1].

# Gratitudes
Without the groundwork done by Stephen C Phillips (https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/)
and Terence Eden (https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/)
I would have never started experimenting with the tado API,
and this tado openapi spec would have never seen the light of day.

# Contribute
Feel free to submit a PR if you want to contribute to this
openapi specification.