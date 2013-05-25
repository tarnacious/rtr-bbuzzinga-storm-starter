(ns utils.redis-test
	(:use [utils.redis]
		[clojure.test]))

; Test requires a local running redis instance
(deftest test-lpushredis
	(let [result (rpush-redis "mylist", "%20%20China%20Glaze 2")]
		(is (= 1 1))))

(deftest test-lpopredis
	(let [result (lpop-redis "mylist")]
		(is (= result "%20%20China%20Glaze 2"))))
