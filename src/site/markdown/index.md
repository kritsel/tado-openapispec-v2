# tado&ordm;, tado API and the tado API definition

## tado&ordm; and the tado API

tado&ordm; (https://www.tado.com/) is a german based company which offers a smart thermostat solution
to control heating and air-conditioning in your home. 
They sell hardware and subscription based add-on services.
These can be controlled via a mobile app or a webapp on their website.

The apps use a tado API which is available at https://my.tado.com/api/v2.
tado&ordm; allows third-party developers to use this API for their own projects,
but they do not provide any kind of support or documentation.

## tado API definition - community managed

This website is the documentation companion for GitHub repository https://github.com/kritsel/tado-openapispec-v2.
That repo is used to maintain a community managed API definition (based on the OpenAPI specification, a.k.a. Swagger)
for the **tado API v2** as exposed via https://my.tado.com/api/v2.

It is based on the experience of software developers who are experimenting
with the API to create their own solutions.

This API definition is not maintained or officially published by the tado&ordm; company.
The contents of this API definition is a best-effort product.
There are no guarantees that this API definition is a complete and
correct representation of the actual API as exposed via https://my.tado.com/api/v2.

As the owner of the repo I also do not have any ties to the tado&ordm; company. 
I simply like to create a space where knowledge about the tado API can be centralized in a standardized form,
so it becomes easier for software developers interested in home automation to use the API.

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
