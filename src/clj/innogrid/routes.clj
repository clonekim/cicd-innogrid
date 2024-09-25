(ns innogrid.routes
  (:require
   [innogrid.middleware :as middleware]
   [innogrid.graphql :as graphql]
   [innogrid.config :refer [env]]
   [innogrid.demo :refer [demo-db]]
   [ring.util.http-response :as response]))



(defn home-page [request]
  (response/ok {:message "hello"}))


(defn post-graphql [request]
  (let [{query :query variables :variables} (:params request)]
    (-> (graphql/query query variables nil nil)
        (response/ok))))


(defn get-demo [request]
  (response/ok {:message demo-db}))

(defn routes []
  [["/" {:get home-page}]
   ["/demo" {:get get-demo}]
   [(:graphql-path env)  {:post post-graphql }]])
