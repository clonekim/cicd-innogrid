(ns innogrid.env
  (:require
    [clojure.tools.logging :as log]
    [clojure.java.io :as io]
    [innogrid.banner :refer [banner]]
    [innogrid.dev-middleware :refer [wrap-dev]]))


(def defaults
  {:init
   (fn []
     (banner))
   :stop
   (fn []
     (log/info "shutdown successfully"))
   :middleware wrap-dev})
