# Instagram-api

This is an Instagram API wrapper based on the Clojure http.async.client library.
It currently support all endpoints except everything related to real-time subscriptions.

## Special thanks to theses libraries and authors

* [http.async.client](https://github.com/neotyk/http.async.client) by Hubert Iwaniuk
* [twitter-api](https://github.com/adamwynne/twitter-api)

## Leiningen

Just add the following to your project.clj file in the _dependencies_ section:

```
[instagram-api "0.1.1"]
```

## Examples

```clojure
(ns yournamespace
  (:use
    instagram.oauth
    instagram.callbacks
    instagram.callbacks.handlers
    instagram.api.endpoint)
  (:import
    (instagram.callbacks.protocols SyncSingleCallback)))

(def ^:dynamic *creds* (make-oauth-creds *client-id*
                                         *client-secret*
                                         *redirect-uri*))


; Generate the authorization url
  (def ^:dynamic *auth-url* (authorization-url *creds* "likes comments relationships"))

; Exchange the code to get the user's access token
  (let [access-token (-> (get-access-token *creds* "code-from-IG") :body :access_token)]
    ; do stuff with access-token, save it somewhere etc.
    (println access-token))

; You can make unauthentificated calls without access token, but you
; still needs to send your app credentials. Some API calls won't work without
; an access token, check the Instagram documentation.

(get-popular :oauth *creds*)

; The same API call, to get popular photos, but with the user’s access token.

(get-popular :access-token *access-token*)

; Some endpoints require parameters, see instagram.api.endpoint and the Instagram
; documentation. Here are some examples:

; Search a user
(search-users :access-token *access-token* :params {:q "rydgel"})

; Get medias from an user
(get-user-medias :access-token *access-token* :params {:user_id "36783"})

```

## Building

Clone the project and run `lein jar`, and the library will be build into a jar.

```
$ lein jar
Created /Users/rydgel/Projects/Clojure/instagram-api/target/instagram-api-0.1.1.jar
```

## Tests

There is a file in _resources/test.config_ that you need to configure with your data.
You can create an app in the [developer Instagram website](http://instagram.com/developer).
Then you can use leiningen to test the library, be patient it makes real API calls and
won’t be instant.

```
$ lein test
lein test instagram.api-test
lein test instagram.api.endpoint-test
lein test instagram.callbacks-test
lein test instagram.core-test
lein test instagram.creds
lein test instagram.request-test
lein test instagram.utils-test
Ran 36 tests containing 93 assertions.
0 failures, 0 errors.
```

## TODO

* Subscriptions stuff.

## License

Copyright © 2012 Jérôme Mahuet

Follow [@phollow](https://twitter.com/phollow) if you want to stay up-to-date or
ask something.

Distributed under the Eclipse Public License, the same as Clojure.
