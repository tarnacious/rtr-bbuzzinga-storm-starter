(ns utils.wikiclicks
  (:use [clojure.java.io])
  (:refer-clojure :exclude [send])
  (:require [utils.zhelpers :as mq])
  (:import [java.util Random]))


(defn distribute [filename]
  (let [socket (-> 1 mq/context (mq/socket mq/rep))]
    (mq/bind socket "tcp://*:5556")
    (with-open [rdr (reader filename)]
      (doseq [line (line-seq rdr)]
        (let [req (mq/recv-str socket)]
          (mq/send socket line))
          )
      )
    (println "DONE")
    ))

