(ns utils.elastisch
  (:require [clojurewerkz.elastisch.rest :as esr]
  	    [clojurewerkz.elastisch.rest.document :as esd]
                [clojurewerkz.elastisch.query         :as q]
                [clojurewerkz.elastisch.rest.response :as esrsp]
                [clojure.pprint :as pp]
  	))

(defn http_connect[host_port]
	(esr/connect! host_port))

(defn wiki_title_query [page_name]
  (esr/connect! "http://127.0.0.1:9200")
  ;; performs a term query using a convenience function
  (let [res  (esd/search "wikipedia" "page" :query {:match_phrase {:title page_name}})
        ;n    (esrsp/total-hits res)
        cats (:category (:_source (first (:hits (:hits res)))))] cats))
