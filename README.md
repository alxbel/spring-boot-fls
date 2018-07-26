# Spring Boot REST API example
## Preconditions
    $ sudo apt update
    $ sudo apt install postgresql postgresql-contrib
    $ sudo -u postgres createuser `whoami` -s
    $ createdb store
    $ psql
        create user store with password 'qwerty';
        grant all on database store to store;
        \q
    $ mvn clean flyway:migrate
## Run
    $ mvn package
    $ java -jar target/fls-store-1.0-SNAPSHOT.jar
    
    Get all applications of the contact:
    $ curl 'localhost:8080/store/applications/1'
    
    Get the latest application:
    $ curl 'localhost:8080/store/applications/latest'
    
    Add new application for the contact:
    $ curl -d '{"dtCreated":"2018-06-01 13:00", "productName":"product v3"}' -H "Content-Type: application/json" -X POST localhost:8080/store/applications/add/1
    
    Update application by contact_id and application_id:
    $ curl -d '{"dtCreated":"2018-07-15 13:00", "productName":"updated product"}' -H "Content-Type: application/json" -X PUT localhost:8080/store/applications/update/2/4
    
    To see changes:
    $ psql store
        select * from application;
## Test
    $ mvn test

    


