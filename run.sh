# Run a worker node in the distributed 
# TYPE can be one of the following:
#	node  -- to launch a worker nodes
#	client -- to launch a client that sends

TYPE=$1
NAME=$2
CONNECTS_TO=$3

IP=$(docker inspect --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $CONNECTS_TO)
echo "Attempt to join: <"$IP">"
echo "Start running:"
docker run -it --name $NAME fredzqm/dht $TYPE $IP