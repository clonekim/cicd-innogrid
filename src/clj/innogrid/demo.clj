(ns innogrid.demo
  (:require [mount.core :as mount]
            [clojure.tools.logging :as log]))

(mount/defstate ^{:on-reload :restart} demo-db
  :start
  (do
    (log/info "demo mounted")
    1))
