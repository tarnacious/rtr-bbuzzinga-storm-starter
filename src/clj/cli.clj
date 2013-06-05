(ns cli
(:use [utils.wikiclicks]))


(defn -main
  [& args]
  (distribute "words.txt")
  )

