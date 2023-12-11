# Mintos Accounts

Restful Webservice that exposes API Endpoints to retrieve Accounts and Transaction details of Client based on CientId

## Getting Started

Follow these instructions to get the project up and running.

### Prerequisites

- Java SDK installed : Java 17
- Gradle build tool :https://github.com/harikayadav/Mintos.git gradle-8.5-bin
- Eclipse/Intellij IDE

### Installation

1. Clone the repository: `https://github.com/harikayadav/Mintos.git`
2. Navigate to the project directory.
3. Build the project: ./gradlew build

## Usage

This application provides the below APIs:

1.Given a client identifier, return a list of accounts (each client might have 0 or more
accounts with different currencies)

`http://localhost:8080/api/accounts/getAccounts/{clientId}`

Sample Request : http://localhost:8080/api/accounts/getAccounts/1

2.Given an account identifier, return transaction history (last transactions come first)
and support result paging using “offset” and “limit” parameters

`http://localhost:8080/api/accounts/getTransactions/{accountId}`

Sample Request : http://localhost:8080/api/accounts/getTransactions/987007

3.Transfer funds between two accounts indicated by identifiers

`http://localhost:8080/api/accounts/transfer?fromAccountId={fromAccountId}&toAccountId={toAccountId}&amount={amount}&exchangeCurrency={exchangeCurrency}`

Sample Request : http://localhost:8080/api/accounts/transfer?fromAccountId=987003&toAccountId=987005&amount=100&exchangeCurrency=EUR

##Note:

H2 In-Memory Embedded database has been used for test data.

schema.sql file consists of create queries for table creation.

data.sql file consists of insert queries for records insertion.
