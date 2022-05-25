(ns Klaes-Ashford.ui
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.string :as Wichita.string]
   [clojure.pprint :as Wichita.pprint]
   [cljs.core.async.impl.protocols :refer [closed?]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [goog.string.format :as format]
   [goog.string :refer [format]]
   [goog.object]
   [cljs.reader :refer [read-string]]
   [goog.events]

   ["react" :as Pacha]
   ["react-dom/client" :as Pacha.dom.client]

   [reagent.core :as Kuzco.core]
   [reagent.dom :as Kuzco.dom]

   [reitit.frontend :as Yzma.frontend]
   [reitit.frontend.easy :as Yzma.frontend.easy]
   [reitit.coercion.spec :as Yzma.coercion.spec]
   [reitit.frontend.controllers :as Yzma.frontend.controllers]
   [reitit.frontend.history :as Yzma.frontend.history]
   [spec-tools.data-spec :as Yzma.data-spec]

   ["antd/lib/layout" :default ThemeSongGuyLayout]
   ["antd/lib/menu" :default ThemeSongGuyMenu]
   ["antd/lib/button" :default ThemeSongGuyButton]
   ["antd/lib/row" :default ThemeSongGuyRow]
   ["antd/lib/col" :default ThemeSongGuyCol]
   ["antd/lib/input" :default ThemeSongGuyInput]
   ["antd/lib/table" :default ThemeSongGuyTable]


   [clojure.test.check.generators :as Pawny.generators]
   [clojure.spec.alpha :as Wichita.spec]

   [Klaes-Ashford.seed :refer [root]]
   [Klaes-Ashford.beans]
   #_[Klaes-Ashford.Ritchi]))


(defn rc-main-page
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:div {}
    [:div "i'm still getting used to being in a uniform myself"]]])

