## Indice della documentazione
Introduzione

Analisi dei Requisiti

Modello Concettuale (UML)

Progettazione Logica (Schema E-R / Schema Relazionale)

Progettazione Fisica (SQL + Vincoli + Trigger)

Scelte Progettuali

Descrizione delle Entità e Relazioni

Gestione delle Sessioni e Adesioni

Gestione delle Ricette e Ingredienti

Considerazioni su Integrità e Performance

Conclusioni

Appendici (Script SQL, Diagrammi, Test, ecc.)

1. Introduzione
Presentazione del progetto UninaFoodLab.

Obiettivi del sistema.

Contesto di utilizzo.

Panoramica delle funzionalità chiave (registrazione corsi, gestione sessioni, adesioni, ricette, ecc.).

2. Analisi dei Requisiti
Requisiti funzionali (es. iscrizione corsi, adesione sessioni pratiche).

Requisiti non funzionali (es. evitare sprechi alimentari, gestione delle quantità).

Attori coinvolti: Chef, Utente, Sistema.

3. Modello Concettuale (UML)
Diagramma delle classi UML.

Descrizione delle classi principali (Corso, Sessione, Ricetta, Utente, Adesione, Ingrediente, ecc.).

Relazioni tra le classi: associazioni, generalizzazioni, cardinalità.

Eventuali diagrammi di sequenza o casi d’uso, se li hai fatti.

4. Progettazione Logica
Schema E-R se presente, altrimenti direttamente lo schema relazionale.

Traduzione del modello UML in tabelle relazionali.

Tipi di relazione (1:N, N:M) e loro implementazione con tabelle ponte.

5. Progettazione Fisica
Script SQL per la creazione del DB.

Definizione dei vincoli (chiavi primarie, esterne, unique, not null).

Implementazione di trigger (es. per aggiornare quantità di ingredienti in base alle adesioni).

Eventuali viste, stored procedure, indici, se presenti.

6. Scelte Progettuali
Motivazioni dietro la scelta delle entità e delle relazioni.

Perché hai gestito le sessioni pratiche in un certo modo?

Come hai gestito il concetto di adesione e conferma utente?

7. Descrizione delle Entità e Relazioni
Per ogni tabella:

Nome tabella.

Descrizione dello scopo.

Attributi (con tipo, vincoli, chiavi).

Relazioni con altre entità.

8. Gestione delle Sessioni e Adesioni
Logica delle sessioni online vs. in presenza.

Come è strutturata l’adesione esplicita.

Trigger e vincoli collegati.

9. Gestione delle Ricette e Ingredienti
Relazione tra sessione pratica, ricette e ingredienti.

Come pianifichi le quantità.

Evitare sprechi tramite calcolo dinamico basato sulle adesioni.

10. Considerazioni su Integrità e Performance
Come il sistema assicura l’integrità dei dati.

Scelte per ottimizzare le query o evitare inconsistenze.

11. Conclusioni
Riflessioni finali.

Possibili estensioni future (es. pagamento corsi, valutazioni, feedback).

Limiti del progetto attuale.

12. Appendici
Script SQL completi.

Diagrammi UML in alta qualità.

Output di test (se presente).

Eventuali query d’uso o simulazioni.