(ns instagram.api.endpoint
  (:use
    instagram.core
    instagram.api
    instagram.callbacks)
  (:import
    (instagram.api ApiContext)
    (instagram.oauth OauthCredentials)))

(def ^:dynamic *oauth-api* (make-api-context "https" "api.instagram.com" nil))
(def ^:dynamic *rest-api* (make-api-context "https" "api.instagram.com" "v1"))


(defmacro def-instagram-restful-method
  "Defines a synchronous, single method using the supplied api context"
  [name action resource-path & rest]

  `(def-instagram-method ~name ~action ~resource-path :api ~*rest-api* :callbacks (get-default-callbacks :sync :single) ~@rest))

; Oauth Endpoints
(defn authorization-url
  "Create the authorization url"
  [^OauthCredentials *creds*
   ^String scope]

  (make-query-string "https://instagram.com/oauth/authorize"
                     {"client_id" (:client-id *creds*)
                      "redirect_uri" (:redirect-uri *creds*)
                      "response_type" "code"
                      "scope" scope}))

(def-instagram-restful-method access-token :post "oauth/access_token" :api *oauth-api*)

(defn get-access-token
  "Get the access token with the supplied code"
  [^OauthCredentials *creds*
   ^String code]

  (access-token :body {
    :code code
    :grant_type "authorization_code"
    :client_id (-> *creds* :client-id)
    :client_secret (-> *creds* :client-secret)
    :redirect_uri (-> *creds* :redirect-uri)}))


; User Endpoints
(def-instagram-restful-method get-user :get "users/{:user_id}")
(def-instagram-restful-method get-current-user-feed :get "users/self/feed")
(def-instagram-restful-method get-user-medias :get "users/{:user_id}/media/recent")
(def-instagram-restful-method get-current-user-liked-medias :get "users/self/media/liked")
(def-instagram-restful-method search-users :get "users/search")

; Relationship Endpoints
(def-instagram-restful-method get-followings :get "users/{:user_id}/follows")
(def-instagram-restful-method get-followers :get "users/{:user_id}/followed-by")
(def-instagram-restful-method get-current-user-requested-by :get "users/self/requested-by")
(def-instagram-restful-method get-user-relationship :get "users/{:user_id}/relationship")
(def-instagram-restful-method change-user-relationship :post "users/{:user_id}/relationship")

; Media Endpoints
(def-instagram-restful-method get-media :get "media/{:media_id}")
(def-instagram-restful-method search-medias :get "media/search")
(def-instagram-restful-method get-popular :get "media/popular")

; Comment Endpoints
(def-instagram-restful-method get-comments :get "media/{:media_id}/comments")
(def-instagram-restful-method post-comment :post "media/{:media_id}/comments")
(def-instagram-restful-method remove-comment :delete "media/{:media_id}/comments/{:comment_id}")

; Like Endpoints
(def-instagram-restful-method get-likes :get "media/{:media_id}/likes")
(def-instagram-restful-method post-like :post "media/{:media_id}/likes")
(def-instagram-restful-method remove-like :delete "media/{:media_id}/likes")

; Tag Endpoints
(def-instagram-restful-method get-tag :get "tags/{:tag_name}")
(def-instagram-restful-method get-tagged-medias :get "tags/{:tag_name}/media/recent")
(def-instagram-restful-method search-tags :get "tags/search")

; Location Endpoints
(def-instagram-restful-method get-location :get "locations/{:location_id}")
(def-instagram-restful-method get-location-medias :get "locations/{:location_id}/media/recent")
(def-instagram-restful-method search-location :get "locations/search")

; Geography Endpoints
; TODO: subscriptions stuff.
