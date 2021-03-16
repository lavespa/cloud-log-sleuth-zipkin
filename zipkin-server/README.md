# zipkin-server
Questo repository contiene un docker-compose per il running di zipkin server attraverso una
image server più leggera(slim) formata dall'UI di Zipkin e le Api feature che ci permettono di
sfruttare le varie funzionalità core ed infine il solo supporto memory.
Per informazioni su documentazione e image appropriate del project Zipkin:

* Documentazione Zipkin: https://zipkin.io/
* Repository github: https://github.com/openzipkin-attic/docker-zipkin

## Running
Questo progetto è configurato per effettuare il running di Zipkin con docker-compose.
Quindi in sostanza basta lanciare:

      $ docker-compose up
	
Per visualizzare l'interfaccia grafica di Zipkin in $(docker ip):9411
	  
