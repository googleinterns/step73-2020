# Runs both dev servers at the same time, can be ended at the same time with ctrl+C
echo "Preview on http://localhost:9000"

trap "kill %1" SIGINT
(cd frontend/; npm run start-dev) | tee frontend.log | sed -e "s/^/\x1b[32m[FRONTEND]\x1b[0m /" \
& (cd server/; mvn package appengine:run) | tee server.log | sed -e "s/^/\x1b[35m[BACKEND]\x1b[0m /"
