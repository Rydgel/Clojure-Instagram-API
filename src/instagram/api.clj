(ns instagram.api)

(defrecord ApiContext
    [^String protocol
     ^String host
     ^String version])


(defn make-api-context
  "Makes an API context. — Captain Obvious"
  ([protocol host] (ApiContext. protocol host nil))
  ([protocol host version] (ApiContext. protocol host version)))


(defn make-uri
   "Makes a uri from a supplied protocol, site, version and resource-path"
   [^ApiContext context
    ^String resource-path]

   (let [protocol (:protocol context)
         host (:host context)
         version (:version context)]
      (str protocol "://" host "/" (if version (str version "/")) resource-path)))


(defn subs-uri
  "Substitutes parameters for tokens in the uri"
  [uri params]

  (loop [matches (re-seq #"\{\:(\w+)\}" uri)
         ^String result uri]
    (if (empty? matches) result
        (let [[token kw] (first matches)
              value (get params (keyword kw))]
          (if-not value (throw (Exception. (format "%s needs :%s param to be supplied" uri kw))))
          (recur (rest matches) (.replace result token (str value)))))))
