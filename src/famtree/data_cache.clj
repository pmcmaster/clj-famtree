(ns famtree.data-cache
  "Cache of records which are read from file")

(def data-cache
  "Store for data read from disk.
  Exact location which data is read from is controlled in famtree.load.
  Data is keyed by keyword, such as :birth"
  (atom {}))

(defn cached-or-load
  "Look in @record-cache for data associated with `cache-key`.
  If it's not present, generate data with load-fn, associate that with
  `cache-key` in the store, and finally return the loaded data"
  [cache-key load-fn]
  (if-let [cached-value (cache-key @data-cache)]
    cached-value
    (let [loaded-data (load-fn)]
      (println "Stored data (of" (count loaded-data)
               "records) into cache for" cache-key)
      (swap! data-cache assoc cache-key loaded-data)
      loaded-data)))

