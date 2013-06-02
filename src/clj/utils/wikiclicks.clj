(ns utils.wikiclicks
  (:use [clojure.java.io])
  (:refer-clojure :exclude [send])
  (:require [utils.zhelpers :as mq])
  (:import [java.util Random]))


(defn distribute [filename]
  (let [publisher (-> 1 mq/context (mq/socket mq/pub))]
    (mq/bind publisher "tcp://*:5556")
    (with-open [rdr (reader filename)]
      (doseq [line (line-seq rdr)]
        (println line)
        (mq/send publisher line)))
      ))

