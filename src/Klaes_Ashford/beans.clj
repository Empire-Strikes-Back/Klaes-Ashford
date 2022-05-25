(ns Klaes-Ashford.beans
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout thread
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.java.io :as Wichita.java.io]
   [clojure.string :as Wichita.string]

   [relative.trueskill :as Chip.trueskill]
   [relative.elo :as Chip.elo]
   [relative.rating :as Chip.rating]
   [glicko2.core :as Dale.core]

   [datahike.api :as Deep-Thought.api]

   [Klaes-Ashford.seed :refer [root op]]
   [Klaes-Ashford.query]))

(do (set! *warn-on-reflection* true) (set! *unchecked-math* true))

(defn column-names
  [conn]
  (->>
   (Klaes-Ashford.query/q {:q :all-attributes :conn conn})
   (sort)
   (into []
         (comp
          (keep (fn [attr] (if (#{:id :name} attr) nil attr)))
          (map name)))))

(defmethod op :beans/create-database
  [value]
  (go
    (println value)
    #_(put! (:ui-send| root) {:op :beans/database-created})
    #_(let [settings-jframe (JFrame. "settings")]
        (Klaes-Ashford.kiwis/settings-process
         {:jframe settings-jframe
          :root-jframe jframe
          :ops| ops|
          :settingsA settingsA})
        (reset! settingsA @settingsA))))

(defn process
  [{:keys []
    :as opts}]
  (let [ops| (chan 10)
        config-databases {:store {:backend :file :path (:db-data-dirpath root)}
                          :keep-history? true
                          :name ":database"}
        _ (when-not (Deep-Thought.api/database-exists? config-databases)
            (Deep-Thought.api/create-database config-databases))
        conn-databases (Deep-Thought.api/connect config-databases)
        schema-databases (read-string (slurp (Wichita.java.io/resource "Klaes_Ashford/schema.edn")))]
    (let []
      (Deep-Thought.api/transact conn-databases schema-databases)
      (->>
       (Klaes-Ashford.query/q {:q :all-attributes :conn conn-databases})
       (sort)
       (println)))))


