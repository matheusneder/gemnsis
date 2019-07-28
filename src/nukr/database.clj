(ns nukr.database)

(defonce database (atom {}))

(defn reset []
  (reset! database {:profiles {}
                    :connection-rank {}}))

(defn add-profile!
  [profile-model]
  (swap! database 
         (fn [database profile-model] 
           (assoc database 
                  :profiles
                  (assoc (:profiles database) (:id profile-model) profile-model)
                  :connection-rank
                  (assoc (:connection-rank database) (:id profile-model) 0)))
         profile-model))

(defn read-profile
  [profile-id]
  (get @database profile-id))

(defn connect!
  [connect-fn profile1-id profile2-id]
  (swap! database connect-fn profile1-id profile2-id))

(defn read-all
  []
  @database)