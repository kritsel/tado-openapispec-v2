# tado API definition features

## Scope
Only supports the API endpoints as exposed by https://my.tado.com/api/v2.

The tado apps use multiple other APIs hosted on `<something else>.tado.com`, but those APIs are not in scope here.

## Versioning
This website is about a community managed API definition for an API which is
actually controlled by another company (being tado&ordm;).
The API definition grows and matures gradually based on the discoveries made by
software developers who are reverse-engineering the API.

The changes and releases of this *definition* do not represent the changes
in the API itself.
They merely represent the changes in *our understanding* of the API.

Because of this, a versioning scheme is used which expresses the date
on which the *definition* was modified.

The version format is `v2.<year>.<month>.<day>.<daily release number>`.

(the daily release number is usually `0`, unless multiple versions are released on the same day)

## Conventions

The following conventions are used to document the tado API:

1. **Paths and operations**
   1. The goal is to tag each operation with just a single tag. 
      Tagging operations with multiple tags can create confusion when visualizing the API definition via a Swagger UI,
      and it can have unexpected behaviour in API client generators that use OAS definitions as input.
   2. Each operation must have an `operationId` (which is usually `getSomething` or `setSomething`)
2. **Responses**
   1. All operations specify the 401 (unauthorized, meaning no valid oauth access token) and
      403 (forbidden, meaning your access token is OK, but you're not allowed to call the operation) response codes
   2. Where a 404 is known to be able to occur, it is specified; but there is no guarantee that the
      API definition is complete on this aspect.
3. **Schema modelling**
   1. Schema elements are modelled in a way that maximizes element re-use.
   2. When the input for a PUT operation on a resource differs from the response of the associated GET operation
      (e.g. not all fields of the response object are expected in the input object), 
      a specific `<some object>Input` schema element is defined. 
4. **Object identifiers**
   1. All object identifiers have their own explicitly identified type (e.g. HomeId, ZoneId)  
      to ensure consistency wherever they are used
   2. Numerical object identifiers which seem to be globally unique are defined as `format: int64`
      to prevent potential future surprises when tado would decide to use such high values.
   3. Numerical object identifiers which are only unique within the scope of a single home
      have no format defined, meaning they are `int32` by default.
5. **Enums**
   1. When a property's type appears to be an enum and there's a reasonable level of certainty that all possible values are known, 
      the field's type is defined as an enum.
   2. When there's uncertainty about all possible values of a property  
      (which is expected to be en enum in the actual API implementation)
      it is defined as a `string` field
6. **Keep API client code generators in mind** 
   1. When doubting what the best way is to model a certain part of the API, model it in a way that helps
      developers who use API client code generators.
   2. Be careful when making changes to the *API definition* which do not actually indicate changes in the *API*
      (like changing an operationId, or the name of a schema component), 
      as these changes can impact method names or signatures of generated code, which can break solutions are build
      on top of generated API clients.
   
## Limitations
Not all functionality in tado's apps are supported via this tado API.

When you use the webapp while keeping an eye on network traffic via your browser's
developer tools, you can see other tado APIs scrolling by as well.

* https://acme.tado.com : additional air comfort information (display texts, visuals to use, outdoor air quality)
* https://auth.tado.com/: manage your account information like name, email and password
* https://energy-insights.tado.com: functionality behind Energy IQ
* https://hops.tado.com: support for tado's X line of products
* https://ivar.tado.com/graphql: retrieve brand and boiler model information to help the user select their boiler type
* https://nibbler.tado.com/graphql: registering new devices
* https://minder.tado.com: get incidents information
* https://susi.tado.com: get skill information (related to paid Auto-Assist subscription)
* https://tariff-experience.tado.com

## Tado X support

See [tado X](tado-xmd)

## Quality control

An automated weekly test determines that the API definition is still in line with the actual API
(tado can introduce both trivial and breaking changes to their API at any given time!)
See [Automated API test](api-test.html) for more details.



