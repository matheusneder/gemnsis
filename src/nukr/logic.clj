(ns gemnsis.logic
  (:require 
   [clojure.string :as str]))

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
    :msg "Trying to connect to a profile which is already connected to."}
   :could-not-connect-itself
   {:key "could-not-connect-itself"
    :msg "Could not connect profile to it self."}
   :profile-name-required
   {:key "profile-name-required"
    :msg "Profile name is required."}
   :profile-invalid-email
   {:key "profile-invalid-email"
    :msg "Profile e-mail is invalid."}
   :profile-email-required
   {:key "profile-email-required"
    :msg "Profile e-mail is required."}
   :profile-email-exists
   {:key "profile-email-exists"
    :msg "Email address is already in use for another profile."}
   :network-over-capacity
   {:key "network-over-capacity"
    :msg "Network number of profiles limit is reached."}
   :conn-limit-reached
   {:key "conn-limit-reached"
    :msg "Max number of connections reached."}
   :conn-to-limit-reached
   {:key "conn-to-limit-reached"
    :msg "Connecting to profile max number of connections reached."}})

(defn add-error 
  [data error]
  (assoc
   data
   :errors (conj (:errors data)
                 error)))

(defn validate-profile-name
  "Check if profile name is not blank."
  [profile]
  (if (str/blank? (:name profile))
    (add-error profile (:profile-name-required core-error))
    profile))

(defn validate-profile-email
  "Validate profile e-mail"
  [profile]
  (let [profile
        ;; convert email to lower case
        (assoc profile :email (str/lower-case
                               ;; use a empty string if nil
                               ;; to prevent NullPointerException
                               ;; on str/lower-case
                               (or (:email profile) "")))]
    (cond
      (str/blank? (:email profile))
      ;; invalid: it's blank.
      (add-error profile (:profile-email-required core-error))
       ;; look for regex validation only if email is not blank
       ;; in order to avoid to add two errors for the same 
       ;; field.      
      (re-matches #"^[a-z0-9.+_-]+@[a-z0-9]{2,}(\.[a-z0-9]{2,})+$"
                  (:email profile))
      ;; valid: matches regex pattern
      profile
      ;; invalid: didnt match regex pattern
      :else (add-error profile (:profile-invalid-email core-error)))))

