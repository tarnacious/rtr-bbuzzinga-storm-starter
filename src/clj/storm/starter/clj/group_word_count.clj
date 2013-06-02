(ns storm.starter.clj.group-word-count
  (:import [backtype.storm StormSubmitter LocalCluster Constants])
  (:require [utils.zhelpers :as mq])
  (:use [backtype.storm clojure config]))

(defspout zmq-line-spout ["words"]
  [conf context collector]
  (let [subscriber (-> 1 mq/context (mq/socket mq/sub))]
    (spout
     (nextTuple []
       (mq/connect subscriber "tcp://localhost:5556")
       (mq/subscribe subscriber)
       (let [string (mq/recv-str subscriber)]
         (emit-spout! collector [string])
       ))
     (ack [id]
        ;; You only need to define this method for reliable spouts
        ;; (such as one that reads off of a queue like Kestrel)
        ;; This is an unreliable spout, so it does nothing here
        ))))

(defspout test-line-spout ["words"]
  [conf context collector]
  (let [words ["pink" "purple" "elephant" "dance" "beached"]]
    (spout
     (nextTuple []
       (Thread/sleep 100)
       (emit-spout! collector [(rand-nth words)])
       )
     (ack [id]
        ;; You only need to define this method for reliable spouts
        ;; (such as one that reads off of a queue like Kestrel)
        ;; This is an unreliable spout, so it does nothing here
        ))))

(defn isTick [tuple]
  (let [sourceComponent (.getSourceComponent tuple)
        systemComponent (Constants/SYSTEM_COMPONENT_ID)
        sourceStream (.getSourceStreamId tuple)
        systemStream (Constants/SYSTEM_TICK_STREAM_ID)]
    (and (.equals sourceComponent systemComponent)
         (.equals sourceStream systemStream))))


(defbolt word-bolt ["word"] {
  :conf {"topology.tick.tuple.freq.secs", 2}
  :prepare true }
  [conf tuple collector]
   (let [counts (atom {})]
     (bolt
       (execute [tuple]
         (cond (isTick tuple)
             (emit-bolt! collector [(deref counts)])
         :else
         (do (let [word (.getString tuple 0)]
             (swap! counts (partial merge-with +) {word 1})
               ))
               )))))

(defbolt aggregate-bolt ["word-counts"] {:prepare true}
  [conf tuple collector]
   (let [counts (atom {})]
     (bolt
       (execute [tuple]
          (let [dict (.getValueByField tuple "word")]
            (swap! counts merge dict)
            (println counts)
           ))
       )))

(defn mk-topology []
  (topology
   {"1" (spout-spec zmq-line-spout)}
   {"2" (bolt-spec {"1" ["words"]} word-bolt :p 1)
    "3" (bolt-spec {"2" :shuffle} aggregate-bolt :p 1)}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "group-word-count" {TOPOLOGY-DEBUG true} (mk-topology))
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

