-- name: check-account-active-query
-- Check if account is activated
SELECT verified
FROM azn_account
WHERE azn_account.verify_uuid = :activation_id

-- name: activate-account-query!
-- Check if account is activated
UPDATE azn_account
SET verified = true
WHERE azn_account.verify_uuid = :activation_id

-- name: list-accounts-query
-- List all users
SELECT *
FROM azn_account

-- name: list-all-accounts-query
-- List all users
SELECT verified, email_address, azn_acl_role.name AS role
FROM azn_account
JOIN azn_acl_role ON (azn_acl_role.id = azn_account.role_id)

-- name: get-account-by-email-query
-- Find user by email_address
SELECT *
FROM azn_account
WHERE azn_account.email_address = :email_address

-- name: get-account-by-activation-query
-- Find user by activation_id
SELECT verified, email_address, azn_acl_role.name AS role
FROM azn_account
JOIN azn_acl_role ON (azn_acl_role.id = azn_account.role_id)
WHERE azn_account.verify_uuid = :activation_id

-- name: create-account-query<!
-- Creates a user account and returns account_id
INSERT INTO azn_account ( email_address, password, created_on, verified, verify_uuid, role_id )
VALUES ( :email_address, :password, NOW(), false, :activation_id,
         ( SELECT id FROM azn_acl_role WHERE name = :account_type ))

-- name: update-account-query!
-- Updates user account by email, returns rows affected
UPDATE azn_account
SET role_id = (SELECT id FROM azn_acl_role WHERE name = :account_type),
    verified = :activated
WHERE azn_account.email_address = :email_address

-- name: set-account-type-query!
-- Sets user group/type
UPDATE azn_account
SET role_id = (SELECT id FROM azn_acl_role WHERE name = :account_type)
WHERE azn_account.email_address = :email_address

-- name: get-password-query
-- Find password by email_address
SELECT password
FROM azn_account
WHERE azn_account.email_address = :email_address

-- name: set-password-query!
-- Change password
UPDATE azn_account
SET password = :password
WHERE azn_account.email_address = :email_address
