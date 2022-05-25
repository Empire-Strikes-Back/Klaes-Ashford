(ns Klaes-Ashford.main
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout thread
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.core.async.impl.protocols :refer [closed?]]
   [clojure.java.io :as Wichita.java.io]
   [clojure.string :as Wichita.string]
   [clojure.pprint :as Wichita.pprint]
   [clojure.repl :as Wichita.repl]
   [clojure.java.shell :as Wichita.java.shell]

   [aleph.http :as Simba.http]
   [manifold.deferred :as Nala.deferred]
   [manifold.stream :as Nala.stream]
   [byte-streams :as Rafiki]
   [cheshire.core :as Cheshire-Cat.core]

   [datahike.api :as Deep-Thought.api]
   [taoensso.timbre :as Sabretooth.core]

   [Klaes-Ashford.seed :refer [root op]]
   [Klaes-Ashford.host]
   [Klaes-Ashford.dates]
   [Klaes-Ashford.sunflower-seeds]
   [Klaes-Ashford.apples]
   [Klaes-Ashford.salt]
   [Klaes-Ashford.microwaved-onions]
   [Klaes-Ashford.corn]
   [Klaes-Ashford.beans])
  (:import
   (java.io File)
   (io.ipfs.api IPFS)
   (java.util.stream Stream)
   (java.util Base64)
   (java.io BufferedReader)
   (java.nio.charset StandardCharsets))
  (:gen-class))

(do (set! *warn-on-reflection* true) (set! *unchecked-math* true))

(Sabretooth.core/merge-config! {:min-level :warn})

(defn reload
  []
  (require
   '[Klaes-Ashford.seed]
   '[Klaes-Ashford.host]
   '[Klaes-Ashford.dates]
   '[Klaes-Ashford.sunflower-seeds]
   '[Klaes-Ashford.apples]
   '[Klaes-Ashford.salt]
   '[Klaes-Ashford.microwaved-onions]
   '[Klaes-Ashford.corn]
   '[Klaes-Ashford.beans]
   '[Klaes-Ashford.main]
   :reload))

(defn encode-base64url-u
  [^String string]
  (-> (Base64/getUrlEncoder) (.withoutPadding)
      (.encodeToString (.getBytes string StandardCharsets/UTF_8)) (->> (str "u"))))

(defn decode-base64url-u
  [^String string]
  (-> (Base64/getUrlDecoder)
      (.decode (subs string 1))
      (String. StandardCharsets/UTF_8)))

(defn pubsub-sub
  [base-url topic message| cancel| raw-stream-connection-pool]
  (let [streamV (volatile! nil)]
    (->
     (Nala.deferred/chain
      (Simba.http/post (str base-url "/api/v0/pubsub/sub")
                       {:query-params {:arg topic}
                        :pool raw-stream-connection-pool})
      :body
      (fn [stream]
        (vreset! streamV stream)
        stream)
      #(Nala.stream/map Rafiki/to-string %)
      (fn [stream]
        (Nala.deferred/loop
         []
          (->
           (Nala.stream/take! stream :none)
           (Nala.deferred/chain
            (fn [message-string]
              (when-not (identical? message-string :none)
                (let [message (Cheshire-Cat.core/parse-string message-string true)]
                  #_(println :message message)
                  (put! message| message))
                (Nala.deferred/recur))))
           (Nala.deferred/catch Exception (fn [ex] (println ex)))))))
     (Nala.deferred/catch Exception (fn [ex] (println ex))))

    (go
      (<! cancel|)
      (Nala.stream/close! @streamV))
    nil))

