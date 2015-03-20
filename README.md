# friendui-mysql

A Clojure library designed to store account information for Clojure Friend in MySQL.
This is an implementation of the storage protocol in [friend-ui](https://github.com/sveri/friend-ui/) with SQL.


## Usage

Currently very rough. Follow [setup instructions for FriendUI](https://github.com/sveri/friend-ui/) and
add a [YeSQL database spec](https://github.com/krisajenkins/yesql/):

Load the `schema.sql` file from resources into your database. It creates tables with prefix `azn_`.

```Clojure
   :database { :user "my_username"
               :password "my_password"
               :subname "//127.0.0.1/database_name?zeroDateTimeBehavior=convertToNull"
               :port "3306"
               :subprotocol "mysql" }
```

This library is not yet available on clojars, so you'll need to clone the repo and `lein install`.

I have attempted to use fairly generic SQL so this could be easily modified for PostgreSQL or other databases.

## License

Copyright © 2015 Sven Pedersen

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
