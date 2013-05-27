(ns utils.redis
    (:use [utils.redis])
    (:import (java.io BufferedReader FileReader)))

(defn process-file [file-name line-func line-acc]
  (with-open [rdr (BufferedReader. (FileReader. file-name))]
    (reduce line-func line-acc (line-seq rdr))))

(defn process-line [acc line]
  (rpush-redis "wikiclicks" line))