<style>
	body {
		background-color: #2c2c2c;
		font-size: 18px;
		color: #ddeeee;
	}
	h1,h2,h3 {
		color: #569cd6;
	}
	a {
		color: #0da6fa;
	}
	code {
		color: #ff8e3c;
	}
</style>

# Preparation

1. `sudo mkdir -p /opt/rest.galleries/logs`
1. `sudo mkdir -p /opt/rest.galleries/galleries`
1. `sudo chown craig:craig -R /opt/rest.galleries`
1. Fill `galleries` folder with folders of pictures.

# Development Certificate
1. `openssl req -newkey rsa:2048 -keyout footeware.ca.test.key -x509 -days 365 -out footeware.ca.test.crt`
1. `openssl rsa -in footeware.ca.test.key -aes256 -out aes.pem`
1. Place cert, key and its AES version in /src/main/resources.

# Building

1. `mvn clean package` or use the `-BUILD` eclipse launch config
1. run as Spring Boot app using `-RUN` eclipse launch config. It uses the `-dev` profile that uses the self-signed cert.
1. [https://localhost:8000/galleries](https://localhost:8000/galleries)
1. `docker run --env JAVA_OPTS="-Dspring.profiles.active=dev" --name rest.galleries -p 8000:8000 -v /opt/rest.galleries/galleries:/opt/rest.galleries/galleries -t rest.galleries:[version]`
1. [https://localhost:8000/galleries](https://localhost:8000/galleries)

# Releasing

1. commit changes to git and create tag
1. `docker tag rest.galleries:[version] craigfoote/rest.galleries:[version]`
1. `docker tag rest.galleries:[version] craigfoote/rest.galleries:latest`
1. `docker push craigfoote/rest.galleries:[version]`
1. `docker push craigfoote/rest.galleries:latest`
