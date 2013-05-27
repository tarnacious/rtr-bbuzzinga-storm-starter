(ns utils.elastisch-test
	(:use [utils.elastisch]
		[clojure.test]))

(deftest test-connect
	(let [result (http_connect "http://localhost:9200")]
		(is (= 1 1))))

(deftest test-query
	(let [result (wiki_title_query "GP32")]
		(is (= 3 (count result)))))