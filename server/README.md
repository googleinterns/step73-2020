# CoffeeHouse Server

This project uses [Maven](https://maven.apache.org/) for building and deploying on
[Google App Engine](https://cloud.google.com/appengine)

Before running any server code, one must verify their gcloud project is set to
coffeehouse-step2020 through the command `gcloud config set project coffeehouse-step2020`


## Running Locally
Run the dev server with the command `mvn package appengine:run`

To use, visit http://localhost:8080


## Deploying
The server service (defined in `appengine-web.xml`) can be deployed with the
command `mvn package appengine:deploy`

To visit the deployed site, navigate to
[http://coffeehouse-step2020.appspot.com](http://coffeehouse-step2020.appspot.com/)


## Testing
To tests specific endpoints on the server, one can use the
[RestMan extension](https://chrome.google.com/webstore/detail/restman/ihgpcfpkpmdcghlnaofdmjkoemnlijdi?hl=en)
to construct POST requests with a custom body and header.
The response of that endpoint can then be received and analyzed by the extension.

Maven can run unit tests with the command `mvn test`.
These unit tests are written with the following resources:
-  [Junit4.13](http://junit.org/junit4/)
-  [Mockito](http://mockito.org/)
