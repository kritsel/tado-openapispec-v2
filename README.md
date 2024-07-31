# About the tado OpenAPI specification

This is a community developed OpenAPI specification for the **tado API v2**
as available on https://my.tado.com/api/v2.
It is based on the experiences of private persons who are experimenting
with the API to create their own solutions. 

This API specification is not maintained or officially published by the tado company.
The contents of this API specification is a best-effort product.
There are no guarantees that this API specification is a complete and 
correct representation of the API as provided by tado.

## Versioning
As this is a community managed OpenAPI specification for an API which is 
actually offered by another company, a versioning scheme is used
which expresses the date on which the specification was modified.

The version format is `v2.<year>.<month>.<day>.<daily releasenumber>`.

The daily releasenumber is basically always `0`, unless we are releasing
multiple versions on the same day.

## Swagger UI
Visit https://kritsel.github.io/tado-openapispec-v2/ to see a visual representation
of the API in a Swagger UI.

Unfortunately you cannot execute API requests via the Swagger UI, 
due to cross-site scripting protection.

# About tado
Tado is a german based company which offers a smart thermostat solution
to control heating and air-conditioning in your home.

Tado (https://www.tado.com/) sells hardware and services, 
which can be controlled and used via a mobile app. 
This mobile app uses the very same API which is documented here.

# OAuth authentication
The tado API only supports OAuth 2.0 authentication. There are four pieces
of information you need to authenticate:
* username: the username of your tado account
* password: the password of your tado account
* client id: `public-api-preview` (as mentioned in [1])
* client secret: `4HJGRffVR8xb3XdEUQpjgZ1VplJi6Xgw` (a publicly shared 'secret' as mentioned in [1])

Here are some resources to help you to successfully authenticate
to the API.

* [1] https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
* [2] https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/
* [3] https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/

## About `client_id` and `client_secret`
Please note that the official tado article [1] explicitly states
"Also, feel free to share the client credentials with other developers.
That way we will be able to easily distinguish our own apps from
third party developers." 
So use the client id and client secret as provided on that page.

Article [2] mentions that you can visit https://my.tado.com/webapp/env.js to
obtain a valid `client_id` and `client_secret`. 
That is correct, but using that approach doesn't allow tado to distinguish
the traffic from their own apps from other traffic.

# Gratitudes
Without the groundwork done by Stephen C Phillips (https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/)
and Terence Eden (https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/)
I would have never started experimenting with the tado API,
and this tado OpenAPI specification would have never seen the light of day.

# Contribute
Feel free to submit a PR if you want to contribute to this
OpenAPI specification.