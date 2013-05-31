(ns storm.starter.clj.group-word-count
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))

(defspout sentence-spout ["words"]
  [conf context collector]
  (let [words ["pink" "purple" "elephant" "dance" "rockstart"]]
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


(defbolt word-bolt ["word"] {:prepare true}
  [conf tuple collector]
   (let [counts (atom {})]
     (bolt
       (execute [tuple]
        (let [word (.getString tuple 0)]
          (swap! counts (partial merge-with +) {word 1})
          (println counts)
          (ack! collector tuple)
          )))))

(defn mk-topology []
  (topology
   {"1" (spout-spec sentence-spout)}
   {"2" (bolt-spec {"1" ["words"]} word-bolt :p 2)}
    ))

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

