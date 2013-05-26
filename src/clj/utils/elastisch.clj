(ns utils.elastisch
  (:require [clojurewerkz.elastisch.native :as es]))

(defn connect [host port]
    ; something like "http://127.0.0.1" and 9300
    (es/connect! [[host 9300]]))
