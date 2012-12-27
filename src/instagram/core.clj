(ns instagram.core
  (:use
    instagram.oauth
    instagram.api
    instagram.callbacks
    instagram.request
    instagram.utils)
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac]
    [clojure.string :as string])
  (:import
    (clojure.lang Keyword PersistentArrayMap)
    (java.net URLEncoder)))


(defn- fix-keyword
  "Takes a parameter name and replaces the - with a _"
  [param-name]

  (keyword (.replace (name param-name) \- \_)))


(defn- fix-colls
  "Turns collections into their string, comma-sep equivalents"
  [val]

  (if (coll? val) (string/join "," val) val))


(defn- add-form-content-type
  "Adds a content type of url-encoded-form to the supplied headers"
  [headers]
  (merge headers
         {:content-type "application/x-www-form-urlencoded"}))


(def memo-create-client (memoize ac/create-client))


(defn default-client
  "Makes a default async client for the http comms"
  []
  (memo-create-client :follow-redirects false :request-timeout -1))


(defn encode-params
  "Encode params for a query string"
  [request-params]

  (let [encode #(URLEncoder/encode (str %) "UTF-8")
        coded (for [[n v] request-params] (str (encode n) "=" (encode v)))]
    (apply str (interpose "&" coded))))


(defn make-query-string
  "Makes a query string with a URL and params"
  [url params]

  (str url "?" (encode-params params)))


(defn- get-request-args
  "Takes uri, action and optional args and returns the final uri and http parameters for the subsequent call.
   Note that the params are transformed (from lispy -'s to x-header-style _'s) and added to the query. So :params
   could be {:screen-name 'blah'} and it be merged into :query as {:screen_name 'blah'}. The uri has the params
   substituted in (so {:id} in the uri with use the :id in the :params map). Also, the oauth headers are added
   if required."
  [^Keyword action
   ^String uri
   ^PersistentArrayMap arg-map]

  (let [params (transform-map (:params arg-map) :key-trans fix-keyword :val-trans fix-colls)
        body (:body arg-map)
        query (merge (:query arg-map) params)
        ; access_token shortcut
        query (if :access-token
                (into query {"access_token" (:access-token arg-map)}) query)
        ; requests with no access_token: ex popular page
        query (if :oauth
                (into query {"client_id" (-> (:oauth arg-map) :client-id)}) query)

        final-uri (subs-uri uri params)

        headers (merge (:headers arg-map))

        my-args (cond (or (= action :get)
                          (= action :delete)) (hash-map :query query :headers headers :body body)
                      (nil? body) (hash-map :headers (add-form-content-type headers) :body query)
                      :else (hash-map :query query :headers headers :body body))]

    {:action action
     :uri final-uri
     :processed-args (merge (dissoc arg-map :query :headers :body :params :client :api :callbacks)
                            my-args)}))


(defn http-request
  "Calls the action on the resource specified in the uri, signing with oauth in the headers
   you can supply args for async.http.client (e.g. :query, :body, :headers etc)."
  [^Keyword action
   ^String uri
   ^PersistentArrayMap arg-map]

  (let [client (or (:client arg-map) (default-client))
        callbacks (or (:callbacks arg-map)
                      (throw (Exception. "need to specify a callback argument for http-request")))
        request-args (get-request-args action uri arg-map)

        request (apply prepare-request-with-multi
                       (:action request-args)
                       (:uri request-args)
                       (apply concat (:processed-args request-args)))]

    (execute-request-callbacks client request callbacks)))


(defmacro def-instagram-method
  "Declares an Instagram method with the supplied name, HTTP verb and relative resource path.
   As part of the specification, it must have an :api and :callbacks member of the 'rest' list.
   From these it creates a uri, the api context and relative resource path. The default callbacks that are
   supplied, determine how to make the call (in terms of the sync/async or single/streaming)"
  [name action resource-path & rest]

  (let [rest-map (apply sorted-map rest)]

    `(defn ~name
       [& {:as args#}]

       (let [arg-map# (merge ~rest-map
                             args#)
             api-context# (assert-throw (:api arg-map#) "must include an ':api' entry in the params")
             uri# (make-uri api-context# ~resource-path)]

         (http-request ~action
                       uri#
                       arg-map#)))))
