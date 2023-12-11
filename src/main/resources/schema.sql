DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS client;

CREATE TABLE client (
    id INT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE account (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    currency VARCHAR(255) NOT NULL,
    account_number VARCHAR(255) NOT NULL UNIQUE,
	balance DECIMAL(10, 2)  CHECK (balance >= 0),
    FOREIGN KEY (client_id) REFERENCES client (id)

);

CREATE TABLE transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2),
    trans_date DATE NOT NULL, 
    FOREIGN KEY (account_number) REFERENCES account (account_number)
);