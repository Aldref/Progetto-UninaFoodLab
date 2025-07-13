# Progetto UninaFoodLab

UninaFoodLab è una piattaforma per la gestione di corsi di cucina tematici, pensata per chef e appassionati. Il progetto comprende sia la parte di database relazionale che un applicativo Java con interfaccia grafica moderna.

---

## Descrizione Generale

La piattaforma permette agli chef di creare, gestire e visualizzare corsi di cucina su vari argomenti (es. cucina asiatica, pasticceria, panificazione). Gli utenti possono iscriversi ai corsi, partecipare a sessioni online o pratiche, e consultare ricette e ingredienti. Il sistema ottimizza la gestione delle adesioni e degli ingredienti, riducendo sprechi alimentari.

---

## Componenti del Progetto

### 1. Database Relazionale
- **Tecnologia:** MySQL/PostgreSQL/Derby (configurabile)
- **Struttura:** Tabelle per chef, utenti, corsi, sessioni, ricette, ingredienti, adesioni
- **Funzionalità:**
  - Gestione chef e utenti
  - Corsi tematici con sessioni online/pratiche
  - Associazione ricette e ingredienti alle sessioni
  - Adesioni utenti e pianificazione ingredienti
- **Documentazione:**
  - Schema E-R, diagrammi UML, script SQL e istruzioni nella cartella `db/docs/`
  - Script di compilazione LaTeX per la documentazione tecnica

### 2. Applicativo Java
- **Tecnologia:** Java 11+, JavaFX per l’interfaccia grafica
- **Struttura:**
  - Pattern MVC: separazione tra interfaccia (Boundary), logica (Controller), accesso dati (DAO/DTO)
  - Gestione sicura delle credenziali e autenticazione chef
  - Interfaccia utente intuitiva, con stili personalizzati tramite CSS
  - Reportistica mensile per chef (corsi, sessioni, ricette)
- **Gestione Dipendenze:** Maven
- **Comandi principali:**
  - Compilazione: `mvn clean compile`
  - Esecuzione: `mvn exec:java`
  - Compilazione ed esecuzione: `mvn clean compile exec:java`
- **Risorse:**
  - File FXML, CSS, immagini in `src/main/resources`
  - Documentazione utente e tecnica in `docs/`

---

## Struttura delle Cartelle

- `src/main/java`: Codice sorgente Java (controller, boundary, DAO, DTO, utilità)
- `src/main/resources`: Risorse (FXML, CSS, immagini)
- `db/`: Script SQL, documentazione, diagrammi
- `docs/`: Documentazione tecnica e utente

---

## Requisiti

- Java 11 o superiore
- Maven
- Database relazionale già fornito e prepopolato (vedi cartella `db/`)

### Strumenti Utilizzati

- GitHub — Versionamento e collaborazione
- Visual Studio Code — Ambiente di sviluppo
- PostgreSQL — Database relazionale
- Maven — Gestione dipendenze e build
- LaTeX — Documentazione tecnica

---

## Membri del Gruppo

- Codice gruppo: **OOBD39**
- D'Angelo Mario — [GitHub](https://github.com/Aldref27)
- Francesco Calone - [GitHub](https://github.com/FrancescoCalone)


---

## Comandi Utili

### Git
- `git pull` — Aggiorna la repository locale
- `git add .` — Aggiunge tutte le modifiche
- `git commit -m "messaggio"` — Salva le modifiche
- `git push` — Invia le modifiche al repository remoto

### Bash/LaTeX
- Per attivare lo script: `chmod +x compila.sh`
- Per compilare la documentazione:
  - `./compila.sh` (nella directory docs)
  - `pdflatex -output-directory=build Documentazione_BaseDiDati.tex`
  - `pdflatex -output-directory=build Documentazione_Programmazione-Object-Oriented.tex`

---

## Note Finali

- Il progetto è pensato per essere facilmente estendibile e manutenibile.
- Tutte le funzionalità sono documentate e testate.
- Per dettagli su installazione, uso e struttura del database consultare la documentazione nella cartella `docs/`.