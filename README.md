# About tado, the API and this OpenAPI specification

## Tado
Tado is a german based company which offers a smart thermostat solution
to control heating and air-conditioning in your home.

Tado (https://www.tado.com/) sells hardware and subscription based add-on services.
These can be controlled via a mobile app or an on-line app on their website.

## Tado API
The tado website and mobile apps use the tado API which is available at
https://my.tado.com/api/v2.

Tado allows third-party developers to use their API as well, 
but they do not provide any kind of support or documentation. 

# The tado OpenAPI specification

This repository contains a community developed OpenAPI specification for the
**tado API v2**.
It is based on the experiences of software developers who are experimenting
with the API to create their own solutions. 

This API specification is not maintained or officially published by the tado company.
The contents of this API specification is a best-effort product.
There are no guarantees that this API specification is a complete and 
correct representation of the actual API as provided by tado.

## Versioning
As this is a community managed OpenAPI specification for an API which is 
actually controlled by another company, the specification grows gradually 
based on the discoveries made by software developers using the API. 

The changes and releases of this *specification* do not represent the changes 
in the API itself. 
They merely represent the changes in our understanding of the API.

Because of this, a versioning scheme is adopted which expresses the date 
on which the *specification* was modified. 

The version format is `v2.<year>.<month>.<day>.<daily release number>`.

(the daily release number is basically always `0`, unless we are releasing
multiple versions on the same day)

## Swagger UI
Visit https://kritsel.github.io/tado-openapispec-v2/ to see a visual representation
of the API in a Swagger UI.

Unfortunately you cannot execute API requests via the Swagger UI, 
due to cross-site scripting protection:
the Swagger UI is basically a javascript application hosted by github.io,
and it is not allowed to make calls to the tado.com host.

## Link to the specification yaml file
If you need a direct link to the specification yaml file 
e.g. as input for a client code generator you can use 
`https://raw.githubusercontent.com/kritsel/tado-openapispec-v2/<version tag>/tado-openapispec-v2.yaml`

E.g. https://raw.githubusercontent.com/kritsel/tado-openapispec-v2/v2.2024.08.01.3/tado-openapispec-v2.yaml

# OAuth 2.0 authentication
The only authentication mechanism supported for the tado API is OAuth 2.0.

Tado only supports OAuth 2.0 authentication via their own authentication server;
the API does not support third party authentication methods.

There are several pieces of information you need to authenticate via OAuth 2.0:
* **client id**: `public-api-preview` (as mentioned in [1])
* **client secret**: `4HJGRffVR8xb3XdEUQpjgZ1VplJi6Xgw` (a publicly shared 'secret' as mentioned in [1])
* **authorization grant type**: password
* **token URI**: https://auth.tado.com/oauth/token
* **username**: the username of your tado account
* **password**: the password of your tado account

_(the tado OAuth 2.0 set-up does not use scopes)_

Here are some resources to help you to successfully authenticate to the tado API.

* [1] https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
* [2] https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/
* [3] https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/
* [4] https://community.tado.com/en-gb/discussion/8/tado-api/p1

## About `client_id` and `client_secret`
Please note that the official tado article [1] explicitly states
"Also, feel free to share the client credentials with other developers.
That way we will be able to easily distinguish our own apps from
third party developers." 
So use the client id and client secret as provided on that page.

Article [2] mentions that you can visit https://my.tado.com/webapp/env.js to
obtain a valid `client_id` and `client_secret`. 
The approach described on that page will work, but it will not allow tado to 
distinguish the traffic from their own apps from other traffic.

# Gratitudes
Without the groundwork done by Stephen C. Phillips (https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/)
and Terence Eden (https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/)
I would have never started experimenting with the tado API,
and this tado OpenAPI specification would have never seen the light of day.

# Contribute
Feel free to submit a PR if you want to contribute to this
OpenAPI specification.