(ns nukr-logic.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn add-profile
   [profile-vector new-profile]
   (conj profile-vector 
         (assoc new-profile 
                :connections [])
         ))
 
 (defn add-to-profile-connection-id-vector
   [target-profile source-profile-id]
   (assoc target-profile 
          :connections 
          (conj 
           (get target-profile :connections) 
           source-profile-id))
   )
 
 (defn connect-from-to [profile-vector source-profile-id target-profile-id]
   (assoc profile-vector source-profile-id 
          (add-to-profile-connection-id-vector 
           (get profile-vector source-profile-id) target-profile-id))
   )

(defn connect [profile-vector profile1-id profile2-id]
  (connect-from-to
   (connect-from-to
    profile-vector
    profile1-id profile2-id) profile2-id profile1-id)
  )