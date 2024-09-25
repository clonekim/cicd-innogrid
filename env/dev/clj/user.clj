(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [innogrid.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [innogrid.cicd :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start []
  (mount/start-without #'innogrid.cicd/nrepl-server))

(defn stop []
  (mount/stop-except #'innogrid.cicd/nrepl-server))

(defn restart []
  (stop)
  (start))


