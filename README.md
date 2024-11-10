<style>
	body {
		background-color: #2c2c2c;
		font-size: 22px;
		color: #ddeeee;
	}
	h1,h2,h3 {
		color: #569cd6;
	}
	code {
		color: #ff8e3c;
	}
</style>

# Preparation

1. `sudo mkdir -p /opt/rest.galleries/logs`
1. `sudo mkdir -p /opt/rest.galleries/galleries`
1. `sudo chown craig:craig -R /opt/rest.galleries`
1. Fill `galleries` with folders of pictures.

# Certificate
1. `openssl req -newkey rsa:2048 -keyout footeware.ca.test.key -x509 -days 365 -out footeware.ca.test.crt`
1. `openssl rsa -in footeware.ca.test.key -aes256 -out aes.pem`
1. Place key and its AES version in /src/main/resources along with the cert.

# Building

1. `mvn clean package`
1. `docker run --name rest.galleries -p 8000:8000 -d -v /opt/rest.galleries/galleries:/opt/rest.galleries/galleries -t rest.galleries:[version]`
1. container should start and exit immediately because it can't find the production cert. To see the error, re-start the container thru the Docker perspective in eclipse.
1. tag **rest.galleries:[version]** as **craigfoote/rest.galleries:[version]**
1. push **craigfoote/rest.galleries:[version]**
1. tag **rest.galleries:[version]** as **craigfoote/rest.galleries:latest**
1. push **craigfoote/rest.galleries:latest**