(ns nukr.logic)

(defn uuid [] (.toString (java.util.UUID/randomUUID)))
(defn now [] (java.util.Date.))

(def core-error
  {:profile-not-found
   {:key "profile-not-found"
    :msg "The given profile was not found."}
   :to-connect-profile-not-found
   {:key "to-connect-profile-not-found"
    :msg "Trying to connect to a nonexistent profile"}
   :profiles-already-connected
   {:key "profiles-already-connected"
    :msg "Trying to connect to a profile which is already connected to."}})

(defn profile-check-preconditions
  [])

(defn new-profile 
  [profile]
  {:id (uuid)
   :name (:name profile)
   :email (:email profile)
   :created-at (now)
   :suggestible (if 
                 (nil? (:suggestible profile))  
                  true
                  (:suggestible profile))
   :connections '()})

(defn connect-single
  "Include source-profile-id to target-profile-model connection list. 
   IMPORTANT NOTE: This function exists as a helper for the connect function.
   It must NOT be used insolated. Explanation: It connects target-profile to 
   source-profile but keep source-profile disconnected to target-profile. 
   This is a partial and inconsistend state. Every profile connection
   must be mutual."
  [target-profile-model source-profile-id]
  (assoc target-profile-model :connections
         (-> (:connections target-profile-model)
             (conj source-profile-id)
             ;; Added distinct to avoid connection duplicates which may 
             ;; occour if no precondition were checked or when  two profiles 
             ;; connect to each other at same time (concurrently).
             ;; This duplication could happen even precondition were checked 
             ;; because precondition checks is been done out of atom 
             ;; transaction scope.
             distinct)))

(defn connecting-check-preconditions
  "Check precodintions to connect profile1 and profile2:
   - profile 1 must exists;
   - profile 2 must exists;
   - profile 1 and 2 must not be already connected.
   Output model is:
     {:errors 
       [(:profile-not-found core-error)
        --or--
        (:to-connect-profile-not-found core-error)
        --or-- 
        (:profiles-already-connected core-error)]}
     --or--
     nil if precondtions ok."
  [network profile1-id profile2-id]
  (let [profiles (:profiles network)
        profile1-model (-> profiles (get profile1-id))
        profile2-model (-> profiles (get profile2-id))]
    (if
     ;; check if profile1 exists
     (nil? profile1-model)
      {:errors [(:profile-not-found core-error)]}
      (if
       ;; check if profile 2 exists
       (nil? profile2-model)
        {:errors [(:to-connect-profile-not-found core-error)]}
        (if
         ;; check if the profiles are not already connected
         (some #(= profile2-id %) (:connections profile1-model))
          {:errors [(:profiles-already-connected core-error)]}
          ;; precondition passed, returning nil
          nil)))))

(defn connect
  "Connect profile1 to profile2 and vice-versa.
   IMPORTANT NOTE: Strongly recomended to check preconditions using 
   connecting-check-preconditions function before use connect.
   If preconditions not checked and profile1 or profile2 don't exists, 
   it will be included in an invalid way in the network."
  [network profile1-id profile2-id]
  (let [profiles (:profiles network)
        connection-rank (:connection-rank network)
        profile1-model (-> profiles (get profile1-id))
        profile2-model (-> profiles (get profile2-id))]
    (assoc network
           :profiles 
           (-> profiles
               (assoc
                profile1-id
                (connect-single profile1-model profile2-id)
                profile2-id
                (connect-single profile2-model profile1-id))))))

(defn get-degree-sequence
  "Degree sequence is profile list descending ordered by connection count."
  [profiles]
  (sort-by #(* -1 (count (:connections %)))
           (vals profiles)))

(defn suggestion-conditions
  "Check if a candidate (profile) should be suggested as 
   connection to profile, accordding to these conditions:
   - candidate suggestibleproperty  must be true
   - candidate is not the profile
   - candidate and profile is not already connected."
  [candidate profile]
  (let [profile-id (:id profile)
        connections (:connections profile)]
    (and
     ;; available for suggestions
     (:suggestible candidate)
     ;; not the same profile
     (not= (:id candidate) profile-id)
     ;; is not already connected to
     (not (some #(= (:id candidate) %) connections)))))

(defn get-suggestions
  "Get connections suggestions for a given profile-id."
  [network profile-id]
  (let [profiles (:profiles network)
        profile (get profiles profile-id)
        rank (get-degree-sequence profiles)]
    (if
     (nil? profile)
      ;; the given profile-id NOT found
      {:errors [(:profile-not-found core-error)]}
      ;; the given profile-id is valid, so
      ;; will get suggestions for it.
      (filter #(suggestion-conditions % profile) rank))))

(defn get-profile-connections
  "Get connections for a given profile"
  [network profile-id]
  (let [profiles (:profiles network)
        profile (get profiles profile-id)]
    (if
     (nil? profile)
      ;; the given profile-id NOT found
      {:errors [(:profile-not-found core-error)]}
      ;; the given profile-id is valid, so...
      (map #(get profiles %) (:connections profile)))))
  