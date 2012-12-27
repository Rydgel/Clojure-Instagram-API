(ns instagram.callbacks-test
  (:use
    clojure.test
    instagram.callbacks
    instagram.callbacks.protocols
    instagram.callbacks.handlers)
  (:import
    (instagram.callbacks.protocols SyncSingleCallback SyncStreamingCallback
                                   AsyncSingleCallback AsyncStreamingCallback)))

(deftest test-sync-single-protocol
  (let [s (SyncSingleCallback. identity identity identity)]
    (is (= (get-async-sync s) :sync))
    (is (= (get-single-streaming s) :single))
    (is (= ((:on-success s) "test") "test"))
    (is (= ((:on-failure s) "test") "test"))
    (is (= ((:on-exception s) "test") "test"))))


(deftest test-sync-streaming-protocol
  (let [s (SyncStreamingCallback. identity identity identity)]
    (is (= (get-async-sync s) :sync))
    (is (= (get-single-streaming s) :streaming))
    (is (= ((:on-bodypart s) "test") "test"))
    (is (= ((:on-failure s) "test") "test"))
    (is (= ((:on-exception s) "test") "test"))))



(deftest test-async-single-protocol
  (let [s (AsyncSingleCallback. identity identity identity)]
    (is (= (get-async-sync s) :async))
    (is (= (get-single-streaming s) :single))
    (is (= ((:on-success s) "test") "test"))
    (is (= ((:on-failure s) "test") "test"))
    (is (= ((:on-exception s) "test") "test"))))



(deftest test-async-streaming-protocol
  (let [s (AsyncStreamingCallback. identity identity identity)]
    (is (= (get-async-sync s) :async))
    (is (= (get-single-streaming s) :streaming))
    (is (= ((:on-bodypart s) "test") "test"))
    (is (= ((:on-failure s) "test") "test"))
    (is (= ((:on-exception s) "test") "test"))))



(deftest test-get-default
  (is (= (type (get-default-callbacks :sync :single)) instagram.callbacks.protocols.SyncSingleCallback))
  (is (= (type (get-default-callbacks :sync :streaming)) instagram.callbacks.protocols.SyncStreamingCallback))
  (is (= (type (get-default-callbacks :async :single)) instagram.callbacks.protocols.AsyncSingleCallback))
  (is (= (type (get-default-callbacks :async :streaming)) instagram.callbacks.protocols.AsyncStreamingCallback)))
