# tado&ordm; X 

## Basics
In may 2024 tado&ordm; introduced their new product generation named **X** 
(previous generations are known as V2, V3 or V3+).
Devices belonging to the X generation cannot be combined with devices from previous generations as a
different technology is used for the communication between devices.

Regardless of which generation of tado&ordm; devices you use in your home, you use the same mobile or webapp to control them

When the home you control via the app is equipped with tado&ordm; X devices, 
the app uses the https://my.tado.com/api/v2 API (which is the API to which this website is dedicated) for some
functionality, like managing users and mobile devices.

**However, the more fundamental features like managing rooms and tado devices are supported via another tado API
which is hosted on https://hops.tado.com.** The functionality of that API is not defined in the API definition documented here.

More info on tado API support for tado&ordm; X can be found here:

* https://community.tado.com/en-gb/discussion/25101/tado-x-api
* https://github.com/gedhi/tadox-postman-collection
* https://github.com/aweddell/PyTadoX/tree/TadoX-Support (fork of https://github.com/chrism0dwk/PyTado)
* https://github.com/svobop/PyTado/tree/master/PyTado (also a fork of https://github.com/chrism0dwk/PyTado)

## Why can't you create an API definition for https://hops.tado.com as well?

As the owner of the https://github.com/kritsel/tado-openapispec-v2 repo, I heavily depend on my own **tado&ordm; V3+**
enabled home to test the correctness of the API definition 
and maintain an API definition which is as accurate as possible.


Without access to a **tado&ordm; X** enabled home, I cannot take ownership of
an API definition geared towards tado&ordm; X as I won't be able to test it myself.

