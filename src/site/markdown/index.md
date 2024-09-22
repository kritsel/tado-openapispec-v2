# About tado&ordm;, the tado API and this OpenAPI specification

## tado&ordm;

https://www.tado.com/

tado&ordm; is a german based company which offers a smart thermostat solution
to control heating and air-conditioning in your home.

tado&ordm; sells hardware and subscription based add-on services.
These can be controlled via a mobile app or an on-line app on their website.

## tado API
The tado&ordm; website and mobile apps use the tado API which is available at
https://my.tado.com/api/v2.

tado&ordm; allows third-party developers to use their API as well,
but they do not provide any kind of support or documentation.

## tado OpenAPI specification

This repository contains a community managed OpenAPI specification for the
**tado API v2**.
It is based on the experience of software developers who are experimenting
with the API to create their own solutions.

This API specification is not maintained or officially published by the tado&ordm; company.
The contents of this API specification is a best-effort product.
There are no guarantees that this API specification is a complete and
correct representation of the actual API as operated by tado&ordm;.

# Specification characteristics

## Versioning
As this is a community managed OpenAPI specification for an API which is
actually controlled by another company (being tado&ordm;),
the specification grows and matures gradually based on the discoveries made by
software developers who are reverse-engineering the API.

The changes and releases of this *specification* do not represent the changes
in the API itself.
They merely represent the changes in our understanding of the API.

Because of this, a versioning scheme is adopted which expresses the date
on which the *specification* was modified.

The version format is `v2.<year>.<month>.<day>.<daily release number>`.

(the daily release number is basically always `0`, unless we are releasing
multiple versions on the same day)

## Conventions

The following conventions are used to document the tado API:

1. Object identifiers
   1. All object identifiers have their own explicitly identified type (e.g. HomeId, ZoneId)  
      to ensure consistency wherever they are used
   2. Numerical object identifiers which seem to be globally unique are defined as `format: int64`
      to prevent potential future surprises when tado would decide to use such high values. 
   3. Numerical object identifiers which are only unique within the scope of a single home
      have no format defined, meaning they are `int32` by default.
2. Schema modelling
   1. Schema elements are modelled in a way that maximizes element re-use.
   2. When the input for a PUT operation on a resource differs from the response of the associated GET operation
      (e.g. not all fields of the response object are expected in the input object), 
      a specific `<some object>Input` schema element is defined. 
3. Enums
   1. When a property's type appears to be an enum and we're quite confident that we know all possible values, 
      the field's type is defined as an enum.
   2. When we're not sure that we know all possible values of a property type which is expected to be en enum
      it is defined as a `string` field
4. Responses
   1. All operations specify the 401 (unauthorized, meaning no valid oauth access token) and 
      403 (forbidden, meaning your access token is OK but you're not allowed to call the operation) response codes
   2. Where a 404 is known to be able to occur it is specified, but there is no guarantee that the
      specification is complete on this aspect. 

## Limitations
Not all functionality in tado's webapp or mobile apps is supported via this tado API.

When you use the webapp while keeping an eye on Network traffic via your browser's
developer tools, you can see other tado APIs scrolling by as well.

* https://acme.tado.com : additional air comfort information (display texts, visuals to use, outdoor air quality)
* https://auth.tado.com/: manage your account information like name, email and password
* https://energy-insights.tado.com: functionality behind Energy IQ
* https://ivar.tado.com/graphql: retrieve brand and boiler model information to help the user select their boiler type
* https://nibbler.tado.com/graphql: registering new devices
* https://minder.tado.com: get incidents information
* https://susi.tado.com: get skill information (related to paid Auto-Assist subscription)
* https://tariff-experience.tado.com

