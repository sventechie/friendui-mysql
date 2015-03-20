(ns sventechie.friendui-mysql.storage
  (:require [sventechie.friendui-mysql.db :as db]
            [de.sveri.friendui.globals :as f-glob])
  (:gen-class))


(defrecord FrienduiStorage []
  f-glob/FrienduiStorage
  (account-activated? [this activation-id])

  (activate-account [this activation-id]
    (db/activate-account activation-id))

  (create-user [this email password role activation-id]
    (db/create-new-user email password role activation-id))

  (get-all-users [this]
    (db/get-all-users))

  (get-user-for-activation-id [this id]
    (db/get-user-for-activation-id id))

  (update-user [this username data-map]
    (db/update-user username data-map))

  (username-exists? [this username]
    (db/username-exists? username))

  (get-loggedin-user-map [this]
    (db/get-loggedin-user-map))

  (get-old-pw-hash [this]
    (db/get-old-pw-hash))

  (change-password [this new-pw]
    (db/change-password new-pw)))
