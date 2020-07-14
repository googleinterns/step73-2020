# CoffeeHouse
### Brought to you by The Codebenders
***


## Code Locations
* **Server Code**, built in Java Servlets, lives in /server
* **Web Client Code**, built in React, lives in /frontend


## Running Locally
Both the frontend and backend development servers can be run together
through the command `bash run-dev.sh`.

The frontend and backend development servers will encounter errors when running
if it is not the case that one has installed all frontend packages with
`npm install` and set their gcloud project to `coffeehouse-step2020`.

To easily perform these operations, run the command `bash run-dev.sh -setup`
(or just `bash run-dev.sh -s`), which will install packages, set the gcloud
project, configure file watchers (to avoid frontend warnings), and start the
development servers. This only needs to be run once upon switching branches
or reopening the terminal.

The outputs of both the frontend and backend development servers will be
sent to `stdout`, as well as logged to either the `server.log` or `frontend.log`
file.

Halting this process with Ctrl+C will stop both the frontend and backend
development servers.

To use, visit http://localhost:9000.


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
