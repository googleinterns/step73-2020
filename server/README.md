# CoffeeHouse Server
This project uses [Maven](https://maven.apache.org/) for building and deploying on [Google App Engine](https://cloud.google.com/appengine)

## Running Locally
Run the dev server with the command `mvn package appengine:run`
To use, visit port 8080 on your local host

## Deploying
Maven can also deploy the project with the command `mvn package appengine:deploy`
To visit the deployed site, navigate to [http://coffeehouse-step2020.appspot.com](http://coffeehouse-step2020.appspot.com/)

## Testing
Maven can run tests with the command `mvn test`
The tests are written with the following resources:
-   [Junit4.13](http://junit.org/junit4/)
-   [Mockito](http://mockito.org/)
