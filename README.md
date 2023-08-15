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

# Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.1/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#using.devtools)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#web)

# Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
