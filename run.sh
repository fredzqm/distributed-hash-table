NAME=$1
CONNECTS_TO=$2

IP= docker inspect --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $CONNECTS_TO

docker run --name $NAME fredzqm/dht ${IP:-www.google.com}