(defn validate-profile-email-uniqueness
  "Check if e-mail for the given profile already exists
   on profile-coll."
  [profile profile-coll]
  (let [emails (map #(-> % val :email) profile-coll)
        profile-email (str/lower-case (:email profile))]
    (if (some #(= profile-email %) emails)
      ;; invalid: email exists
      {:errors [(:profile-email-exists core-error)]}
      ;; valid: email is new to network
      nil)))

(defn profile-validate-model
  "Validate model for a new profile. If validation fail, validation errors 
   will be associate to the :errors key in profile model (as a list). This 
   approach is based on notification pattern -
   https://martinfowler.com/eaaDev/Notification.html."
  [profile]
  (-> profile
      validate-profile-name
      validate-profile-email))

(def network-capacity-max-profiles 10000)

(defn validate-network-capacity
  "Validate if network capacity support a new profile."
  [profile-coll network-capacity-max-profiles]
  (if (>= (count profile-coll) network-capacity-max-profiles)
    ;; reached max profiles
    {:errors [(:network-over-capacity core-error)]}
    ;; capacity ok
    nil))

(defn profile-check-preconditions
  "Check profile preconditions by first check model validation using
   profile-validate-model function. If model was valid, it will check for
   network capacity then email uniqueness over network."
  [profile profile-coll]
  (let [checked-profile (profile-validate-model profile)]
    (if (:errors checked-profile)
      {:errors (:errors checked-profile)}
      (or 
       (validate-network-capacity profile-coll network-capacity-max-profiles)
       (validate-profile-email-uniqueness profile profile-coll)
       profile))))

(defn new-profile 
  "Create a new profile and return it.
   IMPORTANT NOTE: In order to create a new profile, you must first
   check preconditions using profile-check-preconditions function."
  [profile]
  {:id (uuid)
   :name (:name profile)
   :email (str/lower-case (:email profile))
   :created-at (now)
   :updated-at nil
   :visible (boolean 
                 (if (nil? (:visible profile))
                   true
                   (:visible profile)))
   :connections '()})

(defn update-profile
  "Update a profile based on new-profile param and return it.
   IMPORTANT NOTE: In order to update the profile, you must first
   check preconditions using profile-check-preconditions function by
   provinding the profile-coll with the target profile dissassociated
   from it in order to get uniquess checks to correctly work."  
  [old-profile new-profile]
  (merge old-profile
         {:name (:name new-profile)
          :email (str/lower-case (:email new-profile))
          :updated-at (now)
          :visible (boolean 
                        (if (nil? (:visible new-profile))
                          (:visible old-profile)
                          (:visible new-profile)))}))

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

(def profile-max-connections 1000)

(defn connecting-check-preconditions
  "Check precodintions to connect profile1 and profile2:
   - profile 1 must exists;
   - profile 2 must exists;
   - profile 1 and 2 must not be already connected.
   - profile 1 or profile 2 must not reach the profile-max-connections
   Output model is:
     {:errors 
       [(:profile-not-found core-error)]
       ;; --or--
       [(:could-not-connect-itself core-error)]
       ;; --or--
       [(:to-connect-profile-not-found core-error)]
       ;; --or-- 
       [(:profiles-already-connected core-error)]
       ;; --or--
       [(:conn-limit-reached logic/core-error)] ;; profile 1
       ;; --or--
       [(:conn-to-limit-reached logic/core-error)]} ;; profile 2
     ;; --or--
     nil ;; if precondtions ok."
  [network profile1-id profile2-id]
  (let [profiles (:profiles network)
        profile1-model (-> profiles (get profile1-id))
        profile2-model (-> profiles (get profile2-id))]
    ;; check if profile1 exists
    (cond
      (nil? profile1-model)
      {:errors [(:profile-not-found core-error)]}
      ;; check if profile 1 and 2 are not the same
      (= profile1-id profile2-id)
      {:errors [(:could-not-connect-itself core-error)]}
      ;; check if profile 2 exists
      (nil? profile2-model)
      {:errors [(:to-connect-profile-not-found core-error)]}
      ;; check if the profiles are not already connected
      (some #(= profile2-id %) (:connections profile1-model))
      {:errors [(:profiles-already-connected core-error)]}
      ;; check if profile 1 reached profile-max-connections
      (>= (count (:connections profile1-model)) profile-max-connections)
      {:errors [(:conn-limit-reached core-error)]}
      ;; check if profile 2 reached profile-max-connections
      (>= (count (:connections profile2-model)) profile-max-connections)
      {:errors [(:conn-to-limit-reached core-error)]}
      ;; precondition passed, returning nil
      :else nil)))

(defn connect
  "Connect profile1 to profile2 and vice-versa.
   IMPORTANT NOTE: Strongly recommended to check preconditions using 
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

(defn get-profile-rank
  "Degree sequence is profile list descending ordered by connection count."
  [profiles]
  (sort-by #(* -1 (count (:connections %)))
           (vals profiles)))

(defn suggestion-conditions
  "Check if a candidate (profile) should be suggested as 
   connection to profile, accordding to these conditions:
   - candidate visible property  must be true
   - candidate is not the profile itself
   - candidate and profile is not already connected."
  [candidate profile]
  (let [profile-id (:id profile)
        connections (:connections profile)]
    (and
     ;; available for suggestions
     (:visible candidate)
     ;; not the same profile
     (not= (:id candidate) profile-id)
     ;; is not already connected to
     (not (some #(= (:id candidate) %) connections)))))

(defn get-suggestions
  "Get connections suggestions for a given profile-id."
  [network profile-id]
  (let [profiles (:profiles network)
        profile (get profiles profile-id)
        rank (get-profile-rank profiles)]
    (if (nil? profile)
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
    (if (nil? profile)
      ;; the given profile-id NOT found
      {:errors [(:profile-not-found core-error)]}
      ;; the given profile-id is valid, so...
      (map #(get profiles %) (:connections profile)))))
  