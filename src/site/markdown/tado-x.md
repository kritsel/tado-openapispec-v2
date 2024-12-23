# tado&ordm; X 

## Basics
In may 2024 tado&ordm; introduced their new device generation named **X** 
(previous generations are known as V2, V3 or V3+).
Devices belonging to the X generation cannot be mixed with devices from previous generations as completely
different technology is used for the communication between devices.

Regardless of which generation of tado&ordm; devices you use in your home, you use the same mobile of webapp.

When the home you control via the app is equipped with tado&ordm; X devices, 
the app uses the https://my.tado.com/api/v2 API (which is the API to which this website is dedicated) for some
functionality like managing users and mobile devices.
**However, the more fundamental features like managing rooms and tado devices are supported via another tado API
which is hosted on https://hops.tado.com.** The functionality of that API is not defined in the API definition presented here.

More info on this topic can be found here:

* https://community.tado.com/en-gb/discussion/25101/tado-x-api
* https://github.com/gedhi/tadox-postman-collection

## Why can't you create an API definition for https://hops.tado.com as well?

As the owner of the https://github.com/kritsel/tado-openapispec-v2 repo, I heavily depend on my own tado enabled home 
with V3+ tado devices.
I use my own home to test the API definition for the https://my.tado.com/api/v2 API, and take pride in maintaining
an API definition which is as accurate as possible.

Without access to a tado enabled home with generation X devices, it is really difficult to take ownership of
an API definition for an API I cannot test myself.

