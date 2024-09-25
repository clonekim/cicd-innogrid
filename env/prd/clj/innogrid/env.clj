(ns innogrid.env
  (:require [clojure.tools.logging :as log]
            [innogrid.banner :refer [banner]]))

(def defaults
  {:init
   (fn [] (banner))
   :stop
   (fn []
     (log/info "shutdown successfully"))
   :middleware identity})
