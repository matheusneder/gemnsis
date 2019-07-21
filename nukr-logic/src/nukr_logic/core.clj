(ns nukr-logic.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def duplicated-profile-key-error "duplicated-profile-key-error")

(defn add-profile 
  [profile-map key data]
  (if (contains? profile-map key)
    [nil 'duplicated-profile-key-error]
    (assoc profile-map key 
           (assoc data
                  :connections []))))
 
 (defn add-to-profile-connection-id-vector
   [target-profile source-profile-id]
   (assoc target-profile 
          :connections 
          (conj 
           (get target-profile :connections) 
           source-profile-id))
   )
 
 (defn connect-from-to [profile-map source-profile-id target-profile-id]
   (assoc profile-map 
          source-profile-id 
          (add-to-profile-connection-id-vector 
           (get profile-map source-profile-id) target-profile-id))
   )

(defn connect [profile-map profile1-id profile2-id]
  (connect-from-to
   (connect-from-to
    profile-map
    profile1-id profile2-id) profile2-id profile1-id)
  )