(ns nukr.database)

(defonce database (atom {}))

(defn clear! []
  (reset! database nil))

(defn save-profile!
  [profile-model]
  (swap! database 
         (fn [database profile-model] 
           (assoc database 
                  :profiles
                  (assoc 
                   (:profiles database) 
                   (:id profile-model) 
                   profile-model)))
         profile-model))

(defn read-all
  []
  @database)

(defn read-profile
  [profile-id]
  (get (:profiles (read-all)) profile-id))

(defn connect!
  [connect-fn profile1-id profile2-id]
  (swap! database connect-fn profile1-id profile2-id))
