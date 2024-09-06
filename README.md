# Building
```
1. mvn clean install
2. docker compose --verbose up
3. http://localhost:8000/galleries
4. docker tag rest.galleries:[version] craigfoote/rest.galleries:[version]
5. docker push craigfoote/rest.galleries:[version]
6. docker tag rest.galleries:[version] craigfoote/rest.galleries:latest
7. docker push craigfoote/rest.galleries:latest
```

# Deploying
```
1. docker pull craigfoote/rest.galleries:latest
2. docker ps -a [note container id of previous deploy]
3. docker stop [container id]
4. docker rm [container id]
5. docker run --name rest.galleries -p 8000:8000 -d --restart unless-stopped -v /opt/rest.galleries:/opt/rest.galleries --memory="1g" --memory-swap="2g" -t craigfoote/rest.galleries:latest 
6. http://localhost:8000/galleries
```