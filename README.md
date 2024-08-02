# Building

1. mvn clean install to generate the docker image
1. docker tag rest.galleries:[version] craigfoote/rest.galleries:[version]
1. docker push craigfoote/rest.galleries:[version]
1. docker tag rest.galleries:[version] craigfoote/rest.galleries:latest
1. docker push craigfoote/rest.galleries:latest

# Deploying

1. docker pull craigfoote/rest.galleries:latest
1. docker ps -a [note container id of previous deploy]
1. docker stop [container id]
1. docker rm [container id]
1. docker run 
--name rest.galleries 
-p 8000:8000 
-d 
--restart unless-stopped 
-v /opt/galleries:/opt/galleries 
--memory="1g" 
--memory-swap="2g" 
-t craigfoote/rest.galleries:latest 
1. http://localhost:8000/galleries
