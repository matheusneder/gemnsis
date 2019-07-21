(ns nukr-logic.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn add-profile-x
   [profile-map new-profile]
   (conj profile-map 
         (assoc new-profile 
                :connections [])
         ))

(defn newkey [] (.toString (java.util.UUID/randomUUID)))

(defn add-profile
  [profile-map new-profile]
  (assoc profile-map
         (newkey)
         (assoc new-profile
                :connections [])))
 
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