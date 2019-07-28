(ns nukr.controller
  (:require 
   [io.pedestal.log :as log]
   [nukr.database :as database]
   [nukr.logic :as logic]
   [clojure.math.numeric-tower :as math]))

(defn add-profile!
  "Add a new profile to network.
   Input  model is: { :name string :email string :suggestible bool (optional, default true) };
   Output model is: { :id uuid :name string :email string :suggestigle bool }"
  [add-profile-dto]
  (let [profile-model (logic/new-profile add-profile-dto)]
    (database/add-profile! profile-model)
    profile-model))

(defn connect-profiles!
  [profile1-id profile2-id]
  (database/connect! logic/connect profile1-id profile2-id))

(def paginate-items-per-page-max 5)
(def paginate-items-per-page-default 2)

(defn limit
  "If val greater than max, return max; 
   Returns val otherwise."
  [val max]
  (if (> val max)
    max
    val))

(defn try-parse-int 
  [val default-val]
  "Try to parse val as integer then returns it;
   If val is nil or not a valid integer then returns default-val."
  (let [provided-val 
        (if 
         (nil? val) 
          ;; keep nil if nil provided
          nil 
          ;; non nil val was provided
          ;; so reading it
          (read-string val))]
    (if (integer? provided-val)
      provided-val
      default-val)))

(defn paginate 
  ""
  [paginate-params coll]
  (let [items-per-page (-> paginate-params
                           :items-per-page
                           (try-parse-int paginate-items-per-page-default)
                           (limit paginate-items-per-page-max))
        current-page (try-parse-int (:page paginate-params) 1)
        total-items (count coll)
        total-pages (math/ceil (/ total-items items-per-page))
        dropping (* (dec current-page) items-per-page)
        items (->> coll
                   (drop dropping)
                   (take items-per-page))]
    {:total_items total-items
     :showing (count items)
     :current_page current-page
     :total_pages total-pages
     :dropping dropping
     :items items}))

(defn get-profiles
  [paginate-params]
  (->>
   (-> (database/read-all)
       :profiles
       vals)
   (map #(select-keys % [:id :name]))
   (paginate paginate-params)))

(defn get-suggestions
  "Get connection suggestions for a given profile-id.
   Input  model is: uuid;
   Output model is:
     '({:id uuid :name string});
     -or-
     {:errors '({:key profile-not-found})}"
  [profile-id]
  (let [suggestions (logic/get-suggestions (database/read-all) profile-id)]
    suggestions))

(defn reset-database
  []
  (database/reset))

(defn dump-database 
  []
  (database/read-all))