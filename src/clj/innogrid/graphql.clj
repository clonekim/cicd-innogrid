(ns innogrid.graphql
  (:require [mount.core :as mount]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [innogrid.config :refer [env]]
            [innogrid.resolver :as resolver]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as p]))


(defn parse-schema []
  (-> (io/resource (:graphql-schema env))
      slurp
      (p/parse-schema)))


(defn inject-resolvers [schema-edn]
  (-> schema-edn
      (util/inject-resolvers {:Query/users  resolver/get-users :Mutation/NewOrg resolver/create-new-org})
;;      (util/inject-scalar-transformers [:Object  {:parse #(.toString %) :serialize #(.toString %) }])
      (schema/compile)))


(mount/defstate ^{:on-reload :restart} graphql-schema
  :start
  (when (:graphql-schema env)
    (log/info "parsing graphql schema")
    (-> (parse-schema)
        (inject-resolvers))))

(defn query [query-string variables context opts]
  (lacinia/execute graphql-schema query-string variables context (or opts {}) ))
