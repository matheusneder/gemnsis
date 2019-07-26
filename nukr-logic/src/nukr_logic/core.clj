(ns nukr-logic.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def duplicated-profile-key-error "duplicated-profile-key-error")

(def profile-validation-errors
  {:duplicated-key
   {:key "profile-duplicated-key"
    :msg "Profile key must be unique."}
   :blank-name
   {:key "profile-blank-name"
    :msg "The field \"name\" is required."}
   :invalid-name
   {:key "invalid-profile-name"
    :msg "Profile name must contais only letters 
and it must has at least 3 letters and max of 50 letters."}
   :duplicated-name
   {:key "profile-duplicated-name"
    :msg "The profile name has already been 
taken for another person, please, choose another."}})

(defn add-error-reason [data error-reason]
  (assoc
   data
   :errors (conj (-> data :errors)
                 error-reason)))

(defn validate-profile-email [profile-data]
  profile-data)

(defn validate-profile-name [profile-data]
  "Validate if profile name is not blank then if has only letters
and its size is between 3 and 30."
  (if
   (clojure.string/blank? (-> profile-data :name))
    ;; name is invalid: it is blank
    (add-error-reason
     profile-data
     (-> profile-validation-errors :blank-name))
    ;; look for regex validation only if name is not blank
    ;; in order to avoid to add two errors for the same 
    ;; field (name)
    (if
      ;; Accept only letters and white space with
      ;; size between 3 and 50 characters.
      (re-matches #"^[A-Za-z ]{3,50}$" (-> profile-data :name))
      ;; is a valid name, so will not touch the profile-data
      profile-data
      ;; name is invalid: add reason to error list
      (add-error-reason
       profile-data
       (-> profile-validation-errors :invalid-name)))))

(defn validate-profile [profile-data]
  (-> profile-data
      validate-profile-name
      validate-profile-email))

(defn add-profile
  [profile-map key data]
  (let [verified-data (validate-profile data)]
        (if (contains? verified-data :errors)
          ;; profile data is NOT valid
          ;; so return errors
          {:errors (-> verified-data :errors)}
          ;; profile data is valid
          (if (contains? profile-map key)
            ;; key already exists
            ;; return error
            {:errors '((-> profile-validation-errors :duplicated-key))}
            ;; sounds good, adding the new profile with empty 
            ;; connections vector
            (assoc profile-map key 
                   (assoc data
                          :connections []))))))
 
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