(defn rc-settings-page
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyRow
    "settings"
    #_(str "settings" (:rand-int @stateA))]])

(defn rc-brackets-page
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyRow
    "brackets"
    #_(str "settings" (:rand-int @stateA))]])

(defn rc-query-page
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyRow
    "query"
    #_(str "settings" (:rand-int @stateA))]])

(defn rc-current-page
  []
  (Kuzco.core/with-let
    [route-keyA (Kuzco.core/cursor (:matchA root) [:data :name])]
    (let [route-key @route-keyA]
      [:> ThemeSongGuyLayout
       [:> ThemeSongGuyMenu
        {:mode "horizontal"
         :size "large"
         :selectedKeys [route-key]
         :items [{:type "item"
                  :label
                  (Kuzco.core/as-element [:a {:href (Yzma.frontend.easy/href :rc-main-page)} "raiting"])
                  :key :rc-rating-page
                  :icon nil}
                 {:type "item"
                  :label
                  (Kuzco.core/as-element [:a {:href (Yzma.frontend.easy/href :rc-brackets-page)} "edit"])
                  :key :rc-brackets-page
                  :icon nil}
                 {:type "item"
                  :label
                  (Kuzco.core/as-element [:a {:href (Yzma.frontend.easy/href :rc-query-page)} "query"])
                  :key :rc-query-page
                  :icon nil}
                 #_{:type "item"
                    :label
                    (Kuzco.core/as-element [:a {:href (Yzma.frontend.easy/href :rc-settings-page)} "settings"])
                    :key :rc-settings-page
                    :icon nil}]}]
       (when-let [match @(:matchA root)]
         [(-> match :data :view)])])
    #_[:<>

       [:ul
        [:li [:a {:href (Yzma.frontend.easy/href :rc-main-page)} "game"]]
        [:li [:a {:href (Yzma.frontend.easy/href :rc-settings-page)} "settings"]]]
       (when-let [match @matchA]
         [(-> match :data :view) match stateA])]))

(defn websocket-process
  [{:keys [send| recv|]
    :as opts}]
  (let [socket (js/WebSocket. "ws://localhost:3355/ui")]
    (.addEventListener socket "open" (fn [event]
                                       (println :websocket-open)
                                       (put! send| {:op :ping
                                                    :from :ui
                                                    :if :there-is-sompn-strage-in-your-neighbourhood
                                                    :who :ya-gonna-call?})))
    (.addEventListener socket "message" (fn [event]
                                          (put! recv| (read-string (.-data event)))))
    (.addEventListener socket "close" (fn [event]
                                        (println :websocket-close event)))
    (.addEventListener socket "error" (fn [event]
                                        (println :websocket-error event)))
    (go
      (loop []
        (when-let [value (<! send|)]
          (.send socket (str value))
          (recur))))))

(defn router-process
  [{:keys []
    :as opts}]
  (let [history (Yzma.frontend.easy/start!
                 (Yzma.frontend/router
                  ["/"
                   [""
                    {:name :rc-main-page
                     :view Klaes-Ashford.beans/rc-page
                     :controllers [{:start (fn [_]
                                             (js/console.log "start rc-main-page"))
                                    :stop (fn [_]
                                            (js/console.log "stop rc-main-page"))}]}]

                   ["rating"
                    {:name :rc-rating-page
                     :view Klaes-Ashford.beans/rc-page
                     :controllers [{:start (fn [_]
                                             #_(js/console.log "start rc-rating-page")
                                             (Klaes-Ashford.beans/process {}))
                                    :stop (fn [_]
                                            (js/console.log "stop rc-rating-page"))}]}]

                   ["brackets"
                    {:name :rc-brackets-page
                     :view rc-brackets-page
                     :controllers [{:start (fn [_]
                                             (js/console.log "start rc-brackets-page"))
                                    :stop (fn [_]
                                            (js/console.log "stop rc-brackets-page"))}]}]

                   ["query"
                    {:name :rc-query-page
                     :view rc-query-page
                     :controllers [{:start (fn [_]
                                             (js/console.log "start rc-query-page"))
                                    :stop (fn [_]
                                            (js/console.log "stop rc-query-page"))}]}]

                   #_["settings"
                      {:name :rc-settings-page
                       :view rc-settings-page
                       :controllers [{:start (fn [_]
                                               (js/console.log "start rc-settings-page"))
                                      :stop (fn [_]
                                              (js/console.log "stop rc-settings-page"))}]}]]
                  {:data {:controllers [{:start (fn [_]
                                                  (js/console.log "start program")
                                                  (websocket-process {:send| (:program-send| root)
                                                                      :recv| (:ops| root)}))
                                         :stop (fn [_]
                                                 (js/console.log "stop program"))}]
                          :coercion Yzma.coercion.spec/coercion}})
                 (fn [new-match]
                   (swap! (:matchA root) (fn [old-match]
                                           (if new-match
                                             (assoc new-match :controllers (Yzma.frontend.controllers/apply-controllers (:controllers old-match) new-match))))))
                 {:use-fragment false})]
    (goog.events/unlistenByKey (:click-listen-key history))
    (goog.events/listen js/document goog.events.EventType.CLICK
                        (fn [event]
                          (when-let [element (Yzma.frontend.history/closest-by-tag
                                              (Yzma.frontend.history/event-target event) "a")]
                            (let [uri (.parse goog.Uri (.-href element))]
                              (when (Yzma.frontend.history/ignore-anchor-click? (.-router history) event element uri)
                                (.preventDefault event)
                                (let [path (str (.getPath uri)
                                                (when (.hasQuery uri)
                                                  (str "?" (.getQuery uri)))
                                                (when (.hasFragment uri)
                                                  (str "#" (.getFragment uri))))]
                                  (.pushState js/window.history nil "" path)
                                  (Yzma.frontend.history/-on-navigate history path)))))) true)))

(def colors
  {:sands "#edd3af" #_"#D2B48Cff"
   :Korvus "lightgrey"
   :signal-tower "brown"
   :recharge "#30ad23"
   :Klaes-Ashford "blue"})


(defmulti op :op)

(defmethod op :ping
  [value]
  (go
    (Wichita.pprint/pprint value)
    (put! (:program-send| root) {:op :pong
                                 :from :ui
                                 :moneybuster :Jesus})))

(defmethod op :pong
  [value]
  (go
    (Wichita.pprint/pprint value)))

(defn ops-process
  [{:keys []
    :as opts}]
  (go
    (loop []
      (when-let [value (<! (:ops| root))]
        (<! (op value))
        (recur)))))

(defn -main
  []
  (go
    (<! (timeout 1000))
    (println "twelve is the new twony")
    (println ":Madison you though i was a zombie?")
    (println ":Columbus yeah, of course - a zombie")
    (println ":Madison oh my God, no - i dont even eat meat - i'm a vegatarian - vegan actually")
    #_(set! (.-innerHTML (.getElementById js/document "ui"))
            ":Co-Pilot i saw your planet destroyed - i was on the Death Star :_ which one?")
    (router-process {})
    (ops-process {})
    (.render (:Pacha-dom-root root) (Kuzco.core/as-element [rc-current-page]))
    #_(Yzma.frontend.easy/push-state :rc-main-page)))

(defn reload
  [])

#_(-main)