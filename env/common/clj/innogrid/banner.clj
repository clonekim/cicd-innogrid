(ns innogrid.banner
  (:require [clojure.java.io :as io]))

(defn banner []
  (-> (io/resource "banner.txt")
      slurp
      println))
