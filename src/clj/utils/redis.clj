(ns utils.redis
  (:require [taoensso.carmine :as car]))

; REDIS related stuff
(def pool         (car/make-conn-pool)) ; See docstring for additional options
(def spec-server1 (car/make-conn-spec))

(defmacro wcar [& body] `(car/with-conn pool spec-server1 ~@body))

(defn lpop-redis[key-value]
  (wcar (car/lpop key-value)))

(defn rpush-redis[list-name value]
  (wcar (car/rpush list-name value)))

