(ns instagram.api.endpoint-test
  (:use
    clojure.test
    instagram.creds
    instagram.utils-test
    instagram.callbacks
    instagram.api.endpoint)
  (:import
    (java.io File)))


(deftest test-user
  (is-200 get-user :params {:user_id "36783"})
  (is-200 get-current-user-feed)
  (is-200 get-user-medias :params {:user_id "36783"})
  (is-200 get-current-user-liked-medias)
  (is-200 search-users :params {:q "rydgel"}))


(deftest test-relationship
  (is-200 get-followings :params {:user_id "36783"})
  (is-200 get-followers :params {:user_id "36783"})
  (is-200 get-current-user-requested-by)
  (is-200 get-user-relationship :params {:user_id "3"})
  (is-200 change-user-relationship :params {:user_id "3" :action "follow"}))


(deftest test-medias
  (is-200 get-media :params {:media_id "351141336761229388_36783"})
  (is-200 search-medias :params {:lat "48.858844" :lng "2.294351"})
  (is-200 get-popular))


(deftest test-comments
  (is-200 get-comments :params {:media_id "351141336761229388_36783"})
  ; TODO: test comments, but need commenting access from Instagram (need to email them)
  ; (is-200 post-comment :params {:media_id "174569304335573921_36783" :text "hello test comments"})
  )


(deftest test-likes
  (is-200 get-likes :params {:media_id "174569304335573921_36783"})
  (is-200 post-like :params {:media_id "174569304335573921_36783"})
  (is-200 remove-like :params {:media_id "174569304335573921_36783"}))


(deftest test-tags
  (is-200 get-tag :params {:tag_name "cats"})
  (is-200 get-tagged-medias :params {:tag_name "cats"})
  (is-200 search-tags :params {:q "cats"}))


(deftest test-locations
  (is-200 get-location :params {:location_id "1"})
  (is-200 get-location-medias :params {:location_id "1"})
  (is-200 search-location :params {:lat "48.858844" :lng "2.294351"}))


; unauth call (without access token, but with a client_id)
(deftest unauth-call
  (is-200-unauth get-user :params {:user_id "36783"})
  (is-200-unauth get-followings :params {:user_id "36783"})
  (is-200-unauth get-followers :params {:user_id "36783"})
  (is-200-unauth get-media :params {:media_id "351141336761229388_36783"})
  (is-200-unauth search-medias :params {:lat "48.858844" :lng "2.294351"})
  (is-200-unauth get-popular)
  (is-200-unauth get-comments :params {:media_id "351141336761229388_36783"})
  (is-200-unauth get-likes :params {:media_id "174569304335573921_36783"})
  (is-200-unauth get-tag :params {:tag_name "cats"})
  (is-200-unauth get-tagged-medias :params {:tag_name "cats"})
  (is-200-unauth search-tags :params {:q "cats"})
  (is-200-unauth get-location :params {:location_id "1"})
  (is-200-unauth get-location-medias :params {:location_id "1"})
  (is-200-unauth search-location :params {:lat "48.858844" :lng "2.294351"}))
