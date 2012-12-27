(ns instagram.utils-test
  (:use clojure.test
        instagram.utils
        instagram.api.endpoint
        instagram.creds))

(deftest test-assert-throw
  (is (thrown? Exception (assert-throw nil "test exception"))))


(deftest test-transform-map
  (is (= (transform-map {:a 0 :b 1 :c 2 :d 3} :key-trans name) {"a" 0, "b" 1, "c" 2, "d" 3}))
  (is (= (transform-map {:a 0 :b 1 :c 2 :d 3} :val-trans inc) {:a 1 :b 2 :c 3 :d 4})))


(deftest test-partition-map
  (is (= (partition-map {:a 1 :b 2 :c 3 :d 4} (comp even? second)) [{:d 4, :b 2} {:c 3, :a 1}]))
  (is (= (partition-map {:a 1 :b 2 :c 3 :d 4} (comp (partial < 0) second)) [{:d 4, :c 3, :b 2, :a 1} {}]))
  (is (= (partition-map {} even?) [{} {}])))


(deftest test-get-file-ext
  (is (= (get-file-ext "truc.txt") "txt"))
  (is (nil? (get-file-ext "truc")))
  (is (= (get-file-ext "truc/was/here.txt") "txt"))
  (is (nil? (get-file-ext "truc/was/here.ext."))))


(defmacro is-async-200
  "Checks to see if the response is HTTP return code 200, and then cancels it"
  [fn-name & args]

  `(let [response# (~fn-name :access-token (get-test-access-token) ~@args)]
     (try
       (try (is (= (:code (ac/status response#)) 200))
            (finally ((:cancel (meta response#)))))
       (catch java.util.concurrent.CancellationException e# nil))))


(defmacro is-http-code
  "Checks to see if the response is a specific HTTP return code"
  [code fn-name & args]

  `(is (= (get-in (~fn-name :access-token (get-test-access-token) ~@args) [:status :code]) ~code)))


(defmacro is-200
  "Checks to see if the response is HTTP 200"
  [fn-name & args]

  `(is-http-code 200 ~fn-name ~@args))


(defmacro is-http-code-unauth
  "Checks to see if the response is a specific HTTP return code"
  [code fn-name & args]

  `(is (= (get-in (~fn-name :oauth (make-test-creds) ~@args) [:status :code]) ~code)))


(defmacro is-200-unauth
  "Checks to see if the response is HTTP 200 (without access token)"
  [fn-name & args]

  `(is-http-code-unauth 200 ~fn-name ~@args))


(defn poll-until-no-error
  "Repeatedly tries the poll instruction, for a maximum time, or until the error disappears"
  [poll-fn & {:keys [max-timeout-ms wait-time-ms]
              :or {max-timeout-ms 60000 wait-time-ms 10000}} ]

  (loop [curr-time-ms 0]
    (if (< curr-time-ms max-timeout-ms)
      (when-not (try (poll-fn) (catch Exception e nil))
        (Thread/sleep wait-time-ms)
        (recur (+ curr-time-ms wait-time-ms))))))


(defmacro with-setup-poll-teardown
  [id-name setup poll teardown & body]

  `(let [~id-name ~setup]
     (try (poll-until-no-error (fn [] ~poll))
          ~@body
          (finally ~teardown))))
