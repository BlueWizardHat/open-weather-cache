#!/usr/bin/env bash

#
# This script can be used to run the open-weather-cache from a commandline
#

projectDir=$(readlink -f $(dirname "$0"))

[ -z "$JAVA_DEBUG"] && export JAVA_DEBUG=""
export SPRING_PROFILES="local"

while [[ $# -gt 0 ]]; do
  option="$1"
  shift

  case "$option" in
    build)
      echo -e "\n\e[1;33m* Building with maven:\e[0m \e[32m$projectDir\e[0m\n"
      cd $projectDir
      mvn clean install -am -pl open-weather-cache -DskipTests
      maven_exit_code=$?
      if [ $maven_exit_code != 0 ]; then
        exit $maven_exit_code
      fi
    ;;
    start|up)
      echo -e "\n\e[1;33m* Starting containers as daemons (profiles=$SPRING_PROFILES)\e[0m\n"
      cd $projectDir
      docker-compose up -d
    ;;
    restart)
      echo -e "\n\e[1;33m* Restarting backend\e[0m\n"
      cd $projectDir
      docker-compose kill open-weather-cache && docker-compose start open-weather-cache
    ;;
    log|logs)
      echo -e "\n\e[1;33m* Tailing logs for java projects only\e[0m\n"
      cd $projectDir
      docker-compose logs -f --tail=20 open-weather-cache
    ;;
    alllogs)
      echo -e "\n\e[1;33m* Tailing logs\e[0m\n"
      cd $projectDir
      docker-compose logs -f --tail=20
    ;;
    pause)
      echo -e "\n\e[1;33m* Stopping containers\e[0m\n"
      cd $projectDir
      docker-compose stop
    ;;
    stop|down)
      echo -e "\n\e[1;33m* Stopping and removing containers\e[0m\n"
      cd $projectDir
      docker-compose kill && docker-compose down
    ;;
    *)
      echo "Unknown arguments: $*"
      echo "Usage: $0 build|start|logs|pause|stop"
    ;;
  esac

done
