(ns instagram.creds
  (:use
    instagram.oauth)
  (:import
    (java.util Properties)))


(defn load-config-file
  "This loads a config file from the classpath"
  [file-name]
  (let [file-reader (.. (Thread/currentThread)
                        (getContextClassLoader)
                        (getResourceAsStream file-name))
        props (Properties.)]
    (.load props file-reader)
    (into {} props)))


(def ^:dynamic *config* (load-config-file "test.config"))


(defn assert-get
  "Get the value from the config, otherwise throw an exception detailing the problem"
  [key-name]

  (or (get *config* key-name)
      (throw (Exception. (format "Please define %s in the resources/test.config file" key-name)))))


; Getting the properties from the test.config file

(def ^:dynamic *client-id* (assert-get "client.id"))
(def ^:dynamic *client-secret* (assert-get "client.secret"))
(def ^:dynamic *redirect-uri* (assert-get "client.redirect-uri"))
(def ^:dynamic *access-token* (assert-get "user.access-token"))


(defn make-test-creds
  "Makes an Oauth structure that uses an app's credentials"
  []

  (make-oauth-creds *client-id*
                    *client-secret*
                    *redirect-uri*))


(defn get-test-access-token
  "Get the access token for the tests"
  []

  *access-token*)
