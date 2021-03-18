# cloud-log-sleuth-zipkin
Come utilizzare Sleuth e Zipkin per aggiungere informazioni al tracing distribuito nei logs.

## TRACING DISTRIBUITO

Supponendo di dover costruire un architettura distribuita orientata ad esempio a 
microservizi è forte l'esigenza di gestire il tracing log delle chiamate per poter
monitorare non solo i log ma quanto l'architettura risulta scalabile. 
Nel project abbiamo definito 2 ms order-service e payment-service e abbiamo suuposto che
il servizio di creazione dell'ordine invochi il servizio di pagamento. Il nostro scopo
è quello di monitorare i microservizi e capire se ci sono porblemi di scalabilità.
In base a questo scenario, che è semplificato(immaginate n servizi), ci troviamo di fronte ad
una serie di problematiche:

* Anche ne caso di utilizzo di un componente di aggregazione dei log dei diversi ms, tipo ELK,
  è difficile collegare le Trace di differenti ms che lavorano per gestire la stessa richiesta;
* Se una determinata request viene organizzata in sotto richieste come nei ms implementati, 
  risulta difficile verificare e individuare  quale sotto-richiesta risulta lenta e quindi causa dei ritardi nella risposta finale.
  
  Sleuth e Zipkin ci permettono di risolvere questi problemi.
  
## SLEUTH

Sleuth e’ una libreria di Spring Cloud che implementa il sistema di logging con il pattern Distributed tracing. 
A seguire le operazioni salienti che esso compie:

* assegna un identificativo univoco detto Trace Id  per ogni richiesta esterna del client;
* assegna un identificativo detto Span Id per ogni sotto-richiesta gestita da un altro servizio 
  che entra in gioco per soddisfare la richiesta originale del client;
* integra tutte le trace di logging dei nostri microservizi con la coppia <Trace Id, Span Id>.

A titolo di esempio allego un immagine che rende molto l'idea:

<img width="880" alt="Diagram TraceId con Sleuth" src="https://github.com/lavespa/cloud-log-sleuth-zipkin/blob/main/image/tracing_sleuth.png">

## ZIPKIN

Zipkin è un componente sw open source che ci permette di inviare, ricevere, archiviare e visualizzare i dettagli delle trace 
e relative span. In questo modo il team di sviluppo che progetta l'architettura a ms  riesce a coordinare meglio le attività 
tra i vari servizi e ottenere una comprensione molto più approfondita di ciò che sta accadendo nei nostri servizi. 
Zipkin fornisce per questo proprio una dashboard per analizzare le trace nel dettaglio.

Ad esempio possiamo interrogare la GUI di Zipkin per Trace Id, andando a leggere i tempi di ogni span id e quindi sotto-richiesta.

## INSTALLAZIONE SLEUTH/ZIPKIN SU K8S

Integriamo le immagini dei nostri microservizi order-service e payment-service in un cluster K8s che utilizzano Sleuth e 
installiamo anche il servizio di Zipkin.
Creiamo per iniziare i file yaml contenenti rispettivamente la configurazione dei pod e service per i nostri due microservizi 
order-service e payment-service sfruttando le nuove immagini create con docker e due variabili di ambiente rappresentate dall'IP
e dalla porta di Zipkin:

       docker build -t intrieri/order-ms:1.0.0 .
	   docker build -t intrieri/payment-ms:1.0.0 .

#### order-ms-deployment.yml

```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-ms-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order-ms
  template:
    metadata:
      labels:
        app: order-ms
    spec:
      containers:
      - name: order-ms
        image: intrieri/order-ms:1.0.0
        ports:
        - containerPort: 8091
        env:
          - name: ZIPKIN_IP
            value: zipkin-ms-service
          - name: ZIPKING_PORT
            value: '9411'

```

#### order-ms-services.yml

```yml
apiVersion: v1
kind: Service
metadata:
  name: order-ms-ervice
spec:
  type: ClusterIP
  selector:
    app: order-ms
  ports:
  - port: 8091
    targetPort: 8091
```

#### payment-ms-deployment.yml
```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-ms-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: payment-ms
  template:
    metadata:
      labels:
        app: payment-ms
    spec:
      containers:
      - name: payment-ms
        image: intrieri/payment-ms:1.0.0
        ports:
        - containerPort: 8092
        env:
          - name: ZIPKIN_IP
            value: zipkin-ms-service
          - name: ZIPKING_PORT
            value: '9411'
```

#### payment-ms-services.yml

```yml
apiVersion: v1
kind: Service
metadata:
  name: payment-ms-ervice
spec:
  type: ClusterIP
  selector:
    app: payment-ms
  ports:
  - port: 8092
    targetPort: 8092
```

