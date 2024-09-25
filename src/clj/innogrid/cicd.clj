(ns innogrid.cicd
  (:require
    [innogrid.handler :refer [handler]]
    [innogrid.nrepl :as nrepl]
    [ring.adapter.undertow :as undertow]
    [innogrid.config :refer [env]]
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.tools.logging :as log]
    [mount.core :as mount])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (do
    (log/info "starting Http server on port"  (:port env))
    (undertow/run-undertow (handler)
                           (-> env
                               (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
                               (update :port #(or (-> env :options :port) %))
                               (select-keys [:io-threads :host :port :async?]))))
  :stop
  (.stop http-server))

(mount/defstate ^{:on-reload :noop} nrepl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when nrepl-server
    (nrepl/stop nrepl-server)))


(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))
