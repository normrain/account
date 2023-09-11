# Account application
## Setup
The repository includes a docker-compose file to provision the infrastructure needed for the application.
After running the docker-compose file (`docker-compose up`), the application itself can be built.

The repository also includes a docker file. The docker file will build the application's jar file and launch the container.
The container itself exposes port `8080`, which will need to be bound before the API can be accessed.

At application start-up the Liquibase migrations will run to create the necessary tables.

N.B. The application-properties use `host.docker.internal`, to allow the application container to connect to the infrastructure. The project was built on Microsoft Windows using Docker for Desktop. This internal host may not work on Linux

## Documentation

The API documentation can be found at `localhost:[BOUND_PORT]/swagger-ui.html` once the application is started (`BOUND_PORT` being the port the container is accessible from)

## Important decisions and Assumptions

### Account
The `Account` saves only the customerId and country, it's own Id i auto-generated. The account does not save any Currency-related information, as it does not appear that the account itself needs to be aware of those.

The customerId was chosen to be of type `Long`. This was purely done for testing (and usability) purposes. In a real-life situation, this customerId would also be a Uuid, however, to test the API calls, it is much easier to just use a number.

It is possible to create multiple accounts for the same customer (even with the same currency). It is not unheard of for one customer to have multiple bank accounts in one country, I have therefore decided to also allow this to occur.


### Balance
The `Balance` saves the accountId, balance, currency and auto-generates its own Id. In the application itself however, only balance and currency are provided to the customer (via BalanceResponse model).

It is not possible for the same account (same accountId) to have multiple balances in the same currency. While this could be possible, it would need some changes in how the balances are fetched.
Conceptually however, I went with the "classic" bank account, where a multi-currency account only has one of each currency. Allowing the multiple balances with the same currency does resemble the "savings pot" many challenger banks are offering nowadays, but as there was no indication what the account would be used for, I assumed it should not be possible.
The balance will silently remove duplicates without throwing an error message. This is documented in the Swagger documentation (Although an error message could be thrown, it is technically not an invalid request, depending on interpretation)

### Transaction
Transactions are saved in the database, even if they would cause an exception (`InvalidBalanceException` to be precise). For a transaction record, I assume it would be necessary to keep failed transactions as well, however, the transaction object itself does not hold any information whether it succeeded or not.
Depending on the use case, an additional field should be added to the specification, or the code would need to be changed, to check if the transaction would succeed (check balance & funds).

If an account has no transactions, it will return an empty list rather than a 404, as an empty list is an appropriate representation of the resource.


### Log
This was not mentioned in the specifications, and is not used in any real sense in the application.
The log listens to the RabbitMQ queue, and saves eventType & objectIds of the events. 
This was created to test the RabbitMq messaging, and to show that it works.
There is no endpoint to get that data, it only saves events.

### Controller

I have chosen to only use one controller, even though there could have been the possibility to use two (account-controller & transaction-controller)
This was done due to transactions not making sense to exist outside of them being linked to an account.

There are no endpoints for balances as there is no need to create or modify them directly.


### Database
MyBatis was given as a requirement to be used. I use the autoconfiguration, as this should be enough for what I require.
I have not used MyBatis in my work so far (I used mostly Hibernate & Exposed for newer Kotlin projects), and I saw that there is XMl mapping & configurations, however, for the queries I require, I decided to only use Annotation-based mapping.

For database migrations, I have used Liquibase. The other option I had in mind was flyway, which I use at work. However, to experience something new, I have decided to go with Liquibase.

### RabbitMQ
RabbitMQ (as well as the management UI), has been configured and a service has been create to send out messages. 
This service currently only sends out small events during every creation or update event.
The EventLog listens to the queue and saves the event.
This shows that messages can be put into the RabbitMQ queue, and could therefore be consumed by other listeners (microservices)

### Enums
The direction of the transaction as well as the currency was selected to be enums, as opposed to create database tables for them.
While creating database tables would have helped to normalise the database, for what I wanted to achieve, it would have seemed to be a slight over-engineering of the solution.
The tradeoff for using enums however, is that the error message does not look nice, as De-serialization occurs before validation, and the Exception that is thrown, does not offer a good-looking message. 


### Test coverage
According to IntellJ Idea, the test coverage currently is at 
`Class: 100%, Method: 90% and Line: 93%`

### Performance
Considering my hardware (and some previous experience), I would have initially estimated `1000 requests/s` would be the limit. However, I have ran a quick test with JMeter to check what the actual number is, 
and I found out that around `650 requests/s` is the number at which the application (ran locally) starts to degrade.

### Scaling horizontally
Before being able to scale horizontally, it would need to be checked if there are any bottlenecks (either in the code or the hardware) that would inhibit the actual scaling.

In the specific case of this application (but also generally), a read-replica would be advisable to limit database requests which can be read-only. 
Additionally, caching could be used to increase performance. In general, there could also be replicas for the database on different servers, or the data itself could be partitioned, 
to decrease the load on any single server. 

The application code itself would need to be optimised (for concurrency/ parallelism) and maybe include for asynchronous processes 
(in this application, there doesn't seem to be a workflow which stands out as being convertable to an asynchronous one, 
but that would depend on the whole picture)

Additionally, it would require a load-balancer to evenly distribute the load between running micro-services.

