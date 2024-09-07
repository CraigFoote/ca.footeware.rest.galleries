# Preparation

sudo mkdir -p /opt/rest.galleries/galleries

# Building

1. `mvn clean package`
1. `docker run --name rest.galleries -p 8000:8000 -d -v /opt/rest.galleries/galleries:/opt/rest.galleries/galleries -t rest.galleries:[version]`
1. `curl -v http://localhost:8000/galleries`
1. stop and remove container
1. tag **rest.galleries:[version]** as **craigfoote/rest.galleries:[version]**
1. push **craigfoote/rest.galleries:[version]**
1. tag **rest.galleries:[version]** as **craigfoote/rest.galleries:latest**
1. push **craigfoote/rest.galleries:latest**