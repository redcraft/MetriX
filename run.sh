#!/bin/bash
export API_KEY=
export API_SECRET=
export FIXED_THREAD_POOL_SIZE=3
export PRODUCTION_BUILD=false

if [[ -z $API_KEY || -z $API_SECRET || -z $FIXED_THREAD_POOL_SIZE ]]
then 
	echo "[ERROR] Dropbox API_KEY and API_SECRET are not set."; 
else 
	mvn clean && npm install && grunt && mvn jetty:run-war
fi
