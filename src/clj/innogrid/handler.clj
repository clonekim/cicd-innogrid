(ns innogrid.handler
  (:require
    [innogrid.middleware :as middleware]
    [innogrid.routes :refer [routes]]
    [reitit.ring :as ring]
    [ring.util.http-response :as http-response]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [innogrid.env :refer [defaults]]
    [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(defn- async-aware-default-handler
  ([_] nil)
  ([_ respond _] (respond nil)))

(defn dummy [handler]
  (fn [request]
    (handler request)))


(mount/defstate ring-routes
  :start
  (ring/ring-handler
    (ring/router
      [(routes)])
    (ring/routes
      (ring/create-resource-handler {:path "/"})
      (wrap-content-type
       (dummy async-aware-default-handler))
      (ring/create-default-handler
       {:not-found
         (constantly (-> {:message  "Page not found"} (http-response/not-found)))
         :method-not-allowed
         (constantly (-> {:message "Not allowed"} (http-response/method-not-allowed)))
         :not-acceptable
         (constantly (-> {:message "Not acceptable"} (http-response/not-acceptable)))}
        ))
    {:middleware [middleware/wrap-formats]}))

(defn handler []
  (middleware/wrap-base #'ring-routes))
