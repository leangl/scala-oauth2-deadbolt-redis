[![Build Status](https://travis-ci.org/lglossman/scala-oauth2-deadbolt-redis.svg?branch=master)](https://travis-ci.org/lglossman/scala-oauth2-deadbolt-redis)

# scala-oauth2-deadbolt-redis
Play framework template integrating Deadbolt plugin with an OAuth 2 server, backed by Redis.

# How does this work?
 
 It allows you to restrict actions to your routes and parts of your views using Deadbolt access rights based on the OAuth2 token scope.
 
 Let's say a client requested an access token with a "profile" scope, then you can restrict access to your controllers like this..
 
 Scala:
```scala
def inclusiveScopes = actionBuilder.RestrictAction("profile").defaultHandler() { implicit request =>
  ...
  Future(Ok)
}
```
 Java:
```java
@Restrict(@Group("profile"))
public Result singleScope() {
  return ok();
}
```

Also supports multiple scopes if the scope is comma separated:
```scala
def exclusiveScopes = actionBuilder.RestrictAction("posts", "profile").defaultHandler() { implicit request =>
  ...
  Future(Ok)
}
```

# More information

For more information see the project this template depends upon.

Deadbolt:
https://github.com/schaloner/deadbolt-2

OAuth2 scala server:
https://github.com/nulab/scala-oauth2-provider



# License

```
The MIT License (MIT)

Copyright (c) 2016 Leandro Glossman

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
