(ns nukr.logic)

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defn new-profile 
  [data]
  {:id (uuid)
   :name (:name data)
   :email (:email data)
   :suggestible (or (:suggestible data) true)
   :connections '()})

(defn connect-single
  [target-profile-model profile-source-id]
  (assoc target-profile-model :connections 
         (conj (:connections target-profile-model) profile-source-id)))

(defn connect
  [network profile1-id profile2-id]
  (let [profiles (:profiles network)
        connection-rank (:connection-rank network)
        profile1-model (-> profiles (get profile1-id))
        profile2-model (-> profiles (get profile2-id))]
    (assoc network
           :profiles (-> profiles
                         (assoc
                          profile1-id (connect-single profile1-model profile2-id)
                          profile2-id (connect-single profile2-model profile1-id))))))

(defn get-degree-sequence
  "Degree sequence is profile list descending ordered by connection count."
  [profiles]
  (sort-by (fn [item] (* -1 (count (:connections item))))
           (vals profiles)))

(defn suggestion-conditions
  [item profile]
  (let [profile-id (:id profile)
        connections (:connections profile)]
    (and
     ;; available for suggestions
     (:suggestible item)
     ;; not the same profile
     (not= (:id item) profile-id)
     ;; is not already connected to
     (not (some #(= (:id item) %) connections)))))

(defn get-suggestions
  [network profile-id]
  (let [profiles (:profiles network)
        profile (get profiles profile-id)
        rank (get-degree-sequence profiles)]
    (filter 
     #(suggestion-conditions % profile) rank)))