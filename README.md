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

       

   

