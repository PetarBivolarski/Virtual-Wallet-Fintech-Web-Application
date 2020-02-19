USE virtual_wallet;

CREATE TABLE users
(
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    -- Spring security
    username               VARCHAR(50) NOT NULL,
    password               VARCHAR(68) NOT NULL,
    enabled                TINYINT(1)  NOT NULL,
    -- Other columns
    phone_number           VARCHAR(23),
    email                  VARCHAR(254),
    first_name             VARCHAR(50),
    last_name              VARCHAR(50),
    photo                  MEDIUMBLOB,
    blocked                TINYINT(1)  NOT NULL DEFAULT 0,
    confirmed_registration TINYINT(1)  NOT NULL DEFAULT 0,
    default_wallet_id      INT,
    joined_date            DATETIME,
    invited_users          INT                  DEFAULT 0,
    CONSTRAINT uk_users_username UNIQUE KEY (username),
    CONSTRAINT uk_users_email UNIQUE KEY (email),
    CONSTRAINT uk_users_phone_number UNIQUE KEY (phone_number),
    CONSTRAINT chk_users_phone_number CHECK (phone_number REGEXP '\\(\\+[0-9]{1,5}\\)[0-9]{3,15}'),
    CONSTRAINT chk_users_email CHECK (email REGEXP '[^@]+@[^\\.]+\\..+'),
    CONSTRAINT chk_users_invited_users CHECK (invited_users <= 3)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE authorities
(
    username  VARCHAR(50),
    authority VARCHAR(50),
    CONSTRAINT uk_authorities_username_authority UNIQUE KEY (username, authority),
    CONSTRAINT fk_authorities_username FOREIGN KEY (username) REFERENCES users (username)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE user_verification_tokens
(
    id           BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    token        VARCHAR(255),
    created_date DATETIME,
    expiry_date  DATETIME,
    user_id      INT NOT NULL,
    CONSTRAINT fk_user_verification_tokens_user_id FOREIGN KEY (user_id) REFERENCES users (id)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE user_invitation_tokens
(
    id           BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    token        VARCHAR(255),
    created_date DATETIME,
    expiry_date  DATETIME,
    owner_id     INT NOT NULL,
    email        VARCHAR(254),
    used         TINYINT(1) DEFAULT 0,
    CONSTRAINT fk_user_invitation_tokens_owner_id FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT chk_user_invitation_tokens_email CHECK (email REGEXP '[^@]+@[^\\.]+\\..+')
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE payment_instruments
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50)             NOT NULL,
    owner_id        INT                     NOT NULL,
    instrument_type ENUM ('CARD', 'WALLET') NOT NULL,
    CONSTRAINT fk_payment_instruments_owner_id FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT uk_payment_instruments_owner_id_instrument_type_name UNIQUE KEY (owner_id, instrument_type, name)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE cards
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    card_number     VARCHAR(19),
    cardholder_name VARCHAR(40),
    expiration_date CHAR(5),
    card_csv        VARCHAR(68),
    deleted         TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_cards_id FOREIGN KEY (id) REFERENCES payment_instruments (id),
    CONSTRAINT uk_cards_card_number UNIQUE KEY (card_number),
    CONSTRAINT chk_cards_card_number CHECK (deleted <> 0 OR
                                            (card_number IS NOT NULL AND card_number REGEXP '([0-9]{4}-){3}[0-9]{4}')),
    CONSTRAINT chk_cards_cardholder_name CHECK (deleted <> 0 OR
                                                cardholder_name IS NOT NULL AND cardholder_name REGEXP '[A-Za-z ]{2,40}'),
    CONSTRAINT chk_cards_expiration_date CHECK (deleted <> 0 OR
                                                expiration_date IS NOT NULL AND
                                                expiration_date REGEXP '(0[1-9]|1[0-2])/[0-9]{2}'),
    CONSTRAINT chk_cards_card_csv CHECK (deleted <> 0 OR card_csv IS NOT NULL)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE wallets
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    saldo   DECIMAL(19, 4) NOT NULL DEFAULT 0,
    deleted TINYINT(1)     NOT NULL DEFAULT 0,
    CONSTRAINT fk_wallets_id FOREIGN KEY (id) REFERENCES payment_instruments (id)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

ALTER TABLE users
    ADD CONSTRAINT fk_users_default_wallet_id FOREIGN KEY (default_wallet_id) REFERENCES wallets (id);

CREATE TABLE transactions
(
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    date_time               DATETIME                                                                                           DEFAULT CURRENT_TIMESTAMP,
    transfer_amount         DECIMAL(19, 4)                                                                            NOT NULL,
    sender_instrument_id    INT                                                                                       NOT NULL,
    recipient_instrument_id INT                                                                                       NOT NULL,
    description             VARCHAR(255),
    transaction_type        ENUM ('SMALL_AMOUNT', 'LARGE_VERIFIED', 'LARGE_UNVERIFIED', 'CARD_TO_WALLET', 'DONATION') NOT NULL DEFAULT ('SMALL_AMOUNT'),
    with_donation           TINYINT(1)                                                                                         DEFAULT 0,
    CONSTRAINT fk_transactions_sender_instrument_id FOREIGN KEY (sender_instrument_id) REFERENCES payment_instruments (id),
    CONSTRAINT fk_transactions_recipient_instrument_id FOREIGN KEY (recipient_instrument_id) REFERENCES payment_instruments (id),
    CONSTRAINT chk_transactions_payment_amount_positive CHECK (transfer_amount > 0)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE transaction_verification_tokens
(
    id             BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    token          VARCHAR(255),
    created_date   DATETIME,
    expiry_date    DATETIME,
    transaction_id INT NOT NULL,
    CONSTRAINT fk_transaction_verification_tokens_transaction_id FOREIGN KEY (transaction_id) REFERENCES transactions (id)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE inspirational_quotes
(
    id     int AUTO_INCREMENT PRIMARY KEY,
    text   VARCHAR(350),
    author VARCHAR(50)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;


# CREATE TABLE categories
# (
#     id         INT AUTO_INCREMENT PRIMARY KEY,
#     name       VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
#     creator_id INT                                                          NOT NULL,
#     CONSTRAINT fk_categories_creator_id FOREIGN KEY (creator_id) REFERENCES users (id),
#     CONSTRAINT uk_categories_creator_id_name UNIQUE KEY (creator_id, name)
# );

# ALTER TABLE users
#     CHANGE COLUMN phone_number phone_number varchar(23);
#
# ALTER TABLE users
#     CHANGE COLUMN email email varchar(254);
#
# ALTER TABLE transactions
#     CHANGE COLUMN transaction_type
#         transaction_type ENUM ('SMALL_AMOUNT', 'LARGE_VERIFIED', 'LARGE_UNVERIFIED', 'CARD_TO_WALLET') NOT NULL DEFAULT ('SMALL_AMOUNT');
#
#
# ALTER TABLE users
#     ADD joined_date  DATETIME,
#     ADD poster_photo MEDIUMBLOB;
#
# ALTER TABLE cards
#     DROP CONSTRAINT chk_cards_card_number;
#
# ALTER TABLE cards
#     ADD
#         CONSTRAINT chk_cards_card_number CHECK (deleted = 1 OR
#                                                 (card_number IS NOT NULL AND card_number REGEXP '([0-9]{4}-){3}[0-9]{4}'));
#
# alter table payment_instruments
#     drop column currency;

# CREATE TABLE donation_projects
# (
#     id    int PRIMARY KEY AUTO_INCREMENT,
#     name  VARCHAR(100),
#     saldo DECIMAL(19, 4) NOT NULL DEFAULT 0,
#     active TINYINT(1) NOT NULL DEFAULT 0,
#     CONSTRAINT uk_donation_projects_name UNIQUE KEY (name)
# );
#
# CREATE table users_donations
# (
#     id                  INT PRIMARY KEY,
#     user_id             int,
#     donation_project_id int,
#     CONSTRAINT user_id_foreign_key FOREIGN KEY (user_id) REFERENCES users (id),
#     CONSTRAINT donation_id_foreign_key FOREIGN KEY (donation_project_id) REFERENCES donation_projects (id)
# );

# ALTER TABLE users
#     DROP COLUMN poster_photo;
#
# ALTER TABLE transactions
#     ADD with_donation TINYINT(1) DEFAULT 0;
#
# alter table transactions
#     modify transaction_type enum ('SMALL_AMOUNT', 'LARGE_VERIFIED', 'LARGE_UNVERIFIED', 'CARD_TO_WALLET', 'DONATION') default 'SMALL_AMOUNT' not null;
#
# CREATE TABLE inspirational_quotes
# (
#     id     int AUTO_INCREMENT PRIMARY KEY,
#     text   nvarchar(350),
#     author nvarchar(50)
# );

# insert into inspirational_quotes (text, author)
# VALUES ('The past has no power over the present moment.', 'Eckhart Tolle'),
#        ('Realize deeply that the present moment is all you have. Make the NOW the primary focus of your life.',
#         'Eckhart Tolle'),
#        ('Sometimes letting things go is an act of far greater power than defending or hanging on.', 'Eckhart Tolle'),
#        ('Life is the dancer and you are the dance.', 'Eckhart Tolle'),
#        ('Whatever the present moment contains, accept it as if you had chosen it.', 'Eckhart Tolle'),
#        ('Whatever you fight, you strengthen, and what you resist, persists.', 'Echkart Tolle'),
#        ('Instead of fighting the darkness, you bring in the light.', 'Eckhart Tolle'),
#        ('Desire is the starting point of all achievement, not a hope, not a wish, but a keen pulsating desire which transcends everything.',
#         'Napoleon Hill'),
#        ('The starting point of all achievement is desire.', 'Napoleon Hill'),
#        ('Whatever the mind can conceive and believe, it can achieve.', 'Napoleon Hill'),
#        ('You are the master of your destiny. You can influence, direct and control your own environment. You can make your life what you want it to be.',
#         'Napoleon Hill'),
#        ('If you can''t do great things, do small things in a great way.', 'Napoleon Hill'),
#        ('Do not wait: the time will never be ''just right''. Start where you stand, and work whatever tools you may have at your command and better tools will be found as you go along.',
#         'Napoleon Hill'),
#        ('Set your mind on a definite goal and observe how quickly the world stands aside to let you pass.',
#         'Napoleon Hill'),
#        ('The way of success is the way of continuous pursuit of knowledge.', 'Napoleon Hill'),
#        ('The man who does more than he is paid for will soon be paid for more than he does.', 'Napoleon Hill'),
#        ('Every adversity, every failure, every heartbreak, carries with it the seed of an equal or greater benefit.',
#         'Napoleon Hill'),
#        ('Wise men, when in doubt whether to speak or to keep quiet, give themselves the benefit of the doubt, and remain silent.',
#         'Napoleon Hill'),
#        ('If you must speak ill of another, do not speak it ...', 'Napoleon Hill'),
#        ('You may encounter many defeats, but you must not be defeated. In fact, it may be necessary to encounter the defeats, so you can know who you are, what you can rise from, how you can still come out of it.',
#         'Maya Angelou');

# ALTER TABLE donation_projects
#     ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 0,
#     ADD CONSTRAINT uk_donation_projects_name UNIQUE KEY (name);
#
# UPDATE donation_projects
# SET active = 1
# WHERE name = 'SOSChildrenVillages';
#
# ALTER TABLE users
#     ADD COLUMN deleted tinyint(1) DEFAULT 0;

# CREATE TABLE user_invitation_token
# (
#     id           BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
#     token        nvarchar(255),
#     created_date DATETIME,
#     expiry_date  DATETIME,
#     owner_id     INT NOT NULL,
#     email        VARCHAR(254),
#     CONSTRAINT fk_user_invitation_owner_id FOREIGN KEY (owner_id) REFERENCES users (id),
#     CONSTRAINT email_format_constraint CHECK (email REGEXP '[^@]+@[^\\.]+\\..+')
# );
#
# ALTER TABLE users
# ADD COLUMN invited_users int DEFAULT 0,
# ADD CONSTRAINT constraint_invited_users CHECK (invited_users <= 3);
#
# ALTER TABLE user_invitation_token
# ADD COLUMN used tinyint(1) DEFAULT 0;
#
#
# INSERT INTO users(username, password, enabled, phone_number, email, blocked, confirmed_registration, joined_date)
# VALUES ('SOSChildrenVillages', '$2a$10$.c/8A3h/SemNhvHoT1j8hO0YKupZUiwMc8lC3U7nBr36i0Vqrx6oS', 1, '(+359)888454154', 'virtualwalletjava@gmail.com', 0, 	1, '2020-02-10 15:01:31');
#
# INSERT INTO authorities(username, authority)
# VALUES ('SOSChildrenVillages', 'ROLE_USER');
#
# INSERT INTO payment_instruments(name, owner_id, instrument_type)
# SELECT 'SOSChildrenVillages Donation Wallet', id, 'WALLET'
# FROM users WHERE username = 'SOSChildrenVillages';
#
# INSERT INTO wallets(id)
# SELECT id FROM payment_instruments WHERE name = 'SOSChildrenVillages Donation Wallet';
#
# UPDATE users
# SET default_wallet_id = (SELECT id FROM payment_instruments WHERE name = 'SOSChildrenVillages Donation Wallet')
# WHERE username = 'SOSChildrenVillages';
#
#
# ALTER TABLE cards
#     DROP CONSTRAINT chk_cards_cardholder_name,
#     DROP CONSTRAINT chk_cards_card_expiration_date;
#
# ALTER TABLE cards
#     CHANGE COLUMN cardholder_name cardholder_name VARCHAR(40),
#     CHANGE COLUMN expiration_date expiration_date CHAR(5),
#     CHANGE COLUMN card_csv card_csv VARCHAR(68),
#     ADD CONSTRAINT chk_cards_cardholder_name CHECK (deleted <> 0 OR cardholder_name IS NOT NULL AND cardholder_name REGEXP '[A-Za-z ]{2,40}'),
#     ADD CONSTRAINT chk_cards_expiration_date CHECK (deleted <> 0 OR expiration_date IS NOT NULL AND expiration_date REGEXP '(0[1-9]|1[0-2])/[0-9]{2}'),
#     ADD CONSTRAINT chk_cards_card_csv CHECK (deleted <> 0 OR card_csv IS NOT NULL);
#
# ALTER TABLE authorities DROP CONSTRAINT fk_authorities_username;
# ALTER TABLE authorities CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# ALTER TABLE authorities ADD CONSTRAINT fk_authorities_username FOREIGN KEY (username) REFERENCES users (username);