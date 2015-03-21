(ns sventechie.friendui-mysql.storage
  (:require [sventechie.friendui-mysql.db :as db]
            [de.sveri.friendui.globals :as f-glob])
  (:gen-class))


(defrecord FrienduiStorage [db-map]
  f-glob/FrienduiStorage
  (account-activated? [this activation-id]
    (db/account-activated? db-map activation-id))

  (activate-account [this activation-id]
    (db/activate-account db-map activation-id))

  (create-user [this email password role activation-id]
    (db/create-new-user db-map email password role activation-id))

  (get-all-users [this]
    (db/get-all-users db-map))

  (get-user-for-activation-id [this id]
    (db/get-user-for-activation-id db-map id))

  (update-user [this username data-map]
    (db/update-user db-map username data-map))

  (username-exists? [this username]
    (db/username-exists? db-map username))

  (get-loggedin-user-map [this]
    (db/get-loggedin-user-map db-map))

  (get-old-pw-hash [this]
    (db/get-old-pw-hash db-map))

  (change-password [this new-pw]
    (db/change-password db-map new-pw)))
