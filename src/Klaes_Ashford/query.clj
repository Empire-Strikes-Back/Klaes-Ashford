(ns Klaes-Ashford.query
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout thread
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.java.io :as Wichita.java.io]
   [clojure.string :as Wichita.string]

   [datahike.api :as Deep-Thought.api]
   [Klaes-Ashford.seed :refer [root]]))

(do (set! *warn-on-reflection* true) (set! *unchecked-math* true))

(defmulti q :q)

(defmethod q :all-attributes
  [{:keys [conn]
    :as opts}]
  (Deep-Thought.api/q '[:find [?ident ...]
                        :where [_ :db/ident ?ident]]
                      @conn))