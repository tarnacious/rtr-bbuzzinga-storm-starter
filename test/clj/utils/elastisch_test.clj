(ns utils.elastisch-test
	(:use [utils.elastisch]
		[clojure.test]))

; Test requires a local running redis instance
(deftest test-connect
	(let [result (connect "http://127.0.0.1" 9300)]
		(is (= 1 1))))