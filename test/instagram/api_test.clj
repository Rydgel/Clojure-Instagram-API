(ns instagram.api-test
  (:use clojure.test
        instagram.api)
  (:import
    (instagram.api ApiContext)))


(deftest test-make-uri
  (is (= (make-uri (ApiContext. "https" "api.instagram.com" nil) "oauth/authorize")
         "https://api.instagram.com/oauth/authorize"))
  (is (= (make-uri (ApiContext. "https" "api.instagram.com" "v1") "users/self/feed")
         "https://api.instagram.com/v1/users/self/feed")))


(deftest test-sub-uri
  (is (= (subs-uri "https://api.instagram.com/v1/media/{:media_id}" {:media_id 3})
         "https://api.instagram.com/v1/media/3"))
  (is (= (subs-uri "https://api.instagram.com/v1/media/{:media_id}/comments/{:comment_id}" {:media_id 4, :comment_id 56})
         "https://api.instagram.com/v1/media/4/comments/56"))
  (is (thrown? Exception (subs-uri "https://api.instagram.com/v1/media/{:media_id}/comments/{:comment_id}" {:media_id "my123"}))))