(defn pubsub-pub
  [base-url topic message]
  (let []

    (->
     (Nala.deferred/chain
      (Simba.http/post (str base-url "/api/v0/pubsub/pub")
                       {:query-params {:arg topic}
                        :multipart [{:name "file" :content message}]})
      :body
      Rafiki/to-string
      (fn [response-string] #_(println :repsponse reresponse-stringsponse)))
     (Nala.deferred/catch
      Exception
      (fn [ex] (println ex))))

    nil))

(defn subscribe-process
  [{:keys [^String ipfs-api-multiaddress
           ^String ipfs-api-url
           frequency
           raw-stream-connection-pool
           sub|
           cancel|
           id|]
    :as opts}]
  (let [ipfs (IPFS. ipfs-api-multiaddress)
        base-url ipfs-api-url
        topic (encode-base64url-u frequency)
        id (-> ipfs (.id) (.get "ID"))
        message| (chan (sliding-buffer 10))]
    (put! id| {:peer-id id})
    (pubsub-sub base-url  topic message| cancel| raw-stream-connection-pool)

    (go
      (loop []
        (when-let [value (<! message|)]
          (put! sub| (merge value
                            {:message (-> (:data value) (decode-base64url-u) (read-string))}))
          #_(println (merge value
                            {:message (-> (:data value) (decode-base64url-u) (read-string))}))
          #_(when-not (= (:from value) id)

              #_(println (merge value
                                {:message (-> (:data value) (decode-base64url-u) (read-string))})))
          (recur))))

    #_(go
        (loop []
          (<! (timeout 2000))
          (pubsub-pub base-url topic (str {:id id
                                           :rand-int (rand-int 100)}))
          (recur)))))

(defmethod op :ping
  [value]
  (go
    (Wichita.pprint/pprint value)
    (put! (:ui-send| root) {:op :pong
                            :from :program
                            :meatbuster :Jesus})))

(defmethod op :pong
  [value]
  (go
    (Wichita.pprint/pprint value)))

(defmethod op :game
  [value]
  (go
    #_(let [{:keys [frequency role]} value
            id| (chan 1)
            port (or (System/getenv "Jar_Jar_IPFS_PORT") "5001")
            ipfs-api-url (format "http://127.0.0.1:%s" port)
            games-topic (Klaes-Ashford.corn/encode-base64url-u "raisins")
            game-topic (Klaes-Ashford.corn/encode-base64url-u frequency)
            _ (Klaes-Ashford.corn/subscribe-process
               {:sub| sub|
                :cancel| cancel-sub|
                :frequency frequency
                :ipfs-api-url ipfs-api-url
                :ipfs-api-multiaddress (format "/ip4/127.0.0.1/tcp/%s" port)
                :id| id|})
            host? (= role :host)
            {:keys [peer-id]} (<! id|)]
        #_(println :game value)
        (go
          (loop []
            (alt!
              cancel-pub|
              ([_] (do nil))

              (timeout 2000)
              ([_]
               (when host?
                 (Klaes-Ashford.corn/pubsub-pub
                  ipfs-api-url games-topic (str {:op :games
                                                 :timestamp (.getTime (java.util.Date.))
                                                 :frequency frequency
                                                 :host-peer-id peer-id}))
                 (Klaes-Ashford.corn/pubsub-pub
                  ipfs-api-url game-topic (str {:op :game-state
                                                :timestamp (.getTime (java.util.Date.))
                                                :game-state {:host-peer-id peer-id}})))

               (Klaes-Ashford.corn/pubsub-pub
                ipfs-api-url game-topic (str {:op :player-state
                                              :timestamp (.getTime (java.util.Date.))
                                              :peer-id peer-id}))
               (recur))))))))

(defmethod op :leave
  [value]
  (go
    #_(let [{:keys [frequency]} value]
        (>! cancel-sub| true)
        (>! cancel-pub| true)
        (reset! gameA {}))))

(defmethod op :discover
  [value]
  (go
    #_(let [discover-jframe (JFrame. "discover")]
        (Klaes-Ashford.kiwis/discover-process
         {:jframe discover-jframe
          :root-jframe jframe
          :ops| ops|
          :gamesA gamesA
          :gameA gameA
          :stateA stateA})
        (reset! gameA @gameA))))

(defmethod op :settings
  [value]
  (go
    #_(let [settings-jframe (JFrame. "settings")]
        (Klaes-Ashford.kiwis/settings-process
         {:jframe settings-jframe
          :root-jframe jframe
          :ops| ops|
          :settingsA settingsA})
        (reset! settingsA @settingsA))))

(defn ops-process
  [{:keys []
    :as opts}]
  (go
    (loop []
      (when-let [value (<! (:ops| root))]
        (<! (op value))
        (recur)))))

