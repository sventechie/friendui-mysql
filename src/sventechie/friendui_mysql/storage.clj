(ns sventechie.friendui-mysql.storage
  (:require [sventechie.friendui-mysql.db :as db]
            [de.sveri.friendui.globals :as f-glob])
  (:gen-class))


(defrecord FrienduiStorage [db-map]
  f-glob/FrienduiStorage
  (account-activated? [this activation-id]
    (db/account-activated? activation-id {:connection db-map}))

  (activate-account [this activation-id]
    (db/activate-account activation-id {:connection db-map}))

  (create-user [this email password role activation-id]
    (db/create-new-user email password role activation-id {:connection db-map}))

  (get-all-users [this]
    (db/get-all-users {:connection db-map}))

  (get-user-for-activation-id [this id]
    (db/get-user-for-activation-id id {:connection db-map}))

  (update-user [this username data-map]
    (db/update-user username data-map {:connection db-map}))

  (username-exists? [this username]
    (db/username-exists? username {:connection db-map}))

  (get-loggedin-user-map [this]
    (db/get-loggedin-user-map {:connection db-map}))

  (get-old-pw-hash [this]
    (db/get-old-pw-hash {:connection db-map}))

  (change-password [this new-pw]
    (db/change-password new-pw {:connection db-map})))
