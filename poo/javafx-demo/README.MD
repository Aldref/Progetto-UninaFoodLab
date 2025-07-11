## comandi per runnare javafx

## compilare: 
    mvn clean compile

## runnare:
    mvn exec:java

## comando unico:
    mvn clean compile exec:java



# Documentazione Progetto UninaFoodLab

## 1. Introduzione
Il progetto UninaFoodLab è una applicazione desktop per la gestione di corsi e utenti, sviluppata come esercitazione universitaria. L'obiettivo è fornire una piattaforma intuitiva per la gestione delle attività didattiche e amministrative.

## 2. Scelte tecnologiche
- **Maven**: utilizzato per la gestione delle dipendenze e la build automatizzata del progetto.
- **JavaFX**: scelto per la realizzazione della GUI moderna e reattiva.

## 3. Architettura del progetto
La struttura segue il pattern MVC:
- **DAO**: classi per la gestione della persistenza dei dati.
- **DTO**: oggetti per il trasferimento dei dati tra i vari livelli.
- **Controller**: logica applicativa e gestione degli eventi.
- **Boundary**: interfaccia grafica e interazione con l’utente.

## 4. Gestione della GUI
La GUI è realizzata con JavaFX e FXML, separando la logica dall’interfaccia. I file CSS permettono la personalizzazione grafica.

## 5. Gestione della persistenza
I DAO si occupano della connessione e delle operazioni sul database, configurato tramite il file `db.properties`.

## 6. Testing
Sono presenti test automatici per le principali funzionalità, utilizzando JUnit.

## 7. Conclusioni
Il progetto dimostra l’utilizzo di tecnologie moderne e buone pratiche di progettazione software. Possibili sviluppi futuri includono l’integrazione di nuove funzionalità e il miglioramento della user experience.

