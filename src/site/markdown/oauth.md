# OAuth 2.0 authentication

## Basics
The only authentication mechanism supported by the tado API is OAuth 2.0.

tado&ordm; only supports OAuth 2.0 authentication via their own authentication server;
the API does not support third party authentication methods.

There are several pieces of information you need to authenticate via OAuth 2.0:
* **client id**: `public-api-preview` (as mentioned in [1])
* **client secret**: `4HJGRffVR8xb3XdEUQpjgZ1VplJi6Xgw`
  (a publicly shared 'secret' as mentioned in [1])
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
