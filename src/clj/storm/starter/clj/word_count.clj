(ns storm.starter.clj.word-count
  (:require [taoensso.carmine :as car])
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))

; REDIS related stuff
(def pool         (car/make-conn-pool)) ; See docstring for additional options
(def spec-server1 (car/make-conn-spec))

(defmacro wcar [& body] `(car/with-conn pool spec-server1 ~@body))

(defn lpop-from-redis[key-value]
  (wcar (car/lpop key-value)))

(defn rpush-from-redis[list-name value]
  (wcar (car/rpush list-name value)))


(defspout sentence-spout ["sentence"]
  [conf context collector]
  (let [sentences [
                          (lpop-from-redis "mylist")
                          (lpop-from-redis "mylist")
                          (lpop-from-redis "mylist")
                          ]
            ]
    (spout
     (nextTuple []
       (Thread/sleep 100)
       (emit-spout! collector [(rand-nth sentences)])         
       )
     (ack [id]
        ;; You only need to define this method for reliable spouts
        ;; (such as one that reads off of a queue like Kestrel)
        ;; This is an unreliable spout, so it does nothing here
        ))))



(defspout sentence-spout-parameterized ["word"] {:params [sentences] :prepare false}
  [collector]
  (Thread/sleep 500)
  (emit-spout! collector [(rand-nth sentences)]))

(defbolt split-sentence ["word"] [tuple collector]
  (let [words (.split (.getString tuple 0) " ")]
    (doseq [w words]
      (emit-bolt! collector [w] :anchor tuple))
    (ack! collector tuple)
    ))

(defbolt word-count ["word" "count"] {:prepare true}
  [conf context collector]
  (let [counts (atom {})]
    (bolt
     (execute [tuple]
       (let [word (.getString tuple 0)]
         (swap! counts (partial merge-with +) {word 1})
         (emit-bolt! collector [word (@counts word)] :anchor tuple)
         (ack! collector tuple)
         )))))

(defn mk-topology []

  (topology
   {"1" (spout-spec sentence-spout)
    "2" (spout-spec (sentence-spout-parameterized
                     ["the cat jumped over the door"
                      "greetings from a faraway land"])
                     :p 2)}
   {"3" (bolt-spec {"1" :shuffle "2" :shuffle}
                   split-sentence
                   :p 5)
    "4" (bolt-spec {"3" ["word"]}
                   word-count
                   :p 6)}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "word-count" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)
    ))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3}
   (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))

