# payment-service
Questo repository contiene un docker-compose per il running del microservizio
payment-service.

## Running
Questo progetto è configurato per effettuare il running del microservizio 
payment-service con docker-compose.
Quindi in sostanza basta lanciare:

      $ docker-compose up
	  
## NOTE
La property 

      spring.zipkin.base-url = http://192.168.178.30:9411/

presenta come host.docker.internal del container l'ip specificato
Da modificare opportunamente in base al caso oppure definire un network
comune sui docker-compose dei componenti di tutti repository.
