(ns innogrid.middleware
  (:require
    [innogrid.env :refer [defaults]]
    [clojure.tools.logging :as log]
    [luminus-transit.time :as time]
    [muuntaja.core :as m]
    [muuntaja.middleware :refer [wrap-format wrap-params]]
    [innogrid.config :refer [env]]
    [ring.util.response :as response]
    [ring.adapter.undertow.middleware.session :refer [wrap-session]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(def formats
  (m/create
    (-> m/default-options
        (update-in
          [:formats "application/transit+json" :decoder-opts]
          (partial merge time/time-deserialization-handlers))
        (update-in
          [:formats "application/transit+json" :encoder-opts]
          (partial merge time/time-serialization-handlers)))))


(defn wrap-internal-error [handler]
  (let [error-result (fn [^Throwable t]
                       (log/error t (.getMessage t))
                       {:message "Something very bad has happened!"
                        :exception (.getMessage t)})]

    (fn wrap-internal-error-fn
      ([req respond _]
       (handler req respond #(respond (error-result %))))
      ([req]
       (try
         (handler req)
         (catch Throwable t
           (println "catch error" )
           (-> (error-result t)
               (response/response)
               (response/content-type "application/json")
               (response/status 500))))))))



(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats))]
    (fn
      ([request]
         ;; disable wrap-formats for websockets
         ;; since they're not compatible with this middleware
       ((if (:websocket? request) handler wrapped) request))
      ([request respond raise]
       ((if (:websocket? request) handler wrapped) request respond raise)))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-session {:cookie-attrs {:http-only true}})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      wrap-internal-error))


