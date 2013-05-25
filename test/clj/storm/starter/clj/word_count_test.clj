(ns storm.starter.clj.word-count-test
  (:require [taoensso.carmine :as car])
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))
