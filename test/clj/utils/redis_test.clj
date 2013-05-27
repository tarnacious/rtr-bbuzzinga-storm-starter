(ns utils.redis-test
	(:use [utils.redis]
	        [utils.io]
	        [clojure.test]))

(deftest test-push-to-redis-file
	;(process-file "./resources/list-20130525-090000.txt" process-line 0)
	(process-file "./resources/list-20130525-100000.txt" process-line 0)
	(is (= 1 1)))

; Test requires a local running redis instance
(deftest test-lpushredis
	(let [result (rpush-redis "mylist", "%20%20China%20Glaze 2")]
		(is (= 1 1))))

(deftest test-lpopredis
	(let [result (lpop-redis "mylist")]
		(is (= result "%20%20China%20Glaze 2"))))
