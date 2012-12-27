(ns instagram.core-test
  (:use clojure.test
        instagram.core))

(deftest test-fix-keyword
  (is (= (#'instagram.core/fix-keyword :my-test) :my_test)))


(deftest test-fix-colls
  (is (= (#'instagram.core/fix-colls :a) :a))
  (is (= (#'instagram.core/fix-colls [1 2 3]) "1,2,3")))


(deftest test-add-form-content-type
  (let [headers {:Content-Length 32}
        headers-fix (#'instagram.core/add-form-content-type headers)]
    (is (= (-> headers-fix :content-type) "application/x-www-form-urlencoded"))))


(deftest test-encode-params
    (is (= (encode-params {"test" "1"}) "test=1"))
    (is (= (encode-params {"test" "1" "test2" 2}) "test2=2&test=1")))


(deftest test-make-query-string
  (is (= (make-query-string
          "http://api.instagram.com" {"test" "1" "test2" 2})
          "http://api.instagram.com?test2=2&test=1")))

