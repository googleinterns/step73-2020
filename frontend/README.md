# CoffeeHouse Frontend

This project uses [React](https://facebook.github.io/react/) and
[Typescript](https://www.typescriptlang.org/) for the frontend. 

Before running any code, one must use `npm install` to install the required dependencies.


## Running Locally
Run the dev server with the command `npm run start-dev`

To use, visit port 9000 on your localhost

If you see any errors that say "System limit for number of file watchers reached",
you can run the following command to temporarily increase that limit -
`sudo sysctl -w fs.inotify.max_user_watches=100000`


## Deploying
The frontend service (know as the `default` service in `app.yaml`) can
be deployed with the command `npm run build && gcloud app deploy`

To visit the deployed site, navigate to
[http://coffeehouse-step2020.appspot.com](http://coffeehouse-step2020.appspot.com/)


## Testing
Unit tests can be ran with the command `npm run test`

The unit tests are written with:
-  [Jest](https://facebook.github.io/jest/)


---
### All commands

Command | Description
--- | ---
`npm run start-dev` | Build app continuously (HMR enabled) and serve @ `http://localhost:9000`
`npm run start-prod` | Build app once (HMR disabled) to `/dist/` and serve @ `http://localhost:9000`
`npm run build` | Build app to `/dist/`
`npm run test` | Run tests
`npm run lint` | Run Typescript linter
`npm run lint --fix` | Run Typescript linter and fix issues
`npm run start` | (alias of `npm run start-dev`)