(defn -main
  [& args]
  (println (format ":Drummer welcome to the Behemoth"))
  (println (format ":_ i can feel the love already"))

  (let []
    (Wichita.java.io/make-parents (:program-data-dirpath root))
    (reset! (:stateA root) {})

    (remove-watch (:stateA root) :watch-fn)
    (add-watch (:stateA root) :watch-fn
               (fn [ref wathc-key old-state new-state]

                 (when (not= old-state new-state))))

    (Klaes-Ashford.host/process
     {:port (:port root)
      :host| (:host| root)
      :ws-send| (:ui-send| root)
      :ws-recv| (:ops| root)})

    (ops-process {})

    (Wichita.java.io/make-parents (:db-data-dirpath root))
    (Klaes-Ashford.beans/process {})

    
    (let [config {:store {:backend :file :path (:db-data-dirpath root)}
                  :keep-history? true
                  :name "main"}
          _ (when-not (Deep-Thought.api/database-exists? config)
              (Deep-Thought.api/create-database config))
          conn (Deep-Thought.api/connect config)]
      
      (println :log (Sabretooth.core/debug 123))

      (Deep-Thought.api/transact
       conn
       [{:db/cardinality :db.cardinality/one
         :db/ident :id
         :db/unique :db.unique/identity
         :db/valueType :db.type/uuid}
        {:db/ident :name
         :db/valueType :db.type/string
         :db/cardinality :db.cardinality/one}])

      (Deep-Thought.api/transact
       conn
       [{:id #uuid "3e7c14ce-5f00-4ac2-9822-68f7d5a60952"
         :name  "Deep-Thought"}
        {:id #uuid "f82dc4f3-59c1-492a-8578-6f01986cc4c2"
         :name  "Wichita"}
        {:id #uuid "5358b384-3568-47f9-9a40-a9a306d75b12"
         :name  "Little-Rock"}])

      (->>
       (Deep-Thought.api/q '[:find ?e ?n
                             :where
                             [?e :name ?n]]
                           @conn)
       (println))

      (->>
       (Deep-Thought.api/q '[:find [?ident ...]
                             :where [_ :db/ident ?ident]]
                           @conn)
       (sort)
       (println)))))


(comment

  (go
    (loop []
      (when-let [{:keys [message from] :as value} (<! sub|)]
        (condp = (:op message)
          :game-state
          (let [{:keys [game-state]} message]
            (swap! gameA merge game-state))
          :player-state
          (let [{:keys [game-state]} message]
            (swap! gameA update-in [:players from] merge message))
          :games
          (let [{:keys [frequency host-peer-id]} message]
            (swap! gamesA update-in [frequency] merge message)))
        (recur))))

  (go
    (loop []
      (<! (timeout 3000))
      (let [expired (into []
                          (comp
                           (keep (fn [[frequency {:keys [timestamp]}]]
                                   #_(println (- (.getTime (java.util.Date.)) timestamp))
                                   (when-not (< (- (.getTime (java.util.Date.)) timestamp) 4000)
                                     frequency))))
                          @gamesA)]
        (when-not (empty? expired)
          (apply swap! gamesA dissoc expired)))
      (recur)))

  (go
    (loop []
      (<! (timeout 3000))
      (let [expired (into []
                          (comp
                           (keep (fn [[frequency {:keys [timestamp peer-id]}]]
                                   #_(println (- (.getTime (java.util.Date.)) timestamp))
                                   (when-not (< (- (.getTime (java.util.Date.)) timestamp) 4000)
                                     frequency))))
                          (:players @gameA))]
        (when-not (empty? expired)
          (apply swap! gameA update :players dissoc expired)))
      (recur)))

  (let [port (or (System/getenv "Jar_Jar_IPFS_PORT") "5001")
        ipfs-api-url (format "http://127.0.0.1:%s" port)
        id| (chan 1)
        raw-stream-connection-pool (Simba.http/connection-pool {:connection-options {:raw-stream? true}})]

    (alter-var-root #'raw-stream-connection-pool (constantly raw-stream-connection-pool))
    (Klaes-Ashford.corn/subscribe-process
     {:sub| sub|
      :raw-stream-connection-pool raw-stream-connection-pool
      :cancel| (chan (sliding-buffer 1))
      :frequency "raisins"
      :ipfs-api-url ipfs-api-url
      :ipfs-api-multiaddress (format "/ip4/127.0.0.1/tcp/%s" port)
      :id| id|}))

  ;
  )