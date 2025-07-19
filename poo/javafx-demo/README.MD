## Traccia

Si sviluppi un applicativo Java con interfaccia grafica (Swing o JavaFX) per la gestione dei corsi tematici offerti dalla piattaforma UninaFoodLab. Il sistema dovrà essere collegato a un database relazionale prepopolato contenente informazioni su chef, ricette e ingredienti. Il sistema deve permettere l’autenticazione degli chef tramite credenziali (username e password). Una volta autenticato, lo chef può aggiungere un nuovo corso, specificando le seguenti informazioni: categoria, data di inizio, frequenza delle sessioni, numero di sessioni. Per ciascuna sessione, deve essere indicata la modalità di svolgimento, ovvero se si tratta di una sessione online o in presenza. Lo chef avrà inoltre la possibilità di visualizzare i corsi esistenti, applicando filtri per categoria. Dopo aver selezionato un corso, lo chef può associare a ciascuna sessione pratica una o più ricette da realizzare. Infine, il sistema deve fornire un report mensile, che permette allo chef di visualizzare: il numero di corsi totali tenuti, il numero di sessioni online e pratiche, e di quest’ultime il numero medio, massimo e minimo di ricette realizzate.

---

## Tecnologie e Struttura del Progetto

- **Java & JavaFX**: Applicativo sviluppato in Java con interfaccia grafica JavaFX per una user experience moderna e intuitiva.
- **Maven**: Gestione delle dipendenze e della compilazione tramite Maven.
- **Database relazionale**: Collegamento a un database prepopolato.
- **Pattern MVC**: Separazione tra logica di business, accesso ai dati (DAO/DTO) e interfaccia utente.
- **Stili CSS**: Personalizzazione dell’interfaccia tramite file CSS dedicati.
- **Documentazione**: Documentazione tecnica e utente disponibile nella cartella `docs/`.

## Struttura delle Cartelle

- `src/main/java`: Codice sorgente Java (controller, boundary, DAO, DTO, utilità)
- `src/main/resources`: Risorse (file FXML, CSS, immagini)
- `docs/`: Documentazione tecnica e utente

## Requisiti

- Java
- Maven

## Comandi per compilare ed eseguire

- **Compilazione:**
  ```bash
  mvn clean compile
  ```
- **Esecuzione:**
  ```bash
  mvn exec:java
  ```
- **Compilazione ed esecuzione:**
  ```bash
  mvn clean compile exec:java
  ```

## Note Utili

- Tutte le operazioni di gestione corsi, chef, ricette e ingredienti sono gestite tramite interfaccia grafica.
- Il progetto è pensato per essere facilmente estendibile e manutenibile.
- Per dettagli su installazione, uso e struttura del database consultare la documentazione nella cartella `docs/`.
- Creare un file in questa directory "db.proprieties" Con URL, RUOLO e PASSWORD per accedere al database.




