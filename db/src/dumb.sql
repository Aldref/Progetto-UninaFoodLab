--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Ubuntu 17.5-1.pgdg22.04+1)
-- Dumped by pg_dump version 17.4

-- Started on 2025-07-01 13:08:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 887 (class 1247 OID 16707)
-- Name: circuito; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.circuito AS ENUM (
    'Visa',
    'Mastercard'
);


ALTER TYPE public.circuito OWNER TO name;

--
-- TOC entry 893 (class 1247 OID 16720)
-- Name: fds; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.fds AS ENUM (
    'Giornaliera',
    'Ogni due giorni',
    'Ogni tre giorni',
    'Ogni quattro giorni',
    'Ogni cinque giorni',
    'Ogni sei giorni',
    'Settimanale',
    'Mensile'
);


ALTER TYPE public.fds OWNER TO name;

--
-- TOC entry 896 (class 1247 OID 16738)
-- Name: giorno; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.giorno AS ENUM (
    'Lunedì',
    'Martedì',
    'Mercoledì',
    'Giovedì',
    'Venerdì',
    'Sabato',
    'Domenica'
);


ALTER TYPE public.giorno OWNER TO name;

--
-- TOC entry 890 (class 1247 OID 16712)
-- Name: statopagamento; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.statopagamento AS ENUM (
    'In attesa',
    'Pagato',
    'Fallito'
);


ALTER TYPE public.statopagamento OWNER TO name;

--
-- TOC entry 899 (class 1247 OID 16754)
-- Name: unitadimisura; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.unitadimisura AS ENUM (
    'Grammi',
    'Kilogrammi',
    'Litro',
    'Centilitro'
);


ALTER TYPE public.unitadimisura OWNER TO name;

