# Runs both dev servers at the same time, can be ended at the same time with ctrl+C
echo "Preview on http://localhost:9000"

PROJECT="coffeehouse-step2020"

if [[ $* == *-s* ]]
then
  (cd frontend; npm install) &&
  gcloud config set project $PROJECT &&
  GOOGLE_CLOUD_PROJECT=$PROJECT &&
  sudo sysctl -w fs.inotify.max_user_watches=100000
fi

trap "kill %1" SIGINT
(cd server/; mvn package appengine:run) | tee server.log | sed -e "s/^/\x1b[35m[BACKEND]\x1b[0m /" \
& (cd frontend/; npm run start-dev) | tee frontend.log | sed -e "s/^/\x1b[32m[FRONTEND]\x1b[0m /"
