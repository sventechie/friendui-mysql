(ns sventechie.friendui-mysql.db
  (:require [yesql.core :as yesql :refer [defqueries]]
            [sventechie.friendui-mysql.globals :as glob]
            [de.sveri.friendui.service.user :as friend-service]))

(def connection-info (:database glob/friendui-config))
(yesql/defqueries "queries.sql" {:connection connection-info})

(defn role->string
  "Convert role keyword to string"
  [role]
  (clojure.string/replace (str role) #":" ""))

(defn string->role
  "Convert role string to role keyword"
  [role-string]
  (keyword
   (clojure.string/replace role-string #":" "")))

(defn- format-user-map
  "Format user data according to Friend-UI spec"
  [result-map]
  (hash-map :user/activated (:verified result-map)
            :user/role (string->role (:role result-map))
            :user/email (:email_address result-map)))

(defn get-usermap-by-username [email-address & [pw]]
  (when-let [result (get-account-by-email-query {:email_address email-address})]
    (let [user-map (first (format-user-map result))]
      (if pw user-map (dissoc user-map :user/password)))))

(defn get-loggedin-user-map []
  (get-usermap-by-username (friend-service/get-logged-in-username)))

(defn get-loggedin-user-role []
  (glob/role-kw (get-loggedin-user-map)))

(defn login-user [email-address]
  (when-let [user-map (get-usermap-by-username email-address true)]
    (when (friend-service/is-user-activated? user-map)
      {:username email-address :roles #{(glob/role-kw user-map)} :password (glob/pw-kw user-map)})))

(defn account-activated?
  "Check if account has been activated"
  [activation-id]
  (:verified
   (first (check-account-active-query {:activation_id activation-id}))))

(defn activate-account
  "Activate the account"
  [activation-id]
  (activate-account-query! {:activation_id activation-id}))

(defn create-new-user
  "Create a new account with email, pw, role and activation ID"
  [email-address password role activation-id]
  (create-account-query<! {:email_address email-address
                           :password password
                           :activation_id activation-id
                           :account_type (role->string role)}))

(defn get-all-users
  "Lists all accounts"
  []
  (map format-user-map
    (list-all-accounts-query)))

(defn get-user-for-activation-id
  "Lookup user by activation ID"
  [activation-id]
  (let [account-map (first (get-account-by-activation-query {:activation_id activation-id}))]
    {:username (:email_address account-map)
     :roles #{(string->role (:role account-map))}}))

(defn update-user
  "Update user role and activation status"
  [email data-map]
  (update-account-query!
   {:email_addres email
    :activated (:user/activated data-map)
    :account_type (role->string (:user/role data-map))}))

(defn username-exists?
  "Checks if account exists"
  [email-address]
  (some?
   (get-account-by-email-query {:email_address email-address})))

(defn get-old-pw-hash
  "Get current password hash"
  ([] (get-old-pw-hash (friend-service/get-logged-in-username)))
  ([email]
  (:password
   (first
    (get-password-query {:email_address email})))))

(defn change-password
  "Change password"
  ([new-password] (change-password (friend-service/get-logged-in-username) new-password))
  ([email-address new-password]
  (set-password-query! {:email_address email-address
                        :password new-password})))

(defn set-account-type
  "Set account type to :user/admin, :user/free, etc."
  [email-address account-type]
  (set-account-type-query! {:email_address email-address
                            :account_type account-type}))