--
-- TOC entry 263 (class 1255 OID 17534)
-- Name: check_orario_e_durata(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.check_orario_e_durata() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    orario_ora INTEGER;
    orario_minuti INTEGER;
    durata_frazionaria NUMERIC;
    fine_lezione NUMERIC;
BEGIN
    IF NEW.Durata <= 1 OR NEW.Durata >= 8 THEN
        RAISE EXCEPTION 'Durata deve essere maggiore di 1 e minore di 8';
    END IF;

    orario_ora := FLOOR(NEW.Orario);
    orario_minuti := ROUND((NEW.Orario - orario_ora) * 100);

    IF orario_ora >= 23 THEN
        RAISE EXCEPTION 'La parte intera dell''orario deve essere minore di 23';
    ELSIF orario_minuti >= 60 THEN
        RAISE EXCEPTION 'La parte decimale dell''orario deve essere minore di 60';
    END IF;

    durata_frazionaria := NEW.Durata;
    fine_lezione := NEW.Orario + durata_frazionaria;

    IF fine_lezione > 23 THEN
        RAISE EXCEPTION 'L''orario di fine lezione non può superare le 23';
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.check_orario_e_durata() OWNER TO name;

--
-- TOC entry 244 (class 1255 OID 17081)
-- Name: check_partecipazione_corso(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.check_partecipazione_corso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        DECLARE
            corso_id INT;
            partecipante_id INT;
            pagamento_count INT;
        BEGIN
            
            SELECT IDcorso INTO corso_id
            FROM SESSIONE_PRESENZA
            WHERE IdSessionePresenza = NEW.IdSessionePresenza;

            partecipante_id := NEW.IDpartecipante;

            
            SELECT COUNT(*) INTO pagamento_count
            FROM RICHIESTAPAGAMENTO
            WHERE IdCorso = corso_id
            AND IdPartecipante = partecipante_id;

           
            IF pagamento_count = 0 THEN
                RAISE EXCEPTION 'Il partecipante % non è iscritto al corso %.', partecipante_id, corso_id;
            END IF;

            RETURN NEW;
        END;
        $$;


ALTER FUNCTION public.check_partecipazione_corso() OWNER TO name;

--
-- TOC entry 267 (class 1255 OID 17048)
-- Name: elimina_ingredienti_della_ricetta(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.elimina_ingredienti_della_ricetta() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
      DELETE FROM PREPARAZIONEINGREDIENTE
      WHERE IdRicetta = OLD.IdRicetta;
      RETURN OLD;
    END;
    $$;


ALTER FUNCTION public.elimina_ingredienti_della_ricetta() OWNER TO name;

--
-- TOC entry 266 (class 1255 OID 17046)
-- Name: impedisci_carta_duplicata_per_partecipante(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.impedisci_carta_duplicata_per_partecipante() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        existing_card_id INT;
    BEGIN
        IF TG_TABLE_NAME = 'carta' THEN
            IF EXISTS (
                SELECT 1
                FROM Carta
                WHERE UltimeQuattroCifre = NEW.UltimeQuattroCifre
                  AND DataScadenza = NEW.DataScadenza
                  AND Circuito = NEW.Circuito::VARCHAR(50)
                  AND Intestatario = NEW.Intestatario
                  AND IdCarta != NEW.IdCarta
            ) THEN
                RAISE EXCEPTION 'Errore: Una carta con queste ultime 4 cifre, data di scadenza, circuito e intestatario esiste già nel sistema.';
            END IF;
        END IF;
        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.impedisci_carta_duplicata_per_partecipante() OWNER TO name;

--
-- TOC entry 258 (class 1255 OID 17029)
-- Name: impedisci_eliminazione_corso_se_iscritto(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.impedisci_eliminazione_corso_se_iscritto() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        enrolled_count INTEGER;
        course_name VARCHAR(255);
    BEGIN
        SELECT Nome INTO course_name
        FROM Corso
        WHERE IdCorso = OLD.IdCorso;

        SELECT COUNT(*)
        INTO enrolled_count
        FROM RichiestaPagamento
        WHERE IdCorso = OLD.IdCorso
          AND StatoPagamento = 'Pagato';

        IF enrolled_count > 0 THEN
            RAISE EXCEPTION 'Errore: Impossibile eliminare il corso "%" (ID %). Ci sono ancora % partecipanti paganti iscritti.',
                                course_name, OLD.IdCorso, enrolled_count;
        END IF;

        RETURN OLD;
    END;
    $$;


ALTER FUNCTION public.impedisci_eliminazione_corso_se_iscritto() OWNER TO name;

--
-- TOC entry 245 (class 1255 OID 17072)
-- Name: validate_email_full(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.validate_email_full() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    at_pos INT;
    domain_part TEXT;
BEGIN
    
    at_pos := POSITION('@' IN NEW.Email);

    
    IF at_pos = 0 THEN
        RAISE EXCEPTION 'Email non valida: manca la chiocciola (@).';
    END IF;

    
    domain_part := SUBSTRING(NEW.Email FROM at_pos + 1);

   
    IF POSITION('.' IN domain_part) = 0 THEN
        RAISE EXCEPTION 'Email non valida: manca il punto dopo la chiocciola.';
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.validate_email_full() OWNER TO name;

--
-- TOC entry 264 (class 1255 OID 17040)
-- Name: verifica_anni_esperienza_chef(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_anni_esperienza_chef() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        chef_current_age_years INTEGER;
        min_start_age CONSTANT INTEGER := 18;
    BEGIN
        IF NEW.DataDiNascita IS NULL THEN
            RAISE EXCEPTION 'Errore: La DataDiNascita dello Chef non può essere NULL.';
        END IF;

        chef_current_age_years := EXTRACT(YEAR FROM AGE(CURRENT_DATE, NEW.DataDiNascita));

        IF NEW.AnniDiEsperienza < 0 THEN
            RAISE EXCEPTION 'Errore: Gli AnniDiEsperienza non possono essere negativi.';
        END IF;

        IF NEW.AnniDiEsperienza > chef_current_age_years THEN
            RAISE EXCEPTION 'Errore: Gli AnniDiEsperienza (%) non possono superare l''età attuale dello Chef (%).', NEW.AnniDiEsperienza, chef_current_age_years;
        END IF;

        IF (chef_current_age_years - NEW.AnniDiEsperienza) < min_start_age THEN
            RAISE EXCEPTION 'Errore: L''età stimata di inizio esperienza (età: % anni, esperienza: % anni = % anni) è inferiore all''età minima consentita per iniziare (%).', chef_current_age_years, NEW.AnniDiEsperienza, (chef_current_age_years - NEW.AnniDiEsperienza), min_start_age;
        END IF;

        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.verifica_anni_esperienza_chef() OWNER TO name;

--
-- TOC entry 262 (class 1255 OID 17038)
-- Name: verifica_data_pagamento_prima_inizio_corso(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_data_pagamento_prima_inizio_corso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        course_start_date DATE;
        course_name VARCHAR(255);
    BEGIN
        SELECT DataInizio, Nome INTO course_start_date, course_name
        FROM Corso
        WHERE IdCorso = NEW.IdCorso;

        IF NEW.DataRichiesta::DATE >= course_start_date THEN
            RAISE EXCEPTION 'Errore: Impossibile procedere con il pagamento per il corso "%" (ID %). La data della richiesta di pagamento (%) non può essere successiva o uguale alla data di inizio del corso (%).',
                                course_name, NEW.IdCorso, NEW.DataRichiesta::DATE, course_start_date;
        END IF;

        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.verifica_data_pagamento_prima_inizio_corso() OWNER TO name;

--
-- TOC entry 261 (class 1255 OID 17036)
-- Name: verifica_importo_pagamento_corrisponde_prezzo_corso(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_importo_pagamento_corrisponde_prezzo_corso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        course_price DECIMAL(10,2);
    BEGIN
        SELECT Prezzo INTO course_price
        FROM Corso
        WHERE IdCorso = NEW.IdCorso;

        IF NEW.ImportoPagato != course_price THEN
            RAISE EXCEPTION 'Errore: L''ImportoPagato (%.2f) deve corrispondere esattamente al Prezzo del corso (%.2f).', NEW.ImportoPagato, course_price;
        END IF;

        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.verifica_importo_pagamento_corrisponde_prezzo_corso() OWNER TO name;

--
-- TOC entry 269 (class 1255 OID 17042)
-- Name: verifica_intervallo_date_corso(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_intervallo_date_corso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
    IF NEW.DataInizio > NEW.DataFine THEN
        RAISE EXCEPTION 'Errore: La DataInizio del corso (%) non può essere successiva alla DataFine (%).',
            NEW.DataInizio, NEW.DataFine;
    END IF;

    RETURN NEW;
END;
    $$;


ALTER FUNCTION public.verifica_intervallo_date_corso() OWNER TO name;

--
-- TOC entry 260 (class 1255 OID 17033)
-- Name: verifica_intervallo_eta(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_intervallo_eta() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        person_age_years INTEGER;
        min_age CONSTANT INTEGER := 18;
        max_age CONSTANT INTEGER := 100;
    BEGIN
        IF NEW.DataDiNascita IS NULL THEN
            RAISE EXCEPTION 'Errore: La DataDiNascita non può essere NULL.';
        END IF;

        person_age_years := EXTRACT(YEAR FROM AGE(CURRENT_DATE, NEW.DataDiNascita));

        IF person_age_years < min_age THEN
            RAISE EXCEPTION 'Errore: L''età della persona (%, calcolata dalla DataDiNascita %) è inferiore all''età minima consentita (%).', person_age_years, NEW.DataDiNascita, min_age;
        END IF;

        IF person_age_years > max_age THEN
            RAISE EXCEPTION 'Errore: L''età della persona (%, calcolata dalla DataDiNascita %) è superiore all''età massima consentita (%).', person_age_years, NEW.DataDiNascita, max_age;
        END IF;

        RETURN NEW;
    END;
$$;


ALTER FUNCTION public.verifica_intervallo_eta() OWNER TO name;

--
-- TOC entry 265 (class 1255 OID 17044)
-- Name: verifica_max_tipi_cucina_per_corso(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_max_tipi_cucina_per_corso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        current_tipi_count INTEGER;
    BEGIN
        SELECT COUNT(*)
        INTO current_tipi_count
        FROM TipoDiCucina_Corso
        WHERE IDCorso = NEW.IDCorso;

        IF current_tipi_count >= 2 THEN
            RAISE EXCEPTION 'Errore: Il corso con ID % ha già raggiunto il limite massimo di 2 tipi di cucina associati.', NEW.IDCorso;
        END IF;

        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.verifica_max_tipi_cucina_per_corso() OWNER TO name;

--
-- TOC entry 257 (class 1255 OID 17026)
-- Name: verifica_sessioni_contemporanee_chef(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_sessioni_contemporanee_chef() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        conflicting_session_count INTEGER;
        chef_name VARCHAR(255);
        session_type_being_inserted TEXT;
        other_session_type TEXT;
    BEGIN
        SELECT Nome || ' ' || Cognome INTO chef_name
        FROM Chef
        WHERE IdChef = NEW.IDChef;

        IF TG_TABLE_NAME = 'sessione_presenza' THEN
            session_type_being_inserted := 'presenza';
            other_session_type := 'telematica';
        ELSIF TG_TABLE_NAME = 'sessione_telematica' THEN
            session_type_being_inserted := 'telematica';
            other_session_type := 'presenza';
        ELSE
            RAISE EXCEPTION 'Errore interno del trigger: tabella sconosciuta (%).', TG_TABLE_NAME;
        END IF;

        IF session_type_being_inserted = 'presenza' THEN
            SELECT COUNT(*)
            INTO conflicting_session_count
            FROM Sessione_Telematica
            WHERE IDChef = NEW.IDChef
              AND Data = NEW.Data;
        ELSIF session_type_being_inserted = 'telematica' THEN
            SELECT COUNT(*)
            INTO conflicting_session_count
            FROM Sessione_Presenza
            WHERE IDChef = NEW.IDChef
              AND Data = NEW.Data;
        END IF;

        IF conflicting_session_count > 0 THEN
            RAISE EXCEPTION 'Errore: Lo Chef "%" (ID %) è già assegnato a una sessione di tipo "%" in data %. Impossibile assegnarlo anche a una sessione di tipo "%" nello stesso giorno.',
                                chef_name, NEW.IDChef, other_session_type, NEW.Data, session_type_being_inserted;
        END IF;

        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.verifica_sessioni_contemporanee_chef() OWNER TO name;

--
-- TOC entry 259 (class 1255 OID 17031)
-- Name: verifica_superamento_max_partecipanti_corso(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_superamento_max_partecipanti_corso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        course_max_people INTEGER;
        current_paid_count INTEGER;
        potential_paid_count INTEGER;
        course_name VARCHAR(255);
    BEGIN
        SELECT MaxPersone, Nome INTO course_max_people, course_name
        FROM Corso
        WHERE IdCorso = NEW.IdCorso;

        IF course_max_people <= 0 OR course_max_people IS NULL THEN
            RETURN NEW;
        END IF;

        SELECT COUNT(*)
        INTO current_paid_count
        FROM RichiestaPagamento
        WHERE IdCorso = NEW.IdCorso
          AND StatoPagamento = 'Pagato';

        potential_paid_count := current_paid_count;

        IF TG_OP = 'INSERT' THEN
            IF NEW.StatoPagamento = 'Pagato' THEN
                potential_paid_count := potential_paid_count + 1;
            END IF;
        ELSIF TG_OP = 'UPDATE' THEN
            IF OLD.StatoPagamento != 'Pagato' AND NEW.StatoPagamento = 'Pagato' THEN
                potential_paid_count := potential_paid_count + 1;
            ELSIF OLD.StatoPagamento = 'Pagato' AND NEW.StatoPagamento != 'Pagato' THEN
                potential_paid_count := potential_paid_count - 1;
            END IF;
        END IF;

        IF potential_paid_count > course_max_people THEN
            RAISE EXCEPTION 'Errore: Il corso "%" (ID %) ha raggiunto il limite massimo di % partecipanti paganti. Impossibile accettare questa richiesta di pagamento (attuali: %s, limite: %s).',
                                course_name, NEW.IdCorso, course_max_people, current_paid_count, course_max_people;
        END IF;

        RETURN NEW;
    END;
    $$;


ALTER FUNCTION public.verifica_superamento_max_partecipanti_corso() OWNER TO name;

--
-- TOC entry 268 (class 1255 OID 17050)
-- Name: verifica_unicita_email(); Type: FUNCTION; Schema: public; Owner: name
--

CREATE FUNCTION public.verifica_unicita_email() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        IF TG_TABLE_NAME = 'chef' THEN
            IF EXISTS (SELECT 1 FROM Partecipante WHERE Email = NEW.Email) THEN
                RAISE EXCEPTION 'Errore: L''email "%" è già utilizzata da un Partecipante.', NEW.Email;
            END IF;
        ELSIF TG_TABLE_NAME = 'partecipante' THEN
            IF EXISTS (SELECT 1 FROM Chef WHERE Email = NEW.Email) THEN
                RAISE EXCEPTION 'Errore: L''email "%" è già utilizzata da uno Chef.', NEW.Email;
            END IF;
        END IF;
        RETURN NEW;
    END;
 $$;


ALTER FUNCTION public.verifica_unicita_email() OWNER TO name;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 235 (class 1259 OID 16920)
-- Name: adesione_sessionepresenza; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.adesione_sessionepresenza (
    conferma boolean,
    idsessionepresenza integer NOT NULL,
    idpartecipante integer NOT NULL
);


ALTER TABLE public.adesione_sessionepresenza OWNER TO name;

--
-- TOC entry 218 (class 1259 OID 16764)
-- Name: carta; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.carta (
    idcarta integer NOT NULL,
    intestatario character varying(100) NOT NULL,
    datascadenza date NOT NULL,
    ultimequattrocifre character(4) NOT NULL,
    circuito character varying(50) NOT NULL
);


ALTER TABLE public.carta OWNER TO name;

--
-- TOC entry 217 (class 1259 OID 16763)
-- Name: carta_idcarta_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.carta ALTER COLUMN idcarta ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.carta_idcarta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 223 (class 1259 OID 16795)
-- Name: chef; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.chef (
    idchef integer NOT NULL,
    nome character varying(50) NOT NULL,
    cognome character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(100) NOT NULL,
    datadinascita date NOT NULL,
    descrizione character varying(60),
    propic text,
    annidiesperienza integer,
    CONSTRAINT chef_annidiesperienza_check CHECK ((annidiesperienza >= 0))
);


ALTER TABLE public.chef OWNER TO name;

--
-- TOC entry 222 (class 1259 OID 16794)
-- Name: chef_idchef_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.chef ALTER COLUMN idchef ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.chef_idchef_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 225 (class 1259 OID 16806)
-- Name: corso; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.corso (
    idcorso integer NOT NULL,
    nome character varying(100) NOT NULL,
    descrizione character varying(60) NOT NULL,
    datainizio date NOT NULL,
    datafine date NOT NULL,
    frequenzadellesessioni character varying(100) NOT NULL,
    propic text,
    maxpersone integer,
    prezzo numeric(10,2),
    idchef integer,
    CONSTRAINT corso_maxpersone_check CHECK ((maxpersone > 0)),
    CONSTRAINT corso_prezzo_check CHECK ((prezzo >= (0)::numeric))
);


ALTER TABLE public.corso OWNER TO name;

--
-- TOC entry 224 (class 1259 OID 16805)
-- Name: corso_idcorso_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.corso ALTER COLUMN idcorso ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.corso_idcorso_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 240 (class 1259 OID 16957)
-- Name: ingrediente; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.ingrediente (
    idingrediente integer NOT NULL,
    nome character varying(100) NOT NULL,
    unitadimisura character varying(50) NOT NULL
);


ALTER TABLE public.ingrediente OWNER TO name;

--
-- TOC entry 239 (class 1259 OID 16956)
-- Name: ingrediente_idingrediente_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.ingrediente ALTER COLUMN idingrediente ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.ingrediente_idingrediente_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 220 (class 1259 OID 16770)
-- Name: partecipante; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.partecipante (
    idpartecipante integer NOT NULL,
    nome character varying(50) NOT NULL,
    cognome character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(100) NOT NULL,
    datadinascita date NOT NULL,
    propic text
);


ALTER TABLE public.partecipante OWNER TO name;

--
-- TOC entry 219 (class 1259 OID 16769)
-- Name: partecipante_idpartecipante_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.partecipante ALTER COLUMN idpartecipante ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.partecipante_idpartecipante_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 234 (class 1259 OID 16905)
-- Name: partecipante_sessionetelematica; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.partecipante_sessionetelematica (
    idpartecipante integer NOT NULL,
    idsessionetelematica integer NOT NULL
);


ALTER TABLE public.partecipante_sessionetelematica OWNER TO name;

--
-- TOC entry 221 (class 1259 OID 16779)
-- Name: possiede; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.possiede (
    idpartecipante integer NOT NULL,
    idcarta integer NOT NULL
);


ALTER TABLE public.possiede OWNER TO name;

--
-- TOC entry 241 (class 1259 OID 16962)
-- Name: preparazioneingrediente; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.preparazioneingrediente (
    idricetta integer NOT NULL,
    idingrediente integer NOT NULL,
    quanititaunitaria numeric(10,2) NOT NULL,
    CONSTRAINT preparazioneingrediente_quanititaunitaria_check CHECK ((quanititaunitaria >= (0)::numeric))
);


ALTER TABLE public.preparazioneingrediente OWNER TO name;

--
-- TOC entry 238 (class 1259 OID 16941)
-- Name: sessione_presenza_ricetta; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.sessione_presenza_ricetta (
    idricetta integer NOT NULL,
    idsessionepresenza integer NOT NULL
);


ALTER TABLE public.sessione_presenza_ricetta OWNER TO name;

--
-- TOC entry 242 (class 1259 OID 17053)
-- Name: quantitapersessione; Type: VIEW; Schema: public; Owner: name
--

CREATE VIEW public.quantitapersessione AS
 SELECT pi.idricetta,
    pi.idingrediente,
    pi.quanititaunitaria,
    count(asp.idpartecipante) AS numeropartecipanti,
    (pi.quanititaunitaria * (count(asp.idpartecipante))::numeric) AS quantitatotale,
    spr.idsessionepresenza
   FROM ((public.preparazioneingrediente pi
     JOIN public.sessione_presenza_ricetta spr ON ((pi.idricetta = spr.idricetta)))
     JOIN public.adesione_sessionepresenza asp ON ((asp.idsessionepresenza = spr.idsessionepresenza)))
  WHERE (asp.conferma = true)
  GROUP BY pi.idricetta, pi.idingrediente, pi.quanititaunitaria, spr.idsessionepresenza;


ALTER VIEW public.quantitapersessione OWNER TO name;

--
-- TOC entry 237 (class 1259 OID 16936)
-- Name: ricetta; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.ricetta (
    idricetta integer NOT NULL,
    nome character varying(100) NOT NULL
);


ALTER TABLE public.ricetta OWNER TO name;

--
-- TOC entry 236 (class 1259 OID 16935)
-- Name: ricetta_idricetta_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.ricetta ALTER COLUMN idricetta ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.ricetta_idricetta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 226 (class 1259 OID 16830)
-- Name: richiestapagamento; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.richiestapagamento (
    datarichiesta timestamp without time zone NOT NULL,
    statopagamento character varying(50) NOT NULL,
    importopagato numeric(10,2),
    idcorso integer NOT NULL,
    idpartecipante integer NOT NULL,
    CONSTRAINT richiestapagamento_importopagato_check CHECK ((importopagato >= (0)::numeric))
);


ALTER TABLE public.richiestapagamento OWNER TO name;

--
-- TOC entry 231 (class 1259 OID 16870)
-- Name: sessione_presenza; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.sessione_presenza (
    idsessionepresenza integer NOT NULL,
    giorno character varying(20) NOT NULL,
    data date NOT NULL,
    orario time without time zone NOT NULL,
    durata interval NOT NULL,
    citta character varying(50) NOT NULL,
    via character varying(100) NOT NULL,
    cap character(5) NOT NULL,
    descrizione character varying(60) NOT NULL,
    idcorso integer,
    idchef integer,
    CONSTRAINT chk_durata_max_8h CHECK (((EXTRACT(epoch FROM durata) / (60)::numeric) <= (480)::numeric))
);


ALTER TABLE public.sessione_presenza OWNER TO name;

--
-- TOC entry 230 (class 1259 OID 16869)
-- Name: sessione_presenza_idsessionepresenza_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.sessione_presenza ALTER COLUMN idsessionepresenza ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.sessione_presenza_idsessionepresenza_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 233 (class 1259 OID 16888)
-- Name: sessione_telematica; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.sessione_telematica (
    idsessionetelematica integer NOT NULL,
    applicazione character varying(100) NOT NULL,
    codicechiamata character varying(100) NOT NULL,
    data date NOT NULL,
    orario time without time zone NOT NULL,
    durata interval NOT NULL,
    giorno character varying(20) NOT NULL,
    descrizione text NOT NULL,
    idcorso integer,
    idchef integer,
    CONSTRAINT chk_durata_max_8h CHECK (((EXTRACT(epoch FROM durata) / (60)::numeric) <= (480)::numeric))
);


ALTER TABLE public.sessione_telematica OWNER TO name;

--
-- TOC entry 232 (class 1259 OID 16887)
-- Name: sessione_telematica_idsessionetelematica_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.sessione_telematica ALTER COLUMN idsessionetelematica ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.sessione_telematica_idsessionetelematica_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 228 (class 1259 OID 16847)
-- Name: tipodicucina; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.tipodicucina (
    idtipocucina integer NOT NULL,
    nome character varying(50) NOT NULL
);


ALTER TABLE public.tipodicucina OWNER TO name;

--
-- TOC entry 229 (class 1259 OID 16854)
-- Name: tipodicucina_corso; Type: TABLE; Schema: public; Owner: name
--

CREATE TABLE public.tipodicucina_corso (
    idtipocucina integer NOT NULL,
    idcorso integer NOT NULL
);


ALTER TABLE public.tipodicucina_corso OWNER TO name;

--
-- TOC entry 227 (class 1259 OID 16846)
-- Name: tipodicucina_idtipocucina_seq; Type: SEQUENCE; Schema: public; Owner: name
--

ALTER TABLE public.tipodicucina ALTER COLUMN idtipocucina ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tipodicucina_idtipocucina_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 243 (class 1259 OID 17108)
-- Name: vista_statistiche_mensili_chef; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.vista_statistiche_mensili_chef AS
 SELECT ch.idchef,
    ch.nome,
    ch.cognome,
    count(spr.idricetta) AS totale_ricette_mese,
    COALESCE(max(spr_count.ricette_per_sessione), (0)::bigint) AS max_ricette_in_sessione,
    COALESCE(min(spr_count.ricette_per_sessione), (0)::bigint) AS min_ricette_in_sessione,
    COALESCE(avg(spr_count.ricette_per_sessione), (0)::numeric) AS media_ricette_in_sessione,
    count(DISTINCT c.idcorso) AS numero_corsi,
    count(DISTINCT sp.idsessionepresenza) AS numero_sessioni_presenza,
    count(DISTINCT st.idsessionetelematica) AS numero_sessioni_telematiche,
    COALESCE(sum(rp.importopagato), (0)::numeric) AS guadagno_totale,
    EXTRACT(month FROM COALESCE((sp.data)::timestamp without time zone, (st.data)::timestamp without time zone, rp.datarichiesta, (c.datainizio)::timestamp without time zone, (c.datafine)::timestamp without time zone)) AS mese,
    EXTRACT(year FROM COALESCE((sp.data)::timestamp without time zone, (st.data)::timestamp without time zone, rp.datarichiesta, (c.datainizio)::timestamp without time zone, (c.datafine)::timestamp without time zone)) AS anno
   FROM ((((((public.chef ch
     LEFT JOIN public.corso c ON ((ch.idchef = c.idchef)))
     LEFT JOIN public.sessione_presenza sp ON (((c.idcorso = sp.idcorso) AND (sp.idchef = ch.idchef))))
     LEFT JOIN public.sessione_presenza_ricetta spr ON ((spr.idsessionepresenza = sp.idsessionepresenza)))
     LEFT JOIN ( SELECT spr2.idsessionepresenza,
            count(*) AS ricette_per_sessione
           FROM (public.sessione_presenza_ricetta spr2
             JOIN public.sessione_presenza sp2 ON ((spr2.idsessionepresenza = sp2.idsessionepresenza)))
          GROUP BY spr2.idsessionepresenza) spr_count ON ((spr_count.idsessionepresenza = sp.idsessionepresenza)))
     LEFT JOIN public.sessione_telematica st ON (((c.idcorso = st.idcorso) AND (st.idchef = ch.idchef))))
     LEFT JOIN public.richiestapagamento rp ON ((rp.idcorso = c.idcorso)))
  GROUP BY ch.idchef, ch.nome, ch.cognome, (EXTRACT(month FROM COALESCE((sp.data)::timestamp without time zone, (st.data)::timestamp without time zone, rp.datarichiesta, (c.datainizio)::timestamp without time zone, (c.datafine)::timestamp without time zone))), (EXTRACT(year FROM COALESCE((sp.data)::timestamp without time zone, (st.data)::timestamp without time zone, rp.datarichiesta, (c.datainizio)::timestamp without time zone, (c.datafine)::timestamp without time zone)));


ALTER VIEW public.vista_statistiche_mensili_chef OWNER TO postgres;

--
-- TOC entry 3545 (class 0 OID 16920)
-- Dependencies: 235
-- Data for Name: adesione_sessionepresenza; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.adesione_sessionepresenza (conferma, idsessionepresenza, idpartecipante) FROM stdin;
t	52	1
\.


--
-- TOC entry 3528 (class 0 OID 16764)
-- Dependencies: 218
-- Data for Name: carta; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.carta (idcarta, intestatario, datascadenza, ultimequattrocifre, circuito) FROM stdin;
1	Alberto Ferrari	2028-01-31	1111	Visa
2	Beatrice Russo	2027-12-31	2222	Mastercard
3	Carlo Bianchi	2029-06-30	3333	Visa
4	Diana Verdi	2028-11-30	4444	Mastercard
5	Edoardo Gallo	2027-09-30	5555	Visa
6	Francesca Conti	2028-05-31	6666	Mastercard
7	Giorgio Leone	2029-03-31	7777	Visa
8	Helena Greco	2027-07-31	8888	Mastercard
9	Irene Fontana	2028-08-31	9999	Visa
10	Jacopo Rizzo	2029-01-31	0001	Mastercard
\.


--
-- TOC entry 3533 (class 0 OID 16795)
-- Dependencies: 223
-- Data for Name: chef; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.chef (idchef, nome, cognome, email, password, datadinascita, descrizione, propic, annidiesperienza) FROM stdin;
1	Mario	Rossi	mario.rossi@email.com	password1	1980-05-10	Specialista cucina italiana	\N	15
2	Luca	Bianchi	luca.bianchi@email.com	password2	1975-03-22	Esperto pasticceria	\N	20
3	Giulia	Verdi	giulia.verdi@email.com	password3	1985-07-15	Chef vegano	\N	12
4	Francesca	Neri	francesca.neri@email.com	password4	1990-11-30	Cucina fusion	\N	8
5	Alessandro	Russo	alessandro.russo@email.com	password5	1982-01-18	Cucina tradizionale	\N	18
6	Elena	Gallo	elena.gallo@email.com	password6	1988-09-05	Cucina mediterranea	\N	10
8	Martina	Greco	martina.greco@email.com	password8	1992-04-25	Cucina salutare	\N	7
9	Davide	Conti	davide.conti@email.com	password9	1983-06-17	Cucina gourmet	\N	16
10	Sara	Leone	sara.leone@email.com	password10	1986-08-29	Cucina regionale	\N	13
11	Yuki	Tanaka	yuki.tanaka@email.com	password11	1987-02-14	Specialista sushi	\N	14
12	Pierre	Dubois	pierre.dubois@email.com	password12	1979-10-03	Cucina francese	\N	20
13	Carlos	Ramirez	carlos.ramirez@email.com	password13	1984-06-21	Tapas e paella	\N	17
14	Anna	Schmidt	anna.schmidt@email.com	password14	1991-12-01	Cucina tedesca	\N	9
15	Fatima	Hassan	fatima.hassan@email.com	password15	1986-04-18	Cucina mediorientale	\N	13
16	Ivan	Petrov	ivan.petrov@email.com	password16	1982-03-15	Cucina russa	\N	18
17	Linda	Nguyen	linda.nguyen@email.com	password17	1990-07-22	Street food vietnamita	\N	10
18	John	Smith	john.smith@email.com	password18	1977-11-05	BBQ americano	\N	25
19	Maria	Silva	maria.silva@email.com	password19	1985-01-30	Cucina brasiliana	\N	15
20	Ahmed	Ali	ahmed.ali@email.com	password20	1983-09-12	Cucina egiziana	\N	17
21	Sofia	Kowalska	sofia.kowalska@email.com	password21	1988-05-18	Cucina polacca	\N	12
22	George	Papadopoulos	george.papadopoulos@email.com	password22	1979-04-09	Cucina greca	\N	20
23	Emily	Johnson	emily.johnson@email.com	password23	1993-10-27	Cucina gluten free	\N	7
24	Chen	Wei	chen.wei@email.com	password24	1986-06-02	Cucina cinese	\N	14
25	Amina	Benali	amina.benali@email.com	password25	1984-08-16	Cucina marocchina	\N	16
26	Olga	Ivanova	olga.ivanova@email.com	password26	1981-02-10	Cucina ucraina	\N	19
27	Raj	Patel	raj.patel@email.com	password27	1980-08-15	Cucina indiana	\N	20
28	Marta	Lopez	marta.lopez@email.com	password28	1987-03-22	Cucina spagnola	\N	13
29	Tom	Baker	tom.baker@email.com	password29	1975-12-01	Cucina inglese	\N	25
30	Nina	Klein	nina.klein@email.com	password30	1989-06-18	Cucina austriaca	\N	11
31	Ali	Yilmaz	ali.yilmaz@email.com	password31	1982-11-05	Cucina turca	\N	18
32	Sven	Larsen	sven.larsen@email.com	password32	1978-09-14	Cucina scandinava	\N	22
33	Julia	Müller	julia.mueller@email.com	password33	1986-04-27	Cucina tedesca	\N	14
34	Isabella	Ricci	isabella.ricci@email.com	password34	1992-01-19	Cucina vegetariana	\N	8
35	Paul	Dubois	paul.dubois@email.com	password35	1983-07-23	Cucina francese	\N	17
36	Samantha	Brown	samantha.brown@email.com	password36	1985-10-30	Cucina americana	\N	15
37	Hiroshi	Sato	hiroshi.sato@email.com	password37	1979-05-12	Cucina giapponese	\N	21
38	Anna	Nowak	anna.nowak@email.com	password38	1988-08-08	Cucina polacca	\N	12
39	Lucas	Silva	lucas.silva@email.com	password39	1984-03-17	Cucina brasiliana	\N	16
40	Fatou	Diop	fatou.diop@email.com	password40	1986-12-25	Cucina senegalese	\N	13
41	Ming	Zhang	ming.zhang@email.com	password41	1981-09-09	Cucina cinese	\N	19
42	Sofia	Garcia	sofia.garcia@email.com	password42	1990-02-14	Cucina argentina	\N	10
43	Peter	Schneider	peter.schneider@email.com	password43	1982-06-11	Cucina svizzera	\N	18
44	Layla	Haddad	layla.haddad@email.com	password44	1987-11-29	Cucina libanese	\N	13
45	Omar	Farouk	omar.farouk@email.com	password45	1983-04-03	Cucina araba	\N	17
46	Eva	Horvath	eva.horvath@email.com	password46	1985-07-21	Cucina ungherese	\N	15
47	Marek	Novak	marek.novak@email.com	password47	1980-10-16	Cucina ceca	\N	20
48	Helena	Jensen	helena.jensen@email.com	password48	1989-12-13	Cucina danese	\N	11
49	Lars	Eriksson	lars.eriksson@email.com	password49	1977-08-28	Cucina svedese	\N	23
50	Chloe	Martin	chloe.martin@email.com	password50	1991-05-06	Cucina francese moderna	\N	9
7	Simone	Fontana	simone.fontana@email.com	password77	1978-12-12	Cucina asiatica	\N	22
\.


--
-- TOC entry 3535 (class 0 OID 16806)
-- Dependencies: 225
-- Data for Name: corso; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.corso (idcorso, nome, descrizione, datainizio, datafine, frequenzadellesessioni, propic, maxpersone, prezzo, idchef) FROM stdin;
1	Corso di Pasta	Impara a fare la pasta fresca	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	120.00	1
2	Dolci Italiani	Pasticceria tradizionale	2025-09-02	2025-09-25	Martedì e Giovedì	\N	12	150.00	2
3	Cucina Vegana	Ricette vegane creative	2025-09-02	2025-09-25	Martedì e Giovedì	\N	8	110.00	3
4	Fusion Experience	Sapori dal mondo	2025-09-02	2025-09-25	Martedì e Giovedì	\N	15	130.00	4
5	Tradizione Italiana	Piatti tipici regionali	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	140.00	5
6	Mediterraneo	Cucina sana e gustosa	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	125.00	6
7	Asian Taste	Sapori d'Oriente	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	135.00	7
8	Healthy Cooking	Cucina salutare	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	115.00	8
9	Gourmet Lab	Tecniche gourmet	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	160.00	9
10	Sapori Regionali	Viaggio tra le regioni	2025-09-02	2025-09-25	Martedì e Giovedì	\N	10	145.00	10
11	Sushi Masterclass	Tecniche avanzate di sushi	2025-10-06	2025-10-20	Lunedì e Mercoledì	\N	8	180.00	11
12	Cuisine Française	Classici francesi	2025-10-07	2025-10-21	Martedì e Venerdì	\N	10	170.00	12
13	Fiesta Española	Tapas e paella	2025-10-08	2025-10-22	Mercoledì e Sabato	\N	12	150.00	13
14	Sapori di Baviera	Piatti tipici tedeschi	2025-10-09	2025-10-23	Giovedì e Domenica	\N	9	140.00	14
15	Middle East Flavours	Cucina mediorientale	2025-10-10	2025-10-24	Venerdì e Sabato	\N	11	160.00	15
16	Sapori di Mosca	Piatti tipici russi	2025-11-03	2025-11-17	Lunedì e Giovedì	\N	10	150.00	16
17	Pho & Banh Mi	Street food vietnamita	2025-11-04	2025-11-18	Martedì e Sabato	\N	12	120.00	17
18	American BBQ	Grigliate e affumicature	2025-11-05	2025-11-19	Mercoledì e Domenica	\N	15	180.00	18
19	Brasil Sabor	Cucina brasiliana	2025-11-06	2025-11-20	Giovedì e Venerdì	\N	11	140.00	19
20	Sapori del Nilo	Cucina egiziana	2025-11-07	2025-11-21	Venerdì e Sabato	\N	9	130.00	20
21	Polonia in Tavola	Piatti tradizionali polacchi	2025-11-08	2025-11-22	Sabato e Lunedì	\N	10	125.00	21
22	Grecia Gourmet	Cucina greca autentica	2025-11-09	2025-11-23	Domenica e Mercoledì	\N	13	135.00	22
23	Gluten Free Lab	Cucina senza glutine	2025-11-10	2025-11-24	Lunedì e Martedì	\N	8	110.00	23
24	China Taste	Sapori della Cina	2025-11-11	2025-11-25	Martedì e Venerdì	\N	14	160.00	24
25	Moroccan Magic	Cucina marocchina	2025-11-12	2025-11-26	Mercoledì e Sabato	\N	12	145.00	25
26	Sapori di Kiev	Cucina ucraina tradizionale	2025-12-01	2025-12-15	Lunedì e Giovedì	\N	10	130.00	26
27	India Spice	Spezie e curry indiani	2025-12-02	2025-12-16	Martedì e Venerdì	\N	12	140.00	27
28	Tapas y Paella	Specialità spagnole	2025-12-03	2025-12-17	Mercoledì e Sabato	\N	14	135.00	28
29	British Classics	Piatti inglesi iconici	2025-12-04	2025-12-18	Giovedì e Domenica	\N	11	120.00	29
30	Austria Gourmet	Sapori austriaci	2025-12-05	2025-12-19	Venerdì e Lunedì	\N	9	125.00	30
31	Istanbul Street Food	Cibo di strada turco	2025-12-06	2025-12-20	Sabato e Martedì	\N	13	115.00	31
32	Nordic Cuisine	Cucina scandinava moderna	2025-12-07	2025-12-21	Domenica e Mercoledì	\N	10	150.00	32
33	Bavarian Flavours	Piatti tipici tedeschi	2025-12-08	2025-12-22	Lunedì e Giovedì	\N	12	140.00	33
34	Veggie World	Cucina vegetariana creativa	2025-12-09	2025-12-23	Martedì e Venerdì	\N	8	110.00	34
35	Paris Bistro	Cucina francese bistrot	2025-12-10	2025-12-24	Mercoledì e Sabato	\N	14	160.00	35
36	American Diner	Classici USA	2025-12-11	2025-12-25	Giovedì e Domenica	\N	10	120.00	36
37	Tokyo Nights	Cucina giapponese moderna	2025-12-12	2025-12-26	Venerdì e Lunedì	\N	11	170.00	37
38	Polish Table	Sapori della Polonia	2025-12-13	2025-12-27	Sabato e Martedì	\N	9	115.00	38
39	Brasil Fusion	Fusion brasiliana	2025-12-14	2025-12-28	Domenica e Mercoledì	\N	13	145.00	39
40	Senegal Taste	Cucina senegalese	2025-12-15	2025-12-29	Lunedì e Giovedì	\N	10	130.00	40
41	China Street	Street food cinese	2025-12-16	2025-12-30	Martedì e Venerdì	\N	12	140.00	41
42	Buenos Aires Grill	Grigliate argentine	2025-12-17	2025-12-31	Mercoledì e Sabato	\N	14	135.00	42
43	Swiss Alps	Cucina svizzera	2025-12-18	2026-01-01	Giovedì e Domenica	\N	11	120.00	43
44	Beirut Flavours	Sapori libanesi	2025-12-19	2026-01-02	Venerdì e Lunedì	\N	9	125.00	44
45	Arabian Nights	Cucina araba	2025-12-20	2026-01-03	Sabato e Martedì	\N	13	115.00	45
46	Hungarian Soul	Cucina ungherese	2025-12-21	2026-01-04	Domenica e Mercoledì	\N	10	150.00	46
47	Czech Delights	Piatti cechi tradizionali	2025-12-22	2026-01-05	Lunedì e Giovedì	\N	12	140.00	47
48	Danish Hygge	Cucina danese	2025-12-23	2026-01-06	Martedì e Venerdì	\N	8	110.00	48
49	Swedish Smorgasbord	Buffet svedese	2025-12-24	2026-01-07	Mercoledì e Sabato	\N	14	160.00	49
50	French Modern	Cucina francese contemporanea	2025-12-25	2026-01-08	Giovedì e Domenica	\N	10	120.00	50
\.


--
-- TOC entry 3550 (class 0 OID 16957)
-- Dependencies: 240
-- Data for Name: ingrediente; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.ingrediente (idingrediente, nome, unitadimisura) FROM stdin;
1	Farina	Grammi
2	Caffè	Centilitro
3	Ceci	Grammi
4	Riso	Grammi
5	Pasta	Grammi
6	Feta	Grammi
7	Noodles	Grammi
8	Spinaci	Grammi
9	Manzo	Grammi
10	Melanzane	Grammi
11	Patate	Grammi
12	Ricotta	Grammi
13	Tofu	Grammi
14	Latte di Cocco	Centilitro
15	Riso Arborio	Grammi
16	Pomodoro	Grammi
17	Brodo	Litro
18	Mela	Grammi
19	Tonno	Grammi
20	Funghi	Grammi
21	Burro	Grammi
22	Panna	Centilitro
23	Fave	Grammi
24	Maiale	Grammi
25	Cime di Rapa	Grammi
26	Semola	Grammi
27	Gamberi	Grammi
28	Quinoa	Grammi
29	Vitello	Grammi
30	Riso	Grammi
31	Riso per Sushi	Grammi
32	Salmone	Grammi
33	Pasta Brisée	Grammi
34	Manzo	Grammi
35	Riso	Grammi
36	Uova	Unità
37	Salsiccia	Grammi
38	Cavolo	Grammi
39	Ceci	Grammi
40	Prezzemolo	Grammi
41	Barbabietola	Grammi
42	Carne di Manzo	Grammi
43	Noodles di Riso	Grammi
44	Pane	Grammi
45	Maiale	Grammi
46	Farina di Mais	Grammi
47	Fagioli Neri	Grammi
48	Formaggio	Grammi
49	Riso	Grammi
50	Lattuga	Grammi
51	Patate	Grammi
52	Crauti	Grammi
53	Melanzane	Grammi
54	Carne di Maiale	Grammi
55	Farina di Riso	Grammi
56	Cavolo Cinese	Grammi
57	Agnello	Grammi
58	Ceci	Grammi
59	Pollo	Grammi
60	Pasta Fillo	Grammi
61	Patate Dolci	Grammi
62	Pollo	Grammi
63	Pomodori	Grammi
64	Merluzzo	Grammi
65	Cioccolato	Grammi
66	Agnello	Grammi
67	Salmone	Grammi
68	Cetrioli	Grammi
69	Zucchine	Grammi
70	Mele	Grammi
71	Cavolo	Grammi
72	Crauti	Grammi
73	Pesce	Grammi
74	Limone	Grammi
75	Tofu	Grammi
76	Carne Macinata	Grammi
77	Formaggio Svizzero	Grammi
78	Prezzemolo	Grammi
79	Uova	Unità
80	Paprika	Grammi
81	Pane	Grammi
82	Aringa	Grammi
83	Panna	Centilitro
84	Latte	Centilitro
85	Bacon	Grammi
\.


--
-- TOC entry 3530 (class 0 OID 16770)
-- Dependencies: 220
-- Data for Name: partecipante; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.partecipante (idpartecipante, nome, cognome, email, password, datadinascita, propic) FROM stdin;
1	Alberto	Ferrari	alberto.ferrari@email.com	pass1	1990-01-15	\N
2	Beatrice	Russo	beatrice.russo@email.com	pass2	1992-02-20	\N
3	Carlo	Bianchi	carlo.bianchi@email.com	pass3	1988-03-10	\N
4	Diana	Verdi	diana.verdi@email.com	pass4	1995-04-25	\N
5	Edoardo	Gallo	edoardo.gallo@email.com	pass5	1987-05-30	\N
6	Francesca	Conti	francesca.conti@email.com	pass6	1991-06-12	\N
7	Giorgio	Leone	giorgio.leone@email.com	pass7	1989-07-08	\N
8	Helena	Greco	helena.greco@email.com	pass8	1993-08-19	\N
9	Irene	Fontana	irene.fontana@email.com	pass9	1994-09-22	\N
10	Jacopo	Rizzo	jacopo.rizzo@email.com	pass10	1996-10-11	\N
11	Katia	Marino	katia.marino@email.com	pass11	1990-11-05	\N
12	Lorenzo	De Luca	lorenzo.deluca@email.com	pass12	1986-12-17	\N
13	Martina	Serra	martina.serra@email.com	pass13	1992-01-23	\N
14	Nicola	Vitale	nicola.vitale@email.com	pass14	1988-02-14	\N
15	Olga	Ferraro	olga.ferraro@email.com	pass15	1995-03-29	\N
16	Paolo	Sanna	paolo.sanna@email.com	pass16	1991-04-07	\N
17	Quirino	Costa	quirino.costa@email.com	pass17	1987-05-18	\N
18	Rita	Mancini	rita.mancini@email.com	pass18	1993-06-21	\N
19	Stefano	Barone	stefano.barone@email.com	pass19	1994-07-15	\N
20	Teresa	Longo	teresa.longo@email.com	pass20	1996-08-28	\N
21	Ugo	Rinaldi	ugo.rinaldi@email.com	pass21	1990-09-09	\N
22	Valeria	Fabbri	valeria.fabbri@email.com	pass22	1986-10-13	\N
23	Walter	Testa	walter.testa@email.com	pass23	1992-11-27	\N
24	Xenia	Pagano	xenia.pagano@email.com	pass24	1988-12-03	\N
25	Yuri	Coppola	yuri.coppola@email.com	pass25	1995-01-16	\N
26	Zara	Fiore	zara.fiore@email.com	pass26	1991-02-22	\N
27	Alessia	Riva	alessia.riva@email.com	pass27	1987-03-14	\N
28	Bruno	Sereni	bruno.sereni@email.com	pass28	1993-04-19	\N
29	Clara	Monti	clara.monti@email.com	pass29	1994-05-23	\N
30	Dario	Sarti	dario.sarti@email.com	pass30	1996-06-30	\N
31	Elisa	Basso	elisa.basso@email.com	pass31	1990-07-12	\N
32	Fabio	Gatti	fabio.gatti@email.com	pass32	1986-08-25	\N
33	Giada	Rossi	giada.rossi@email.com	pass33	1992-09-17	\N
34	Hugo	Mazza	hugo.mazza@email.com	pass34	1988-10-29	\N
35	Isabella	Pace	isabella.pace@email.com	pass35	1995-11-11	\N
36	Luca	Sole	luca.sole@email.com	pass36	1991-12-24	\N
37	Marta	Valli	marta.valli@email.com	pass37	1987-01-08	\N
38	Nadia	Fiori	nadia.fiori@email.com	pass38	1993-02-15	\N
39	Oscar	Belli	oscar.belli@email.com	pass39	1994-03-27	\N
40	Pietro	Rossi	pietro.rossi@email.com	pass40	1996-04-09	\N
41	Quinta	Lodi	quinta.lodi@email.com	pass41	1990-05-21	\N
42	Rocco	Moro	rocco.moro@email.com	pass42	1986-06-13	\N
43	Sonia	Bassi	sonia.bassi@email.com	pass43	1992-07-25	\N
44	Tiziano	Gallo	tiziano.gallo@email.com	pass44	1988-08-07	\N
45	Ursula	Serra	ursula.serra@email.com	pass45	1995-09-19	\N
46	Vittorio	Neri	vittorio.neri@email.com	pass46	1991-10-31	\N
47	Wanda	Costa	wanda.costa@email.com	pass47	1987-11-23	\N
48	Xavier	Pagani	xavier.pagani@email.com	pass48	1993-12-05	\N
49	Ylenia	Coppola	ylenia.coppola@email.com	pass49	1994-01-17	\N
50	Zeno	Fiore	zeno.fiore@email.com	pass50	1996-02-28	\N
\.


--
-- TOC entry 3544 (class 0 OID 16905)
-- Dependencies: 234
-- Data for Name: partecipante_sessionetelematica; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.partecipante_sessionetelematica (idpartecipante, idsessionetelematica) FROM stdin;
\.


--
-- TOC entry 3531 (class 0 OID 16779)
-- Dependencies: 221
-- Data for Name: possiede; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.possiede (idpartecipante, idcarta) FROM stdin;
1	1
2	2
3	3
4	4
5	5
6	6
7	7
8	8
9	9
10	10
\.


--
-- TOC entry 3551 (class 0 OID 16962)
-- Dependencies: 241
-- Data for Name: preparazioneingrediente; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.preparazioneingrediente (idricetta, idingrediente, quanititaunitaria) FROM stdin;
1	1	100.00
2	2	50.00
3	3	120.00
4	4	80.00
5	5	150.00
6	6	60.00
7	7	90.00
8	8	70.00
9	9	110.00
10	10	100.00
11	11	130.00
12	12	40.00
13	13	100.00
14	14	60.00
15	15	120.00
16	16	80.00
17	17	200.00
18	18	50.00
19	19	90.00
20	20	100.00
21	21	110.00
22	22	60.00
23	23	100.00
24	24	80.00
25	25	120.00
26	26	80.00
27	27	90.00
28	28	70.00
29	29	110.00
30	30	100.00
31	31	120.00
32	32	80.00
33	33	100.00
34	34	150.00
35	35	200.00
36	36	2.00
37	37	110.00
38	38	90.00
39	39	130.00
40	40	20.00
41	41	120.00
42	42	100.00
43	43	150.00
44	44	80.00
45	45	200.00
46	46	90.00
47	47	180.00
48	48	60.00
49	49	130.00
50	50	50.00
51	51	110.00
52	52	70.00
53	53	140.00
54	54	100.00
55	55	90.00
56	56	80.00
57	57	160.00
58	58	120.00
59	59	110.00
60	60	60.00
61	61	120.00
62	62	150.00
63	63	100.00
64	64	200.00
65	65	80.00
66	66	110.00
67	67	90.00
68	68	70.00
69	69	130.00
70	70	100.00
71	71	140.00
72	72	120.00
73	73	160.00
74	74	110.00
75	75	100.00
76	76	130.00
77	77	90.00
78	78	80.00
79	79	60.00
80	80	150.00
81	81	120.00
82	82	110.00
83	83	100.00
84	84	90.00
85	85	130.00
\.


--
-- TOC entry 3547 (class 0 OID 16936)
-- Dependencies: 237
-- Data for Name: ricetta; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.ricetta (idricetta, nome) FROM stdin;
1	Tagliatelle al Ragù
2	Tiramisù
3	Burger di Ceci
4	Sushi Roll
5	Lasagna
6	Insalata Greca
7	Pad Thai
8	Zuppa Detox
9	Filetto al Pepe
10	Caponata
11	Gnocchi di Patate
12	Cannoli Siciliani
13	Tofu Saltato
14	Curry Verde
15	Risotto alla Milanese
16	Pasta alla Norma
17	Ramen
18	Smoothie Verde
19	Tartare di Tonno
20	Polenta e Funghi
21	Fettuccine Alfredo
22	Panna Cotta
23	Falafel
24	Baozi
25	Orecchiette alle Cime di Rapa
26	Cous Cous
27	Tempura
28	Insalata di Quinoa
29	Vitello Tonnato
30	Arancini
31	Nigiri
32	Maki Roll
33	Quiche Lorraine
34	Boeuf Bourguignon
35	Paella Valenciana
36	Tortilla Española
37	Bratwurst
38	Sauerkraut
39	Hummus
40	Falafel
41	Borscht
42	Pelmeni
43	Pho
44	Banh Mi
45	Pulled Pork
46	Cornbread
47	Feijoada
48	Pão de Queijo
49	Koshari
50	Molokhia
51	Pierogi
52	Bigos
53	Moussaka
54	Souvlaki
55	Pancakes Gluten Free
56	Baozi
57	Jiaozi
58	Couscous Marocchino
59	Tajine
60	Baklava
61	Varenyky
62	Chicken Tikka
63	Gazpacho
64	Fish and Chips
65	Sachertorte
66	Kebab Turco
67	Gravlax
68	Kartoffelsalat
69	Ratatouille
70	Apple Pie
71	Okonomiyaki
72	Bigos Polacco
73	Moqueca
74	Yassa
75	Mapo Tofu
76	Empanadas
77	Rösti
78	Tabbouleh
79	Shakshuka
80	Gulasch
81	Knedlíky
82	Smørrebrød
83	Köttbullar
84	Soufflé
85	Tartiflette
\.


--
-- TOC entry 3536 (class 0 OID 16830)
-- Dependencies: 226
-- Data for Name: richiestapagamento; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.richiestapagamento (datarichiesta, statopagamento, importopagato, idcorso, idpartecipante) FROM stdin;
2025-08-01 00:00:00	Pagato	120.00	1	1
2025-08-01 00:00:00	Pagato	150.00	2	2
2025-08-01 00:00:00	Pagato	110.00	3	3
2025-08-01 00:00:00	Pagato	130.00	4	4
2025-08-01 00:00:00	Pagato	140.00	5	5
2025-08-01 00:00:00	Pagato	125.00	6	6
2025-08-01 00:00:00	Pagato	135.00	7	7
2025-08-01 00:00:00	Pagato	115.00	8	8
2025-08-01 00:00:00	Pagato	160.00	9	9
2025-08-01 00:00:00	Pagato	145.00	10	10
2025-08-01 00:00:00	Pagato	180.00	11	11
2025-08-01 00:00:00	Pagato	170.00	12	12
2025-08-01 00:00:00	Pagato	150.00	13	13
2025-08-01 00:00:00	Pagato	140.00	14	14
2025-08-01 00:00:00	Pagato	160.00	15	15
2025-08-01 00:00:00	Pagato	150.00	16	16
2025-08-01 00:00:00	Pagato	120.00	17	17
2025-08-01 00:00:00	Pagato	180.00	18	18
2025-08-01 00:00:00	Pagato	140.00	19	19
2025-08-01 00:00:00	Pagato	130.00	20	20
\.


--
-- TOC entry 3541 (class 0 OID 16870)
-- Dependencies: 231
-- Data for Name: sessione_presenza; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.sessione_presenza (idsessionepresenza, giorno, data, orario, durata, citta, via, cap, descrizione, idcorso, idchef) FROM stdin;
1	Martedì	2026-01-13	18:00:00	02:00:00	Kiev	Via Ucraina 26	01001	Varenyky	26	26
2	Venerdì	2026-01-16	18:00:00	02:00:00	Kiev	Via Ucraina 26	01001	Varenyky	26	26
3	Martedì	2026-01-13	18:00:00	02:00:00	Delhi	Via India 27	11000	Chicken Tikka	27	27
4	Venerdì	2026-01-16	18:00:00	02:00:00	Delhi	Via India 27	11000	Chicken Tikka	27	27
5	Martedì	2026-01-13	18:00:00	02:00:00	Madrid	Via Spagna 28	28001	Gazpacho	28	28
6	Venerdì	2026-01-16	18:00:00	02:00:00	Madrid	Via Spagna 28	28001	Gazpacho	28	28
7	Martedì	2026-01-13	18:00:00	02:00:00	Londra	Via UK 29	SW1A 	Fish and Chips	29	29
8	Venerdì	2026-01-16	18:00:00	02:00:00	Londra	Via UK 29	SW1A 	Fish and Chips	29	29
9	Martedì	2026-01-13	18:00:00	02:00:00	Vienna	Via Austria 30	10100	Sachertorte	30	30
10	Venerdì	2026-01-16	18:00:00	02:00:00	Vienna	Via Austria 30	10100	Sachertorte	30	30
11	Martedì	2026-01-13	18:00:00	02:00:00	Istanbul	Via Turchia 31	34000	Kebab Turco	31	31
12	Venerdì	2026-01-16	18:00:00	02:00:00	Istanbul	Via Turchia 31	34000	Kebab Turco	31	31
13	Martedì	2026-01-13	18:00:00	02:00:00	Stoccolma	Via Svezia 32	11120	Gravlax	32	32
14	Venerdì	2026-01-16	18:00:00	02:00:00	Stoccolma	Via Svezia 32	11120	Gravlax	32	32
15	Martedì	2026-01-13	18:00:00	02:00:00	Monaco	Via Germania 33	80331	Kartoffelsalat	33	33
16	Venerdì	2026-01-16	18:00:00	02:00:00	Monaco	Via Germania 33	80331	Kartoffelsalat	33	33
17	Martedì	2026-01-13	18:00:00	02:00:00	Parigi	Via Francia 34	75001	Ratatouille	34	34
18	Venerdì	2026-01-16	18:00:00	02:00:00	Parigi	Via Francia 34	75001	Ratatouille	34	34
19	Martedì	2026-01-13	18:00:00	02:00:00	New York	Via USA 35	10001	Apple Pie	35	35
20	Venerdì	2026-01-16	18:00:00	02:00:00	New York	Via USA 35	10001	Apple Pie	35	35
21	Martedì	2026-01-13	18:00:00	02:00:00	Osaka	Via Giappone 36	53000	Okonomiyaki	36	36
22	Venerdì	2026-01-16	18:00:00	02:00:00	Osaka	Via Giappone 36	53000	Okonomiyaki	36	36
23	Martedì	2026-01-13	18:00:00	02:00:00	Varsavia	Via Polonia 37	00001	Bigos Polacco	37	37
24	Venerdì	2026-01-16	18:00:00	02:00:00	Varsavia	Via Polonia 37	00001	Bigos Polacco	37	37
25	Martedì	2026-01-13	18:00:00	02:00:00	Salvador	Via Brasile 38	40000	Moqueca	38	38
26	Venerdì	2026-01-16	18:00:00	02:00:00	Salvador	Via Brasile 38	40000	Moqueca	38	38
27	Martedì	2026-01-13	18:00:00	02:00:00	Dakar	Via Senegal 39	10000	Yassa	39	39
28	Venerdì	2026-01-16	18:00:00	02:00:00	Dakar	Via Senegal 39	10000	Yassa	39	39
29	Martedì	2026-01-13	18:00:00	02:00:00	Chengdu	Via Cina 40	61000	Mapo Tofu	40	40
30	Venerdì	2026-01-16	18:00:00	02:00:00	Chengdu	Via Cina 40	61000	Mapo Tofu	40	40
31	Martedì	2026-01-13	18:00:00	02:00:00	Buenos Aires	Via Argentina 41	C100 	Empanadas	41	41
32	Venerdì	2026-01-16	18:00:00	02:00:00	Buenos Aires	Via Argentina 41	C100 	Empanadas	41	41
33	Martedì	2026-01-13	18:00:00	02:00:00	Zurigo	Via Svizzera 42	80001	Rösti	42	42
34	Venerdì	2026-01-16	18:00:00	02:00:00	Zurigo	Via Svizzera 42	80001	Rösti	42	42
35	Martedì	2026-01-13	18:00:00	02:00:00	Beirut	Via Libano 43	01100	Tabbouleh	43	43
36	Venerdì	2026-01-16	18:00:00	02:00:00	Beirut	Via Libano 43	01100	Tabbouleh	43	43
37	Martedì	2026-01-13	18:00:00	02:00:00	Gerusalemme	Via Israele 44	91000	Shakshuka	44	44
38	Venerdì	2026-01-16	18:00:00	02:00:00	Gerusalemme	Via Israele 44	91000	Shakshuka	44	44
39	Martedì	2026-01-13	18:00:00	02:00:00	Budapest	Via Ungheria 45	10510	Gulasch	45	45
40	Venerdì	2026-01-16	18:00:00	02:00:00	Budapest	Via Ungheria 45	10510	Gulasch	45	45
41	Martedì	2026-01-13	18:00:00	02:00:00	Praga	Via Cechia 46	11000	Knedlíky	46	46
42	Venerdì	2026-01-16	18:00:00	02:00:00	Praga	Via Cechia 46	11000	Knedlíky	46	46
43	Martedì	2026-01-13	18:00:00	02:00:00	Copenaghen	Via Danimarca 47	01000	Smørrebrød	47	47
44	Venerdì	2026-01-16	18:00:00	02:00:00	Copenaghen	Via Danimarca 47	01000	Smørrebrød	47	47
45	Martedì	2026-01-13	18:00:00	02:00:00	Stoccolma	Via Svezia 48	11120	Köttbullar	48	48
46	Venerdì	2026-01-16	18:00:00	02:00:00	Stoccolma	Via Svezia 48	11120	Köttbullar	48	48
47	Martedì	2026-01-13	18:00:00	02:00:00	Parigi	Via Francia 49	75001	Soufflé	49	49
48	Venerdì	2026-01-16	18:00:00	02:00:00	Parigi	Via Francia 49	75001	Soufflé	49	49
49	Martedì	2026-01-13	18:00:00	02:00:00	Annecy	Via Francia 50	74000	Tartiflette	50	50
50	Venerdì	2026-01-16	18:00:00	02:00:00	Annecy	Via Francia 50	74000	Tartiflette	50	50
51	Martedì	2025-09-02	18:00:00	02:00:00	Napoli	Via Roma 1	80100	Pasta fresca	1	1
52	Giovedì	2025-09-04	18:00:00	02:00:00	Napoli	Via Roma 1	80100	Pasta fresca	1	1
53	Martedì	2025-09-09	18:00:00	02:00:00	Napoli	Via Roma 1	80100	Pasta fresca	1	1
54	Giovedì	2025-09-11	18:00:00	02:00:00	Napoli	Via Roma 1	80100	Pasta fresca	1	1
55	Martedì	2025-09-02	18:00:00	02:00:00	Milano	Via Milano 2	20100	Dolci	2	2
56	Giovedì	2025-09-04	18:00:00	02:00:00	Milano	Via Milano 2	20100	Dolci	2	2
57	Martedì	2025-09-09	18:00:00	02:00:00	Milano	Via Milano 2	20100	Dolci	2	2
58	Giovedì	2025-09-11	18:00:00	02:00:00	Milano	Via Milano 2	20100	Dolci	2	2
59	Martedì	2025-09-02	18:00:00	02:00:00	Roma	Via Roma 3	00100	Vegano	3	3
60	Giovedì	2025-09-04	18:00:00	02:00:00	Roma	Via Roma 3	00100	Vegano	3	3
61	Martedì	2025-09-09	18:00:00	02:00:00	Roma	Via Roma 3	00100	Vegano	3	3
62	Giovedì	2025-09-11	18:00:00	02:00:00	Roma	Via Roma 3	00100	Vegano	3	3
63	Martedì	2025-09-02	18:00:00	02:00:00	Torino	Via Torino 4	10100	Fusion	4	4
64	Giovedì	2025-09-04	18:00:00	02:00:00	Torino	Via Torino 4	10100	Fusion	4	4
65	Martedì	2025-09-09	18:00:00	02:00:00	Torino	Via Torino 4	10100	Fusion	4	4
66	Giovedì	2025-09-11	18:00:00	02:00:00	Torino	Via Torino 4	10100	Fusion	4	4
67	Martedì	2025-09-02	18:00:00	02:00:00	Bologna	Via Bologna 5	40100	Tradizione	5	5
68	Giovedì	2025-09-04	18:00:00	02:00:00	Bologna	Via Bologna 5	40100	Tradizione	5	5
69	Martedì	2025-09-09	18:00:00	02:00:00	Bologna	Via Bologna 5	40100	Tradizione	5	5
70	Giovedì	2025-09-11	18:00:00	02:00:00	Bologna	Via Bologna 5	40100	Tradizione	5	5
71	Martedì	2025-09-02	18:00:00	02:00:00	Firenze	Via Firenze 6	50100	Mediterraneo	6	6
72	Giovedì	2025-09-04	18:00:00	02:00:00	Firenze	Via Firenze 6	50100	Mediterraneo	6	6
73	Martedì	2025-09-09	18:00:00	02:00:00	Firenze	Via Firenze 6	50100	Mediterraneo	6	6
74	Giovedì	2025-09-11	18:00:00	02:00:00	Firenze	Via Firenze 6	50100	Mediterraneo	6	6
75	Martedì	2025-09-02	18:00:00	02:00:00	Venezia	Via Venezia 7	30100	Asia	7	7
76	Giovedì	2025-09-04	18:00:00	02:00:00	Venezia	Via Venezia 7	30100	Asia	7	7
77	Martedì	2025-09-09	18:00:00	02:00:00	Venezia	Via Venezia 7	30100	Asia	7	7
78	Giovedì	2025-09-11	18:00:00	02:00:00	Venezia	Via Venezia 7	30100	Asia	7	7
79	Martedì	2025-09-02	18:00:00	02:00:00	Genova	Via Genova 8	16100	Salute	8	8
80	Giovedì	2025-09-04	18:00:00	02:00:00	Genova	Via Genova 8	16100	Salute	8	8
81	Martedì	2025-09-09	18:00:00	02:00:00	Genova	Via Genova 8	16100	Salute	8	8
82	Giovedì	2025-09-11	18:00:00	02:00:00	Genova	Via Genova 8	16100	Salute	8	8
83	Martedì	2025-09-02	18:00:00	02:00:00	Parma	Via Parma 9	43100	Gourmet	9	9
84	Giovedì	2025-09-04	18:00:00	02:00:00	Parma	Via Parma 9	43100	Gourmet	9	9
85	Martedì	2025-09-09	18:00:00	02:00:00	Parma	Via Parma 9	43100	Gourmet	9	9
86	Giovedì	2025-09-11	18:00:00	02:00:00	Parma	Via Parma 9	43100	Gourmet	9	9
87	Martedì	2025-09-02	18:00:00	02:00:00	Palermo	Via Palermo 10	90100	Regionale	10	10
88	Giovedì	2025-09-04	18:00:00	02:00:00	Palermo	Via Palermo 10	90100	Regionale	10	10
89	Martedì	2025-09-09	18:00:00	02:00:00	Palermo	Via Palermo 10	90100	Regionale	10	10
90	Giovedì	2025-09-11	18:00:00	02:00:00	Palermo	Via Palermo 10	90100	Regionale	10	10
91	Lunedì	2025-11-03	18:00:00	02:00:00	Mosca	Red Square 1	10100	Borscht	16	16
92	Giovedì	2025-11-06	18:00:00	02:00:00	Mosca	Red Square 1	10100	Pelmeni	16	16
93	Martedì	2025-11-04	19:00:00	02:00:00	Hanoi	Pho St 2	10000	Pho	17	17
94	Sabato	2025-11-08	19:00:00	02:00:00	Hanoi	Pho St 2	10000	Banh Mi	17	17
95	Mercoledì	2025-11-05	20:00:00	02:00:00	Dallas	BBQ Ave 3	75201	Pulled Pork	18	18
96	Domenica	2025-11-09	20:00:00	02:00:00	Dallas	BBQ Ave 3	75201	Cornbread	18	18
97	Giovedì	2025-11-06	18:30:00	02:00:00	Rio	Rua Brasil 4	20000	Feijoada	19	19
98	Venerdì	2025-11-13	18:30:00	02:00:00	Rio	Rua Brasil 4	20000	Pão de Queijo	19	19
99	Venerdì	2025-11-07	19:00:00	02:00:00	Il Cairo	Nilo St 5	11511	Koshari	20	20
100	Sabato	2025-11-14	19:00:00	02:00:00	Il Cairo	Nilo St 5	11511	Molokhia	20	20
101	Sabato	2025-11-08	18:00:00	02:00:00	Varsavia	Polska 6	00001	Pierogi	21	21
102	Lunedì	2025-11-10	18:00:00	02:00:00	Varsavia	Polska 6	00001	Bigos	21	21
103	Domenica	2025-11-09	19:30:00	02:00:00	Atene	Odos 7	10558	Moussaka	22	22
104	Mercoledì	2025-11-12	19:30:00	02:00:00	Atene	Odos 7	10558	Souvlaki	22	22
105	Lunedì	2025-11-10	17:00:00	02:00:00	Londra	GF St 8	EC1A 	Pancakes GF	23	23
106	Martedì	2025-11-11	17:00:00	02:00:00	Londra	GF St 8	EC1A 	Baozi	23	23
107	Martedì	2025-11-11	18:30:00	02:00:00	Pechino	China Rd 9	10000	Jiaozi	24	24
108	Venerdì	2025-11-14	18:30:00	02:00:00	Pechino	China Rd 9	10000	Couscous	24	24
109	Mercoledì	2025-11-12	19:00:00	02:00:00	Marrakech	Souk 10	40000	Tajine	25	25
110	Sabato	2025-11-15	19:00:00	02:00:00	Marrakech	Souk 10	40000	Baklava	25	25
\.


--
-- TOC entry 3548 (class 0 OID 16941)
-- Dependencies: 238
-- Data for Name: sessione_presenza_ricetta; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.sessione_presenza_ricetta (idricetta, idsessionepresenza) FROM stdin;
2	2
3	3
4	4
5	5
7	7
8	8
9	9
10	10
12	12
13	13
14	14
15	15
17	17
18	18
19	19
20	20
22	22
23	23
24	24
25	25
27	27
28	28
29	29
30	30
32	42
33	43
34	44
35	45
37	47
38	48
39	49
40	50
42	52
43	53
44	54
45	55
47	57
48	58
49	59
50	60
52	62
53	63
54	64
55	65
56	66
57	67
58	68
59	69
60	70
61	71
61	72
62	73
62	74
63	75
63	76
64	77
64	78
65	79
65	80
66	81
66	82
67	83
67	84
68	85
68	86
69	87
69	88
70	89
70	90
71	91
71	92
72	93
72	94
73	95
73	96
74	97
74	98
75	99
75	100
76	101
76	102
77	103
77	104
78	105
78	106
79	107
79	108
80	109
80	110
61	1
63	6
66	11
68	16
71	21
73	26
76	31
76	32
77	33
77	34
78	35
78	36
79	37
79	38
80	39
80	40
81	41
83	46
\.


--
-- TOC entry 3543 (class 0 OID 16888)
-- Dependencies: 233
-- Data for Name: sessione_telematica; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.sessione_telematica (idsessionetelematica, applicazione, codicechiamata, data, orario, durata, giorno, descrizione, idcorso, idchef) FROM stdin;
\.


--
-- TOC entry 3538 (class 0 OID 16847)
-- Dependencies: 228
-- Data for Name: tipodicucina; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.tipodicucina (idtipocucina, nome) FROM stdin;
1	Italiana
2	Pasticceria
3	Vegana
4	Fusion
5	Tradizionale
6	Mediterranea
7	Asiatica
8	Salutare
9	Gourmet
10	Regionale
11	Francese
12	Spagnola
13	Tedesca
14	Mediorientale
15	Russa
16	Vietnamita
17	Americana
18	Brasiliana
19	Egiziana
20	Polacca
21	Greca
22	Gluten Free
23	Cinese
24	Marocchina
25	Ucraina
26	Indiana
27	Inglese
28	Austriaca
29	Turca
30	Scandinava
31	Vegetariana
32	Giappone
33	Senegalese
34	Argentina
35	Svizzera
36	Libanese
37	Araba
38	Ungherese
39	Ceca
40	Danese
41	Svedese
42	Francese Moderna
43	Francese Bistrot
44	Francese Contemporanea
45	Giapponese Moderna
46	Fusion Brasiliana
47	Greca Gourmet
48	Austria Gourmet
49	Cucina Tradizionale Ucraina
50	Cucina Senza Glutine
51	Cucina Vegetariana Creativa
52	Cucina Francese Contemporanea
53	Cucina Francese Moderna
54	Cucina Francese Bistrot
55	Cucina Giapponese Moderna
56	Cucina Greca Gourmet
57	Cucina Austria Gourmet
58	Cucina Ucraina Tradizionale
59	Cucina Americana
60	Cucina Polacca
61	Cucina Brasiliana
62	Cucina Indiana
63	Cucina Inglese
64	Cucina Turca
65	Cucina Scandinava
66	Cucina Danese
67	Cucina Svedese
68	Cucina Libanese
69	Cucina Araba
70	Cucina Ungherese
71	Cucina Ceca
72	Cucina Svizzera
\.


--
-- TOC entry 3539 (class 0 OID 16854)
-- Dependencies: 229
-- Data for Name: tipodicucina_corso; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.tipodicucina_corso (idtipocucina, idcorso) FROM stdin;
1	1
2	2
3	3
4	4
5	5
6	6
7	7
8	8
9	9
10	10
11	11
12	12
13	13
14	14
15	15
16	16
17	17
18	18
19	19
20	20
21	21
22	22
23	23
24	24
25	25
26	26
27	27
28	28
29	29
30	30
31	31
32	32
33	33
34	34
35	35
36	36
37	37
38	38
39	39
40	40
41	41
42	42
43	43
44	44
45	45
46	46
47	47
48	48
49	49
50	50
\.


--
-- TOC entry 3575 (class 0 OID 0)
-- Dependencies: 217
-- Name: carta_idcarta_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.carta_idcarta_seq', 11, true);


--
-- TOC entry 3576 (class 0 OID 0)
-- Dependencies: 222
-- Name: chef_idchef_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.chef_idchef_seq', 50, true);


--
-- TOC entry 3577 (class 0 OID 0)
-- Dependencies: 224
-- Name: corso_idcorso_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.corso_idcorso_seq', 50, true);


--
-- TOC entry 3578 (class 0 OID 0)
-- Dependencies: 239
-- Name: ingrediente_idingrediente_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.ingrediente_idingrediente_seq', 85, true);


--
-- TOC entry 3579 (class 0 OID 0)
-- Dependencies: 219
-- Name: partecipante_idpartecipante_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.partecipante_idpartecipante_seq', 50, true);


--
-- TOC entry 3580 (class 0 OID 0)
-- Dependencies: 236
-- Name: ricetta_idricetta_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.ricetta_idricetta_seq', 85, true);


--
-- TOC entry 3581 (class 0 OID 0)
-- Dependencies: 230
-- Name: sessione_presenza_idsessionepresenza_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.sessione_presenza_idsessionepresenza_seq', 110, true);


--
-- TOC entry 3582 (class 0 OID 0)
-- Dependencies: 232
-- Name: sessione_telematica_idsessionetelematica_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.sessione_telematica_idsessionetelematica_seq', 2, true);


--
-- TOC entry 3583 (class 0 OID 0)
-- Dependencies: 227
-- Name: tipodicucina_idtipocucina_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.tipodicucina_idtipocucina_seq', 72, true);


--
-- TOC entry 3333 (class 2606 OID 16924)
-- Name: adesione_sessionepresenza adesione_sessionepresenza_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.adesione_sessionepresenza
    ADD CONSTRAINT adesione_sessionepresenza_pkey PRIMARY KEY (idsessionepresenza, idpartecipante);


--
-- TOC entry 3303 (class 2606 OID 16768)
-- Name: carta carta_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.carta
    ADD CONSTRAINT carta_pkey PRIMARY KEY (idcarta);


--
-- TOC entry 3313 (class 2606 OID 16804)
-- Name: chef chef_email_key; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.chef
    ADD CONSTRAINT chef_email_key UNIQUE (email);


--
-- TOC entry 3315 (class 2606 OID 16802)
-- Name: chef chef_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.chef
    ADD CONSTRAINT chef_pkey PRIMARY KEY (idchef);


--
-- TOC entry 3317 (class 2606 OID 16814)
-- Name: corso corso_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.corso
    ADD CONSTRAINT corso_pkey PRIMARY KEY (idcorso);


--
-- TOC entry 3339 (class 2606 OID 16961)
-- Name: ingrediente ingrediente_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.ingrediente
    ADD CONSTRAINT ingrediente_pkey PRIMARY KEY (idingrediente);


--
-- TOC entry 3307 (class 2606 OID 16778)
-- Name: partecipante partecipante_email_key; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante
    ADD CONSTRAINT partecipante_email_key UNIQUE (email);


--
-- TOC entry 3309 (class 2606 OID 16776)
-- Name: partecipante partecipante_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante
    ADD CONSTRAINT partecipante_pkey PRIMARY KEY (idpartecipante);


--
-- TOC entry 3331 (class 2606 OID 16909)
-- Name: partecipante_sessionetelematica partecipante_sessionetelematica_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante_sessionetelematica
    ADD CONSTRAINT partecipante_sessionetelematica_pkey PRIMARY KEY (idpartecipante, idsessionetelematica);


--
-- TOC entry 3311 (class 2606 OID 16783)
-- Name: possiede possiede_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.possiede
    ADD CONSTRAINT possiede_pkey PRIMARY KEY (idpartecipante, idcarta);


--
-- TOC entry 3341 (class 2606 OID 16968)
-- Name: preparazioneingrediente preparazioneingrediente_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.preparazioneingrediente
    ADD CONSTRAINT preparazioneingrediente_pkey PRIMARY KEY (idricetta, idingrediente);


--
-- TOC entry 3335 (class 2606 OID 16940)
-- Name: ricetta ricetta_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.ricetta
    ADD CONSTRAINT ricetta_pkey PRIMARY KEY (idricetta);


--
-- TOC entry 3319 (class 2606 OID 16835)
-- Name: richiestapagamento richiestapagamento_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.richiestapagamento
    ADD CONSTRAINT richiestapagamento_pkey PRIMARY KEY (datarichiesta, idcorso, idpartecipante);


--
-- TOC entry 3327 (class 2606 OID 16876)
-- Name: sessione_presenza sessione_presenza_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza
    ADD CONSTRAINT sessione_presenza_pkey PRIMARY KEY (idsessionepresenza);


--
-- TOC entry 3337 (class 2606 OID 16945)
-- Name: sessione_presenza_ricetta sessione_presenza_ricetta_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza_ricetta
    ADD CONSTRAINT sessione_presenza_ricetta_pkey PRIMARY KEY (idricetta, idsessionepresenza);


--
-- TOC entry 3329 (class 2606 OID 16894)
-- Name: sessione_telematica sessione_telematica_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_telematica
    ADD CONSTRAINT sessione_telematica_pkey PRIMARY KEY (idsessionetelematica);


--
-- TOC entry 3325 (class 2606 OID 16858)
-- Name: tipodicucina_corso tipodicucina_corso_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina_corso
    ADD CONSTRAINT tipodicucina_corso_pkey PRIMARY KEY (idtipocucina, idcorso);


--
-- TOC entry 3321 (class 2606 OID 16853)
-- Name: tipodicucina tipodicucina_nome_key; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina
    ADD CONSTRAINT tipodicucina_nome_key UNIQUE (nome);


--
-- TOC entry 3323 (class 2606 OID 16851)
-- Name: tipodicucina tipodicucina_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina
    ADD CONSTRAINT tipodicucina_pkey PRIMARY KEY (idtipocucina);


--
-- TOC entry 3305 (class 2606 OID 17002)
-- Name: carta uq_carta_details; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.carta
    ADD CONSTRAINT uq_carta_details UNIQUE (ultimequattrocifre, datascadenza, circuito, intestatario);


--
-- TOC entry 3378 (class 2620 OID 17082)
-- Name: adesione_sessionepresenza trg_check_partecipazione_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_check_partecipazione_corso BEFORE INSERT ON public.adesione_sessionepresenza FOR EACH ROW EXECUTE FUNCTION public.check_partecipazione_corso();


--
-- TOC entry 3375 (class 2620 OID 17535)
-- Name: sessione_presenza trg_check_sessione_presenza; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_check_sessione_presenza BEFORE INSERT OR UPDATE ON public.sessione_presenza FOR EACH ROW EXECUTE FUNCTION public.check_orario_e_durata();


--
-- TOC entry 3371 (class 2620 OID 17032)
-- Name: richiestapagamento trg_controllo_max_partecipanti_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_controllo_max_partecipanti_corso BEFORE INSERT OR UPDATE ON public.richiestapagamento FOR EACH ROW EXECUTE FUNCTION public.verifica_superamento_max_partecipanti_corso();


--
-- TOC entry 3361 (class 2620 OID 17047)
-- Name: carta trg_impedisci_dettagli_carta_duplicati; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_impedisci_dettagli_carta_duplicati BEFORE INSERT OR UPDATE ON public.carta FOR EACH ROW EXECUTE FUNCTION public.impedisci_carta_duplicata_per_partecipante();


--
-- TOC entry 3369 (class 2620 OID 17030)
-- Name: corso trg_impedisci_eliminazione_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_impedisci_eliminazione_corso BEFORE DELETE ON public.corso FOR EACH ROW EXECUTE FUNCTION public.impedisci_eliminazione_corso_se_iscritto();


--
-- TOC entry 3372 (class 2620 OID 17039)
-- Name: richiestapagamento trg_impedisci_pagamento_tardivo; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_impedisci_pagamento_tardivo BEFORE INSERT OR UPDATE ON public.richiestapagamento FOR EACH ROW EXECUTE FUNCTION public.verifica_data_pagamento_prima_inizio_corso();


--
-- TOC entry 3374 (class 2620 OID 17045)
-- Name: tipodicucina_corso trg_max_tipi_cucina_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_max_tipi_cucina_corso BEFORE INSERT ON public.tipodicucina_corso FOR EACH ROW EXECUTE FUNCTION public.verifica_max_tipi_cucina_per_corso();


--
-- TOC entry 3365 (class 2620 OID 17041)
-- Name: chef trg_valida_anni_esperienza_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_anni_esperienza_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.verifica_anni_esperienza_chef();


--
-- TOC entry 3370 (class 2620 OID 17043)
-- Name: corso trg_valida_date_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_date_corso BEFORE INSERT OR UPDATE ON public.corso FOR EACH ROW EXECUTE FUNCTION public.verifica_intervallo_date_corso();


--
-- TOC entry 3366 (class 2620 OID 17034)
-- Name: chef trg_valida_eta_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_eta_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.verifica_intervallo_eta();


--
-- TOC entry 3362 (class 2620 OID 17035)
-- Name: partecipante trg_valida_eta_partecipante; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_eta_partecipante BEFORE INSERT OR UPDATE ON public.partecipante FOR EACH ROW EXECUTE FUNCTION public.verifica_intervallo_eta();


--
-- TOC entry 3373 (class 2620 OID 17037)
-- Name: richiestapagamento trg_valida_importo_pagamento; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_importo_pagamento BEFORE INSERT OR UPDATE ON public.richiestapagamento FOR EACH ROW EXECUTE FUNCTION public.verifica_importo_pagamento_corrisponde_prezzo_corso();


--
-- TOC entry 3367 (class 2620 OID 17074)
-- Name: chef trg_validate_email_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_validate_email_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.validate_email_full();


--
-- TOC entry 3363 (class 2620 OID 17073)
-- Name: partecipante trg_validate_email_partecipante; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_validate_email_partecipante BEFORE INSERT OR UPDATE ON public.partecipante FOR EACH ROW EXECUTE FUNCTION public.validate_email_full();


--
-- TOC entry 3368 (class 2620 OID 17052)
-- Name: chef trg_verifica_email_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_email_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.verifica_unicita_email();


--
-- TOC entry 3364 (class 2620 OID 17051)
-- Name: partecipante trg_verifica_email_partecipante; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_email_partecipante BEFORE INSERT OR UPDATE ON public.partecipante FOR EACH ROW EXECUTE FUNCTION public.verifica_unicita_email();


--
-- TOC entry 3376 (class 2620 OID 17027)
-- Name: sessione_presenza trg_verifica_sessione_presenza_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_sessione_presenza_chef BEFORE INSERT OR UPDATE ON public.sessione_presenza FOR EACH ROW EXECUTE FUNCTION public.verifica_sessioni_contemporanee_chef();


--
-- TOC entry 3377 (class 2620 OID 17028)
-- Name: sessione_telematica trg_verifica_sessione_telematica_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_sessione_telematica_chef BEFORE INSERT OR UPDATE ON public.sessione_telematica FOR EACH ROW EXECUTE FUNCTION public.verifica_sessioni_contemporanee_chef();


--
-- TOC entry 3379 (class 2620 OID 17049)
-- Name: ricetta trigger_elimina_ingredienti_associati; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trigger_elimina_ingredienti_associati BEFORE DELETE ON public.ricetta FOR EACH ROW EXECUTE FUNCTION public.elimina_ingredienti_della_ricetta();


--
-- TOC entry 3355 (class 2606 OID 16930)
-- Name: adesione_sessionepresenza adesione_sessionepresenza_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.adesione_sessionepresenza
    ADD CONSTRAINT adesione_sessionepresenza_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3356 (class 2606 OID 16925)
-- Name: adesione_sessionepresenza adesione_sessionepresenza_idsessionepresenza_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.adesione_sessionepresenza
    ADD CONSTRAINT adesione_sessionepresenza_idsessionepresenza_fkey FOREIGN KEY (idsessionepresenza) REFERENCES public.sessione_presenza(idsessionepresenza);


--
-- TOC entry 3344 (class 2606 OID 17075)
-- Name: corso fk_corso_chef; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.corso
    ADD CONSTRAINT fk_corso_chef FOREIGN KEY (idchef) REFERENCES public.chef(idchef);


--
-- TOC entry 3342 (class 2606 OID 17066)
-- Name: possiede fk_possiede_idcarta_carta_cascade; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.possiede
    ADD CONSTRAINT fk_possiede_idcarta_carta_cascade FOREIGN KEY (idcarta) REFERENCES public.carta(idcarta) ON DELETE CASCADE;


--
-- TOC entry 3353 (class 2606 OID 16910)
-- Name: partecipante_sessionetelematica partecipante_sessionetelematica_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante_sessionetelematica
    ADD CONSTRAINT partecipante_sessionetelematica_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3354 (class 2606 OID 16915)
-- Name: partecipante_sessionetelematica partecipante_sessionetelematica_idsessionetelematica_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante_sessionetelematica
    ADD CONSTRAINT partecipante_sessionetelematica_idsessionetelematica_fkey FOREIGN KEY (idsessionetelematica) REFERENCES public.sessione_telematica(idsessionetelematica);


--
-- TOC entry 3343 (class 2606 OID 16784)
-- Name: possiede possiede_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.possiede
    ADD CONSTRAINT possiede_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3359 (class 2606 OID 16974)
-- Name: preparazioneingrediente preparazioneingrediente_idingrediente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.preparazioneingrediente
    ADD CONSTRAINT preparazioneingrediente_idingrediente_fkey FOREIGN KEY (idingrediente) REFERENCES public.ingrediente(idingrediente);


--
-- TOC entry 3360 (class 2606 OID 16969)
-- Name: preparazioneingrediente preparazioneingrediente_idricetta_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.preparazioneingrediente
    ADD CONSTRAINT preparazioneingrediente_idricetta_fkey FOREIGN KEY (idricetta) REFERENCES public.ricetta(idricetta);


--
-- TOC entry 3345 (class 2606 OID 16836)
-- Name: richiestapagamento richiestapagamento_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.richiestapagamento
    ADD CONSTRAINT richiestapagamento_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3346 (class 2606 OID 16841)
-- Name: richiestapagamento richiestapagamento_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.richiestapagamento
    ADD CONSTRAINT richiestapagamento_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3349 (class 2606 OID 16882)
-- Name: sessione_presenza sessione_presenza_idchef_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza
    ADD CONSTRAINT sessione_presenza_idchef_fkey FOREIGN KEY (idchef) REFERENCES public.chef(idchef);


--
-- TOC entry 3350 (class 2606 OID 16877)
-- Name: sessione_presenza sessione_presenza_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza
    ADD CONSTRAINT sessione_presenza_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3357 (class 2606 OID 16946)
-- Name: sessione_presenza_ricetta sessione_presenza_ricetta_idricetta_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza_ricetta
    ADD CONSTRAINT sessione_presenza_ricetta_idricetta_fkey FOREIGN KEY (idricetta) REFERENCES public.ricetta(idricetta);


--
-- TOC entry 3358 (class 2606 OID 16951)
-- Name: sessione_presenza_ricetta sessione_presenza_ricetta_idsessionepresenza_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza_ricetta
    ADD CONSTRAINT sessione_presenza_ricetta_idsessionepresenza_fkey FOREIGN KEY (idsessionepresenza) REFERENCES public.sessione_presenza(idsessionepresenza);


--
-- TOC entry 3351 (class 2606 OID 16900)
-- Name: sessione_telematica sessione_telematica_idchef_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_telematica
    ADD CONSTRAINT sessione_telematica_idchef_fkey FOREIGN KEY (idchef) REFERENCES public.chef(idchef);


--
-- TOC entry 3352 (class 2606 OID 16895)
-- Name: sessione_telematica sessione_telematica_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_telematica
    ADD CONSTRAINT sessione_telematica_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3347 (class 2606 OID 16864)
-- Name: tipodicucina_corso tipodicucina_corso_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina_corso
    ADD CONSTRAINT tipodicucina_corso_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3348 (class 2606 OID 16859)
-- Name: tipodicucina_corso tipodicucina_corso_idtipocucina_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina_corso
    ADD CONSTRAINT tipodicucina_corso_idtipocucina_fkey FOREIGN KEY (idtipocucina) REFERENCES public.tipodicucina(idtipocucina);


--
-- TOC entry 3557 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE adesione_sessionepresenza; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.adesione_sessionepresenza TO "Mario";


--
-- TOC entry 3558 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE carta; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.carta TO "Mario";


--
-- TOC entry 3559 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE chef; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.chef TO "Mario";


--
-- TOC entry 3560 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE corso; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.corso TO "Mario";


--
-- TOC entry 3561 (class 0 OID 0)
-- Dependencies: 240
-- Name: TABLE ingrediente; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.ingrediente TO "Mario";


--
-- TOC entry 3562 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE partecipante; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.partecipante TO "Mario";


--
-- TOC entry 3563 (class 0 OID 0)
-- Dependencies: 234
-- Name: TABLE partecipante_sessionetelematica; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.partecipante_sessionetelematica TO "Mario";


--
-- TOC entry 3564 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE possiede; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.possiede TO "Mario";


--
-- TOC entry 3565 (class 0 OID 0)
-- Dependencies: 241
-- Name: TABLE preparazioneingrediente; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.preparazioneingrediente TO "Mario";


--
-- TOC entry 3566 (class 0 OID 0)
-- Dependencies: 238
-- Name: TABLE sessione_presenza_ricetta; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.sessione_presenza_ricetta TO "Mario";


--
-- TOC entry 3567 (class 0 OID 0)
-- Dependencies: 242
-- Name: TABLE quantitapersessione; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.quantitapersessione TO "Mario";


--
-- TOC entry 3568 (class 0 OID 0)
-- Dependencies: 237
-- Name: TABLE ricetta; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.ricetta TO "Mario";


--
-- TOC entry 3569 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE richiestapagamento; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.richiestapagamento TO "Mario";


--
-- TOC entry 3570 (class 0 OID 0)
-- Dependencies: 231
-- Name: TABLE sessione_presenza; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.sessione_presenza TO "Mario";


--
-- TOC entry 3571 (class 0 OID 0)
-- Dependencies: 233
-- Name: TABLE sessione_telematica; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.sessione_telematica TO "Mario";


--
-- TOC entry 3572 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE tipodicucina; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.tipodicucina TO "Mario";


--
-- TOC entry 3573 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE tipodicucina_corso; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.tipodicucina_corso TO "Mario";


--
-- TOC entry 3574 (class 0 OID 0)
-- Dependencies: 243
-- Name: TABLE vista_statistiche_mensili_chef; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.vista_statistiche_mensili_chef TO "Mario";


-- Completed on 2025-07-01 13:08:18

--
-- PostgreSQL database dump complete
--

