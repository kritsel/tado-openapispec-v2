![content](https://img.shields.io/badge/content-tado_API_definition-blue)
[![release](https://img.shields.io/github/v/release/kritsel/tado-openapispec-v2)](https://github.com/kritsel/tado-openapispec-v2/releases)
![OAS version](https://img.shields.io/badge/open_api_version-3.0.0-blue)
[![repo stars](https://img.shields.io/github/stars/kritsel/tado-openapispec-v2?style=plastic)](https://github.com/kritsel/tado-openapispec-v2/stargazers)
[![last commit](https://img.shields.io/github/last-commit/kritsel/tado-openapispec-v2)](https://github.com/kritsel/tado-openapispec-v2/commits/main/)
![open for contributors](https://img.shields.io/badge/open_for_contributors-yes-blue)
[![contributors](https://img.shields.io/github/contributors/kritsel/tado-openapispec-v2)](https://github.com/kritsel/tado-openapispec-v2/graphs/contributors)

*Full documentation is available in [this repo's Wiki](https://github.com/kritsel/tado-openapispec-v2/wiki).
This README is a summary.*

# About tado&ordm;, tado API and the tado API definition

## tado&ordm; and the tado API

tado&ordm; (https://www.tado.com/) is a german based company which offers a smart thermostat solution
to control heating and air-conditioning in your home.
They sell hardware and subscription based add-on services.
These can be controlled via a mobile app or a webapp on their website.

The apps use a tado API which is available at https://my.tado.com/api/v2.
tado&ordm; allows third-party developers to use this API for their own projects,
but they do not provide any kind of support or documentation.

## tado API definition

This repository contains a community managed API definition for the
**tado API v2** which is hosted on https://my.tado.com/api. It uses OpenAPI v3.0.0 as its specification language.
The API definition is based on the experience of software developers who are experimenting
with the API to create their own solutions.

This API definition is not maintained or officially published by the tado&ordm; company.
The contents of this API definition is a best-effort product.
There are no guarantees that this API definition is a complete and
correct representation of the actual API as exposed via https://my.tado.com/api/v2.

As the owner of the repo I also do not have any ties to the tado&ordm; company.
I simply like to create a space where knowledge about the tado API can be centralized in a standardized form,
so it becomes easier for software developers interested in home automation to use the API.


Links to the API definition in various formats:
 * **file in this GitHub repo**: [tado-openapispec-v2.yaml](tado-openapispec-v2.yaml)
 * **raw content**: https://raw.githubusercontent.com/kritsel/tado-openapispec-v2/latest/tado-openapispec-v2.yaml
 * **Swagger UI**: https://kritsel.github.io/tado-openapispec-v2/swagger.html

# API support for tado&ordm; X

The tado API hosted on https://my.tado.com/api has limited support for tado&ordm; X devices.
Tado&ordm; hosts a separate API geared towards tado&ordm; X.

More info is available at https://kritsel.github.io/tado-openapispec-v2/tado-x.html

# Acknowledgments
Without the groundwork done by **Stephen C. Phillips** 
(https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/)
and **Terence Eden** (https://shkspr.mobi/blog/2019/02/tado-api-guide-updated-for-2019/)
I would have never started experimenting with the tado API,
and this API definition would have never seen the light of day.

Additionally, the following GitHub members and repositories have been a great help:
 * [**mattdavis90** Matt Davis](https://github.com/mattdavis90): https://github.com/mattdavis90/node-tado-client
 * [**clambin** Christophe Lambin](https://github.com/clambin)


# Contribute
Feel free to submit a PR or create an issue if you want to contribute to this
API definition.

Special request for owners of the new tado X hardware: if you have knowledge about any https://my.tado.com/ endpoints
described in this definition which do not work as described, please create an issue (or submit a PR) with
documentation improvement suggestions.