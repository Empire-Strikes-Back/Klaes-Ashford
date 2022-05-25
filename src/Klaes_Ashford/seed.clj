(ns Klaes-Ashford.seed
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout thread
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.java.io :as Wichita.java.io]
   [clojure.string :as Wichita.string]
   [clojure.repl :as Wichita.repl])
  (:import
   (java.io File)))

(do (set! *warn-on-reflection* true) (set! *unchecked-math* true))

(defmulti op :op)

(defonce root (let [program-data-dirpath (or
                                          (some-> (System/getenv "Klaes-Ashford_PATH")
                                                  (.replaceFirst "^~" (System/getProperty "user.home")))
                                          (.getCanonicalPath ^File (Wichita.java.io/file (System/getProperty "user.home") ".Klaes-Ashford")))]
                {:program-data-dirpath program-data-dirpath
                 :state-file-filepath (.getCanonicalPath ^File (Wichita.java.io/file program-data-dirpath "Klaes-Ashford.edn"))
                 :db-data-dirpath (.getCanonicalPath ^File (Wichita.java.io/file program-data-dirpath "Deep-Thought"))
                 :port (or (try (Integer/parseInt (System/getenv "PORT"))
                                (catch Exception e nil))
                           3355)
                 :stateA (atom nil)
                 :host| (chan 1)
                 :ops| (chan 10)
                 :ui-send| (chan 10)}))