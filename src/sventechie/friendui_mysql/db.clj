(ns sventechie.friendui-mysql.db
  (:require [yesql.core :as yesql :refer [defqueries]]
            [sventechie.friendui-mysql.globals :as glob]
            [de.sveri.friendui.service.user :as friend-service]))

(def connection-info (:database glob/friendui-config))
(yesql/defqueries "queries.sql")

(defn role->string
  "Convert role keyword to string"
  [role]
  (clojure.string/replace (str role) #":" ""))

(defn string->role
  "Convert role string to role keyword"
  [raw-string]
  (if-let [role-string raw-string]
    (keyword
      (clojure.string/replace role-string #":" ""))
    :user/bogus-role))

(defn- format-user-map
  "Format user data according to Friend-UI spec"
  [result-map]
  (if-let [user-map result-map]
    (hash-map glob/activated-kw (:verified user-map)
              glob/role-kw (string->role (:role user-map))
              glob/username-kw (:email_address user-map)
              glob/pw-kw (:password user-map))
    {}))

(defn get-usermap-by-username
  [db-map email-address & pw]
  (when-let [result (get-account-by-email-query {:email_address email-address}
                                                {:connection db-map})]
    (let [user-map (format-user-map (first result))]
      (if pw user-map (dissoc user-map glob/pw-kw)))))

(defn get-loggedin-user-map [db-map]
  (get-usermap-by-username db-map (friend-service/get-logged-in-username)))

(defn get-loggedin-user-role [db-map]
  (glob/role-kw (get-loggedin-user-map db-map)))

(defn login-user [db-map email-address]
  (when-let [user-map (get-usermap-by-username db-map email-address true)]
    (when (friend-service/is-user-activated? user-map)
      {:username email-address
       :roles #{(glob/role-kw user-map)}
       :password (glob/pw-kw user-map)})))


(defn account-activated?
  "Check if account has been activated"
  [db-map activation-id]
  (:verified
   (first (check-account-active-query {:activation_id activation-id}
                                      {:connection db-map}))))

(defn activate-account
  "Activate the account"
  [db-map activation-id]
  (activate-account-query! {:activation_id activation-id}
                           {:connection db-map}))

(defn create-new-user
  "Create a new account with email, pw, role and activation ID"
  [db-map email-address password role activation-id]
  (create-account-query<! {:email_address email-address
                           :password password
                           :activation_id activation-id
                           :account_type (role->string role)}
                          {:connection db-map}))

(defn get-all-users
  "Lists all accounts"
  [db-map]
  (map format-user-map
    (list-all-accounts-query)))

(defn get-user-for-activation-id
  "Lookup user by activation ID"
  [db-map activation-id]
  (let [account-map (first (get-account-by-activation-query {:activation_id activation-id}
                                                            {:connection db-map}))]
    {:username (:email_address account-map)
     :roles #{(string->role (:role account-map))}}))

(defn update-user
  "Update user role and activation status"
  [db-map email data-map]
  (update-account-query!
   {:email_addres email
    :activated (:user/activated data-map)
    :account_type (role->string (:user/role data-map))}
   {:connection db-map}))

(defn username-exists?
  "Checks if account exists"
  [db-map email-address]
  (some?
    (first
      (get-account-by-email-query {:email_address email-address}
                                  {:connection db-map}))))

(defn get-old-pw-hash
  "Get current password hash"
  ([db-map] (get-old-pw-hash db-map (friend-service/get-logged-in-username)))
  ([db-map email]
  (:password
   (first
    (get-password-query {:email_address email}
                        {:connection db-map})))))

(defn change-password
  "Change password"
  ([db-map new-password] (change-password db-map (friend-service/get-logged-in-username) new-password))
  ([db-map email-address new-password]
  (set-password-query! {:email_address email-address
                        :password new-password}
                       {:connection db-map})))

(defn set-account-type
  "Set account type to :user/admin, :user/free, etc."
  [db-map email-address account-type]
  (set-account-type-query! {:email_address email-address
                            :account_type account-type}
                           {:connection db-map}))
