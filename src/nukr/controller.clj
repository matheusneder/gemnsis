(ns nukr.controller
  (:require 
   [io.pedestal.log :as log]
   [nukr.database :as database]
   [nukr.logic :as logic]
   [nukr.components :as components]))

;; Output models helpers

(defn to-profile-analytic-model
  "Produce the profile details output model."
  [profile]
  {:id (:id profile)
   :name (:name profile)
   :email (:email profile)
   :visible (:visible profile)
   :createdat (:created-at profile)
   :updatedat (:updated-at profile)
   :connections (count (:connections profile))})

(defn profile-sintetic-projection
  "Produce a list of {:id :name}."
  [profiles]
  (map #(select-keys % [:id :name]) profiles))

;; the controller functions

(defn add-profile!
  "Add a new profile to network.
   Input  model is: {:name string 
                     :email string 
                     :visible bool (optional, default true)};
   Output model is 
     - success: see to-profile-analytic-model function.
     - error: {:errors [(:network-over-capacity logic/core-error)]
                       ;; --or--
                       [(:profile-email-exists logic/core-error)]
                       ;; --or one or two of below items--
                       [(:profile-name-required logic/core-error)
                        (:profile-email-required logic/core-error)
                        (:profile-invalid-email logic/core-error)]}"
  [profile]
  (log/debug :msg "add-profile! fired." 
             :profile profile)
  (let [profile-or-error
        (logic/profile-check-preconditions
         profile
         (:profiles (database/read-all)))]
    (if (:errors profile-or-error)
      (do
        ;; Look for :network-over-capacity condition
        (if (some
             #(= (:network-over-capacity logic/core-error) %)
             (:errors profile-or-error))
          ;; then log warn if it happened
          (log/warn
           :msg "Network over capacity while trying to add profile."
           :profile profile)
          ;; log info for other kinds of precondition error
          (log/info
           :msg "add-profile! preconditions failed."
           :profile profile
           :result profile-or-error))
        ;; return errors
        profile-or-error)
      (let [new-profile (logic/new-profile profile)]
        (log/debug 
         :msg "add-profile! preconditions ok, saving the new profile."
         :new-profile new-profile)
        (database/save-profile! new-profile)
        (log/debug
         :msg "add-profile! successfully saved new-profile."
         :new-profile new-profile)        
        (to-profile-analytic-model new-profile)))))

(defn update-profile!
  "Update an existing profile.
   Input  model is 
     - profile-id: uuid
     - profile: {:name string 
                 :email string 
                 :visible bool (optional, default true)};
   Output model is 
     - success: see to-profile-analytic-model function.
     - error: {:errors [(:profile-not-found logic/core-error)]
                       ;; --or--
                       [(:profile-email-exists logic/core-error)]
                       ;; --or one or two of below items--
                       [(:profile-name-required logic/core-error)
                        (:profile-email-required logic/core-error)
                        (:profile-invalid-email logic/core-error)]}"
  [profile-id profile]
  (log/debug :msg "update-profile! fired."  
             :profile-id profile-id
             :profile profile)
  (let [old-profile (database/read-profile profile-id)]
    (if (nil? old-profile)
      {:errors [(:profile-not-found logic/core-error)]}
      (let [profile-or-error
            (logic/profile-check-preconditions
             profile
             (dissoc (:profiles (database/read-all)) profile-id))]
        (if (:errors profile-or-error)
          (do
            (log/info
             :msg "update-profile! preconditions failed."
             :profile-id profile-id
             :profile profile
             :result profile-or-error)
            ;; return errors
            profile-or-error)
          (let [new-profile (logic/update-profile old-profile profile)]
            (database/save-profile! new-profile)
            (log/debug
             :msg "update-profile! successfully updated profile."
             :new-profile new-profile)
            (to-profile-analytic-model new-profile)))))))

(defn connect-profiles!
  "Connect two profiles to each other.
   Input  model is 
     - profile1-id: uuid
     - profile2-id: uuid
   Output model is 
     - success: profile2-id details, see to-profile-analytic-model function.
     - error: {:errors [(:profile-not-found logic/core-error)] ;; profile1 
                       ;; --or--
                       [(:to-connect-profile-not-found logic/core-error)] ;; profile2
                       ;; --or--
                       [(:could-not-connect-itself logic/core-error)]
                       ;; --or--
                       [(:profiles-already-connected logic/core-error)]
                       ;; --or--
                       [(:conn-limit-reached logic/core-error)] ;; profile1
                       ;; --or--
                       [(:conn-to-limit-reached logic/core-error)]} ;; profile2"
  [profile1-id profile2-id]
  (log/debug :msg "connect-profiles! fired."
             :profile1-id profile1-id
             :profile2-id profile2-id)
  (let [preconditions
        (logic/connecting-check-preconditions
         (database/read-all)
         profile1-id
         profile2-id)]
    (if (:errors preconditions)
      (do
        (log/info :msg "connect-profiles! preconditions failed."
                  :profile1-id profile1-id
                  :profile2-id profile2-id
                  :result preconditions)
        preconditions)
      (let [result
            (database/connect! logic/connect profile1-id profile2-id)]
        (log/debug :msg "connect-profiles! successfully connected profiles."
                   :profile1-id profile1-id
                   :profile2-id profile2-id)
        (to-profile-analytic-model (get (:profiles result) profile2-id))))))

(defn get-profiles
  "Get network profile (paginated).
   For input/output models, see components/paginate function.
   For output model items, see profile-sintetic-projection function."
  [paginate-params]
  (log/debug :msg "get-profiles fired.")
  (->> (-> (database/read-all)
           :profiles
           vals)
       profile-sintetic-projection
       (components/paginate paginate-params)))

(defn get-suggestions
  "Get connection suggestions for a given profile-id.
   Input  model is: 
     - profile-id uuid;
     - paginate-params: see components/paginate function
   Output model is:
     - success: see components/paginate function; for items, 
                see profile-sintetic-projection function.
     - error: {:errors [{:key profile-not-found}]}"
  [profile-id paginate-params]
  (log/debug :msg "get-suggestions fired."
             :profile-id profile-id
             :paginate-params paginate-params)
  (let [result (logic/get-suggestions (database/read-all) profile-id)]
    (if (:errors result)
      (do
        (log/info :msg "get-suggestions preconditions failed."
                  :profile-id profile-id
                  :paginate-params paginate-params
                  :result result)
        result)
      (do
        (log/debug :msg "get-suggestions successfully generated suggestion list."
                   :profile-id profile-id
                   :num-of-generated-suggestions (count result))
        (->> result
             profile-sintetic-projection
             (components/paginate paginate-params))))))

(defn get-profile-connections
  "Get connections for a given profile-id.
   Input  model is: 
     - profile-id uuid;
     - paginate-params: see components/paginate function
   Output model is:
     - success: see components/paginate function; for items, 
                see profile-sintetic-projection function.
     - error: {:errors [{:key profile-not-found}]}"
  [profile-id paginate-params]
  (log/debug :msg "get-profile-connections fired."
             :profile-id profile-id
             :paginate-params paginate-params)  
  (let [result (logic/get-profile-connections 
                (database/read-all) profile-id)]
    (if (:errors result)
      (do
        (log/info :msg "get-profile-connections preconditions failed."
                  :profile-id profile-id
                  :paginate-params paginate-params)
        result)
      (do
        (log/debug :msg "get-profile-connections successfully returned."
                   :profile-id profile-id
                   :num-of-profile-connections (count result))
        (->> result
             profile-sintetic-projection
             (components/paginate paginate-params))))))

(defn get-profile-details
  "Get connections for a given profile-id.
   Input  model is: uuid;
   Output model is: 
     - success: see to-profile-analytic-model function.
     - error: {:errors [(:profile-not-found logic/core-error)]"
  [profile-id]
  (log/debug :msg "get-profile-details fired."
             :profile-id profile-id)  
  (let [result (database/read-profile profile-id)]
    (if (nil? result)
      (let [result {:errors [(:profile-not-found logic/core-error)]}]
        (log/info :msg "get-profile-details preconditions failed."
                  :profile-id profile-id
                  :result result)
        result)
      (to-profile-analytic-model result))))

(defn reset-database
  []
  (database/reset))

(defn dump-database 
  []
  (database/read-all))