Abbiamo utilizzato ClusterIp come tipo specifico di servizio kubernetes. Questo tipo espone il servizio su un IP interno al cluster, 
in questo modo il servizio è raggiungibile solo all’interno del cluster. Questo è il tipo di servizio predefinito.

Infine andiamo a creare il file di deployment del nuovo Pod dove verrà eseguito Zipkin e il suo servizio relativo

#### zipkin-ms-deployment.yml

```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zipkin-ms-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: zipkin
  template:
    metadata:
      labels:
        app: zipkin
    spec:
      containers:
      - name: zipkin
        image: openzipkin/zipkin
        ports:
        - containerPort: 9411
        env:
            # note: L'archiviazione in memoria conserva tutti i dati in memoria, 
            #       eliminando i dati meno recenti in base a un limite di intervallo.
            #       Sarà necessario utilizzare una memoria(storage db anche per esempio) 
            #       adeguata negli ambienti di produzione        
          - name: STORAGE_TYPE
            value: mem
```

#### zipkin-ms-service.yml

```yml
apiVersion: v1
kind: Service
metadata:
  name: zipkin-ms-service
spec:
  type: NodePort
  selector:
    app: zipkin
  ports:
  - port: 9411
    targetPort: 9411
    nodePort: 32766
```

Zipkinv è configurato per utilizzare il Db con modalità memory, ma è possibile configurarlo ad es. in produzione
per utilizzare un Db storage vero e prorpio. Infine il servizio zipkin-ms-service è un service che è raggiungibile
anche da fuori dal cluster(NodePort).
Applicando le modifiche sia ai deployment che ai service definiti per il server Zipkin quest'ultimo sarà raggiungibile
tramite la sua dashboard al seguente url: http://localhost:32766/zipkin

## Ingress Controller

Tutti i service che abbiamo definito sono di tipologia ClusterIp(predefinito) ed espongono quindi il servizio su 
un IP che è all'interno del cluster e quindi ambedue i nostri microservizi non risultano raggiungibili al di 
fuori del Cluster. 
E' necessario quindi rendere accessibili all'esterno le API dei microservizi order.service e payment-service. 
Dobbiamo allora utilizzare una risorsa di K8s che si chiama Ingress il cui compito principale è proprio quello di 
esporre le routes HTTP e HTTPS dall'esterno del Cluster fino ai servizi all'interno del Cluster. Il routing del 
traffico è inoltre controllato da regole definite nella nostra risorsa ingress.
Il diagramma che rappresenta l'object Ingress di K8s che abbiamo implementato è qui rappresentato e conosciuto
come configurazione "Fanout" (https://kubernetes.io/docs/concepts/services-networking/ingress), cioè una 
configurazione che instrada il traffico da un singolo indirizzo IP a più servizi, in base all'URI http richiesto.


<img width="880" alt="Diagram Ingress Controller" src="https://github.com/lavespa/cloud-log-sleuth-zipkin/blob/main/image/Ingress.png">

Nella demo utilizzeremo [nginx Ingress](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html). Le istruzioni sul setup e 
la documentazione si possono trovare ai seguenti link:

* Setup: https://kubernetes.github.io/ingress-nginx/deploy/
* Doc: https://kubernetes.github.io/ingress-nginx/

Avendo utilizzato Windows come OS e avendo un cluster K8s generato con Docker Desktop è necessario lanciare il seguenete comando 
sul repository delle images disponibili la cui ultima versione è proprio quella specificata nel docker, cioè al momento la 
versione 0.34.0:

                 kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-0.32.0/deploy/static/provider/cloud/deploy.yaml
				 
A questo punto si verifica l'installazione, cioè se i pod del controller di ingresso sono stati avviati come indicato nel link di 
setup, con il comando:

                 kubectl get pods -n ingress-nginx  -l app.kubernetes.io/name=ingress-nginx --watch

Una volta che i pod del controller di ingresso sono in esecuzione, è possibile annullare la digitazione con il comando Ctrl+C.

Terminata l'installazione di Ingress è necessario, come anticipato, configurare la route da applicare come nel diagramma descritto,
inviando ad K8s la seguente configurazione:

#### ingress-service.yml

```yml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-service
  annotations:
    ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
    - http:
        paths:
          - path: /order
            pathType: Prefix
            backend:
              service:
                name: order-ms-ervice
                port: 
                  number: 8091
          - path: /payment
            pathType: Prefix
            backend:
              service:
                name: payment-ms-ervice
                port: 
                  number: 8092
```

Come possiamo osservare abbiamo effettuato la configurazione dell'Ingress per fare in modo che tutte le request http che iniziato con
prefix /order siano redirette al service del microservizio order-service e lo stesso dicasi per il microservizio payment-service.
In pratica quindi avremo che:

http://localhost/order/creaOrdine verrà risolta dall'Ingress Controller all'interno del Cluster con l'URL: http://order-ms-ervice:8091/creaOrdine.


       

   

