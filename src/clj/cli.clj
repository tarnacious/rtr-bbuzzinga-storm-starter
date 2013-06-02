(ns cli
(:use [utils.wikiclicks]))


(defn -main
  [& args]
  (distribute "pagecount/out.txt")
  )

