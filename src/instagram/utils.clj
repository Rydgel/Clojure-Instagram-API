(ns instagram.utils)

(defn assert-throw
  "If the supplied arg is nil, throw with the exception text provided"
  [val msg]

  (or val (throw (Exception. msg))))


(defn transform-map
  "Transforms the k/v pairs of a map using a supplied transformation function"
  [map-to-transform & {:keys [key-trans val-trans] :or {key-trans identity val-trans identity}}]

  (if map-to-transform
    (into {} (map (fn [[k v]] [(key-trans k) (val-trans v)]) map-to-transform))))


(defn partition-map
  "Partitions a map, depending on a predicate, returning a vector of maps of passes and fails"
  [map-to-partition pred]

  (loop [passes {}
         fails {}
         m map-to-partition]
    (if (empty? m) [passes fails]
        (let [[k v] (first m)]
          (if (pred [k v])
            (recur (assoc passes k v) fails (rest m))
            (recur passes (assoc fails k v) (rest m)))))))


(defn get-file-ext
  "Retrieves the file extension portion from the filename"
  [filename]

  (let [dot-pos (.lastIndexOf filename ".")
        result (.substring filename (inc dot-pos))]
    (if (and (>= dot-pos 0) (> (count result) 0))
      result)))


(defn classpath-file
  "This loads a file from the classpath and returns an input stream"
  [file-name]

  (assert-throw (.. (Thread/currentThread)
                    (getContextClassLoader)
                    (getResource file-name)
                    (getFile))
                (format "Cannot find file %s" file-name)))
