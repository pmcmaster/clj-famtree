(ns famtree.data-test-helper
  (:require [famtree.load]
            [famtree.data-cache]))

;; Allows the data source (and data that has been read) to be switched out for
;; testing purposes

(defmacro do-with-test-data
  "Temporarily clear the data-cache, and redirect the source of data files
  to the data_sample directory. Restores the usual data source and cache after
  execution."
  [& body]
  `(with-redefs [famtree.load/data-source-dir "data_sample/"
                 famtree.data-cache/data-cache (atom {})]
     ~@body))

