(ns innogrid.resolver
  (:require [clojure.tools.logging :as log]))

(defn get-users [contxt args value]
  (log/debug  args value)
  [{:id "1" :name "a"}
   {:id "2" :name "b"}])


(defn create-new-org [_ args value]
 (log/debug args value)
  {:id "3"
   :name "A"}
  )
