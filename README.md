# CoffeeHouse
### Brought to you by The Codebenders
***


## Code Locations
* **Server Code**, built in Java Servlets, lives in /server
* **Web Client Code**, built in React, lives in /frontend


## Running Locally
Both the frontend and backend development servers can be run together
through the command `bash run-dev.sh` (assuming that one has installed
all frontend packages with `npm install` and has set their gcloud project
to `coffeehouse-step2020` through the command
`gcloud config set project coffeehouse-step2020`).

The outputs of both the frontend and backend development servers will be
sent to `stdout`, as well as logged to either the `server.log` or `frontend.log`
file.

Halting this process with Ctrl+C will stop both the frontend and backend
development servers.

To use, visit port 9000 on your localhost.


## Deploying
This project is composed of two seperate services. The frontend (known as the `default` service)
can be updated and deployed without touching the server. Similarly, the server
(known as the `server` service) can be updated and deployed without touching the frontend.

To deploy each service, follow the instructions in the README files in the frontend and server
directories.

### First Deployment
Upon the first deployment of the project, multiple steps must be done in order.

1.) Deploy the frontend service through the instructions in the README in the frontend directory.
2.) Deploy the server service through the instructions in the README in the server directory.
3.) In this directory, run the command `gcloud app deploy dispatch.yaml` to configure URL routing.


## Helpful Commands
Tests can be run for both the frontend and server. To run, reference the README files in the frontend
and server directories.
