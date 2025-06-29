--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Ubuntu 17.5-1.pgdg22.04+1)
-- Dumped by pg_dump version 17.4

-- Started on 2025-06-29 21:35:52

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
-- TOC entry 885 (class 1247 OID 16707)
-- Name: circuito; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.circuito AS ENUM (
    'Visa',
    'Mastercard'
);


ALTER TYPE public.circuito OWNER TO name;

--
-- TOC entry 891 (class 1247 OID 16720)
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
-- TOC entry 894 (class 1247 OID 16738)
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
-- TOC entry 888 (class 1247 OID 16712)
-- Name: statopagamento; Type: TYPE; Schema: public; Owner: name
--

CREATE TYPE public.statopagamento AS ENUM (
    'In attesa',
    'Pagato',
    'Fallito'
);


ALTER TYPE public.statopagamento OWNER TO name;

--
-- TOC entry 897 (class 1247 OID 16754)
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
-- TOC entry 265 (class 1255 OID 17048)
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
-- TOC entry 264 (class 1255 OID 17046)
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
-- TOC entry 257 (class 1255 OID 17029)
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
-- TOC entry 244 (class 1255 OID 17072)
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
-- TOC entry 262 (class 1255 OID 17040)
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
-- TOC entry 261 (class 1255 OID 17038)
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
-- TOC entry 260 (class 1255 OID 17036)
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
-- TOC entry 267 (class 1255 OID 17042)
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
-- TOC entry 259 (class 1255 OID 17033)
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
-- TOC entry 263 (class 1255 OID 17044)
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
-- TOC entry 256 (class 1255 OID 17026)
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
-- TOC entry 258 (class 1255 OID 17031)
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
-- TOC entry 266 (class 1255 OID 17050)
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
    quantitatotale numeric(10,2),
    quanititaunitaria numeric(10,2) NOT NULL,
    CONSTRAINT preparazioneingrediente_quanititaunitaria_check CHECK ((quanititaunitaria >= (0)::numeric)),
    CONSTRAINT preparazioneingrediente_quantitatotale_check CHECK ((quantitatotale >= (0)::numeric))
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
-- TOC entry 243 (class 1259 OID 17061)
-- Name: vista_statistiche_mensili_chef; Type: VIEW; Schema: public; Owner: name
--

CREATE VIEW public.vista_statistiche_mensili_chef AS
 SELECT ch.idchef,
    ch.nome,
    ch.cognome,
    count(spr.idricetta) AS totale_ricette_mese,
    COALESCE(max(spr_count.ricette_per_sessione), (0)::bigint) AS max_ricette_in_sessione,
    COALESCE(min(spr_count.ricette_per_sessione), (0)::bigint) AS min_ricette_in_sessione,
    COALESCE(avg(spr_count.ricette_per_sessione), (0)::numeric) AS media_ricette_per_sessione,
    count(DISTINCT c.idcorso) AS numero_corsi,
    count(DISTINCT sp.idsessionepresenza) AS numero_sessioni_presenza,
    count(DISTINCT st.idsessionetelematica) AS numero_sessioni_telematiche,
    COALESCE(sum(rp.importopagato), (0)::numeric) AS guadagno_totale
   FROM ((((((public.chef ch
     LEFT JOIN public.corso c ON ((ch.idchef = c.idchef)))
     LEFT JOIN public.sessione_presenza sp ON (((c.idcorso = sp.idcorso) AND (sp.idchef = ch.idchef))))
     LEFT JOIN public.sessione_presenza_ricetta spr ON ((spr.idsessionepresenza = sp.idsessionepresenza)))
     LEFT JOIN ( SELECT spr2.idsessionepresenza,
            count(*) AS ricette_per_sessione
           FROM (public.sessione_presenza_ricetta spr2
             JOIN public.sessione_presenza sp2 ON ((spr2.idsessionepresenza = sp2.idsessionepresenza)))
          WHERE ((date_part('month'::text, sp2.data) = date_part('month'::text, CURRENT_DATE)) AND (date_part('year'::text, sp2.data) = date_part('year'::text, CURRENT_DATE)))
          GROUP BY spr2.idsessionepresenza) spr_count ON ((spr_count.idsessionepresenza = sp.idsessionepresenza)))
     LEFT JOIN public.sessione_telematica st ON (((c.idcorso = st.idcorso) AND (st.idchef = ch.idchef) AND (date_part('month'::text, st.data) = date_part('month'::text, CURRENT_DATE)) AND (date_part('year'::text, st.data) = date_part('year'::text, CURRENT_DATE)))))
     LEFT JOIN public.richiestapagamento rp ON (((rp.idcorso = c.idcorso) AND (date_part('month'::text, rp.datarichiesta) = date_part('month'::text, CURRENT_DATE)) AND (date_part('year'::text, rp.datarichiesta) = date_part('year'::text, CURRENT_DATE)))))
  WHERE ((sp.data IS NULL) OR ((date_part('month'::text, sp.data) = date_part('month'::text, CURRENT_DATE)) AND (date_part('year'::text, sp.data) = date_part('year'::text, CURRENT_DATE))))
  GROUP BY ch.idchef, ch.nome, ch.cognome;


ALTER VIEW public.vista_statistiche_mensili_chef OWNER TO name;

--
-- TOC entry 3542 (class 0 OID 16920)
-- Dependencies: 235
-- Data for Name: adesione_sessionepresenza; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.adesione_sessionepresenza (conferma, idsessionepresenza, idpartecipante) FROM stdin;
t	1	3
t	4	12
t	5	13
t	12	26
t	14	27
t	15	32
t	17	32
t	19	32
\.


--
-- TOC entry 3525 (class 0 OID 16764)
-- Dependencies: 218
-- Data for Name: carta; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.carta (idcarta, intestatario, datascadenza, ultimequattrocifre, circuito) FROM stdin;
2	Giulia Bianchi	2027-06-30	5678	Mastercard
5	Anna Bianchi	2029-01-20	9012	Visa
6	Marco Gialli	2027-11-01	3456	Mastercard
10	Sofia Neri	2030-05-25	7890	Visa
11	Giovanni Blu	2028-09-15	2345	Mastercard
12	Martina Viola	2027-03-01	6789	Visa
19	Filippo Bruno	2030-11-05	7788	Visa
20	Greta Azzurra	2029-07-20	9900	Mastercard
31	Marco Rossi Nuovo	2032-01-01	1010	Visa
32	Anna Bianchi Nuova	2033-03-03	2020	Mastercard
\.


--
-- TOC entry 3530 (class 0 OID 16795)
-- Dependencies: 223
-- Data for Name: chef; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.chef (idchef, nome, cognome, email, password, datadinascita, descrizione, propic, annidiesperienza) FROM stdin;
3	Anna	Neri	anna.neri@example.com	passwordAnna	1985-01-20	Chef specializzata in cucina italiana	PropicChef/anna_neri.png	20
6	Piero	Conti	piero.conti@example.com	passwordPiero	1978-11-05	Maestro della pasticceria	PropicChef/piero_conti.jpg	25
7	Elena	Ferrari	elena.ferrari@example.com	passwordElena	1990-04-01	Esperti di cucina asiatica	PropicChef/elena_ferrari.png	12
11	Giuseppe	Verdi	giuseppe.verdi@example.com	passGiuseppe	1980-02-10	Chef innovativo e creativo	PropicChef/giuseppe_verdi.jpg	18
12	Laura	Bassi	laura.bassi@example.com	passLaura	1975-09-03	Specialista in cucina vegana	PropicChef/laura_bassi.png	22
13	Francesco	Bianchi	francesco.bianchi@example.com	passFrancesco	1992-12-12	Giovane promessa della cucina molecolare	PropicChef/francesco_bianchi.jpg	8
20	Carla	Moretti	carla.moretti@example.com	passCarla	1987-04-22	Specialista in pasticceria moderna	PropicChef/carla_moretti.jpg	13
21	Fabio	Russo	fabio.russo@example.com	passFabio	1982-08-08	Esperto di cucina fusion	PropicChef/fabio_russo.png	16
26	Elena	Ricci Nuova	elena.riccinuova@example.com	passElenaR	1978-11-11	Chef specializzata in cucina mediterranea	PropicChef/elena_ricci_nuova.jpg	20
27	Luca	Mancini Nuovo	luca.mancininuovo@example.com	passLucaM	1985-03-25	Esperto di cucina internazionale	PropicChef/luca_mancini_nuovo.png	10
\.


--
-- TOC entry 3532 (class 0 OID 16806)
-- Dependencies: 225
-- Data for Name: corso; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.corso (idcorso, nome, descrizione, datainizio, datafine, frequenzadellesessioni, propic, maxpersone, prezzo, idchef) FROM stdin;
3	Corso Base di Pasta Fresca	Impara a fare la pasta fresca da zero	2025-07-10	2025-08-10	Settimanale	corsi/pasta_fresca.jpg	15	120.00	3
6	Corso di Pasticceria Francese	Dolci classici della tradizione francese	2025-09-01	2025-10-15	Ogni due giorni	corsi/pasticceria_francese.jpg	10	180.00	6
7	Introduzione alla Cucina Giapponese	Base di sushi e ramen	2025-08-05	2025-08-25	Settimanale	corsi/cucina_giapponese.webp	12	150.00	7
12	Cucina Vegana Avanzata	Tecniche e ricette innovative vegane	2025-09-20	2025-10-20	Settimanale	corsi/vegana_avanzata.jpg	8	200.00	12
13	Laboratorio di Pane Fatto in Casa	Dalla lievitazione alla cottura	2025-10-05	2025-10-12	Ogni due giorni	corsi/pane_casa.webp	10	80.00	11
14	Corso di Cucina Molecolare	Introduzione alle basi della gastronomia molecolare	2025-11-01	2025-11-30	Mensile	corsi/molecolare.png	5	300.00	13
15	Cocktail Art e Mixology	Crea i tuoi cocktail d'autore	2026-01-10	2026-01-20	Giornaliera	corsi/mixology.jpg	12	150.00	11
26	Corso di Cioccolateria Artigianale	Dalla fava al cioccolatino	2026-02-01	2026-03-01	Settimanale	corsi/cioccolateria.jpg	8	190.00	20
27	Cucina Fusion Asiatica-Mediterranea	Esplorazione di nuovi sapori	2026-03-10	2026-04-10	Mensile	corsi/fusion.webp	10	220.00	21
28	Masterclass di Panificazione Avanzata	Segreti dei grandi lievitati	2026-04-15	2026-05-15	Bisettimanale	corsi/panificazione_avanzata.jpg	7	280.00	11
37	Corso di Pasta Fresca Avanzata	Tecniche per pasta ripiena e formati speciali	2025-09-01	2025-09-28	Settimanale	corsi/pasta_fresca_avanzata.jpg	10	120.00	26
38	Cucina Vegetariana Creativa	Ricette innovative senza carne e pesce	2025-10-06	2025-10-27	Bisettimanale	corsi/vegetariana_creativa.webp	12	150.00	27
39	Dolci al Cucchiaio e Mignon	L'arte della piccola pasticceria	2025-11-01	2025-11-30	Ogni due giorni	corsi/dolci_mignon.png	8	180.00	20
40	Corso di Cucina Messicana Autentica	Sapori e tradizioni del Messico	2025-12-01	2025-12-31	Settimanale	corsi/messicana.jpg	15	140.00	7
\.


--
-- TOC entry 3547 (class 0 OID 16957)
-- Dependencies: 240
-- Data for Name: ingrediente; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.ingrediente (idingrediente, nome, unitadimisura) FROM stdin;
1	Farina 00	Kilogrammi
2	Uova	Grammi
3	Burro	Grammi
4	Zucchero	Kilogrammi
5	Riso per Sushi	Kilogrammi
6	Salmone Fresco	Grammi
12	Lievito di Birra	Grammi
13	Acqua	Litro
14	Cacao Amaro	Grammi
15	Alginato di Sodio	Grammi
16	Cloruro di Calcio	Grammi
17	Olio d'Oliva	Litro
25	Burro di Cacao	Grammi
26	Zucchero a Velo	Kilogrammi
27	Gamberi	Grammi
28	Curry Verde	Grammi
29	Semola Rimacinata	Kilogrammi
30	Zucca	Kilogrammi
31	Ceci	Grammi
32	Maiale	Grammi
33	Ananas	Kilogrammi
\.


--
-- TOC entry 3527 (class 0 OID 16770)
-- Dependencies: 220
-- Data for Name: partecipante; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.partecipante (idpartecipante, nome, cognome, email, password, datadinascita, propic) FROM stdin;
6	Chiara	Russo	chiara.russo@example.com	passwordChiara	1995-03-22	PropicUtente/chiara_russo.webp
7	Marco	Gialli	marco.gialli@example.com	passwordMarco	1998-08-10	PropicUtente/marco_gialli.jpeg
12	Sofia	Neri	sofia.neri@example.com	passSofia	2001-07-01	PropicUtente/sofia_neri.jpg
13	Giovanni	Blu	giovanni.blu@example.com	passGiovanni	1999-11-20	PropicUtente/giovanni_blu.png
14	Martina	Viola	martina.viola@example.com	passMartina	1990-01-01	PropicUtente/martina_viola.jpeg
15	Paolo	Rossi	paolo.rossi@example.com	passPaolo	1988-06-01	PropicUtente/paolo_rossi.jpg
26	Filippo	Bruno	filippo.bruno@example.com	passFilippo	1990-06-01	PropicUtente/filippo_bruno.jpg
27	Greta	Azzurra	greta.azzurra@example.com	passGreta	1994-02-14	PropicUtente/greta_azzurra.png
3	Luca	Verdi	luca.verdi@example.com	passLuca	2000-05-15	immagini/PropicUtente/Luca_Verdi.jpg
32	Marco	Rossi Nuovo	marco.rossinuovo@example.com	passMarcoN	1995-05-10	PropicUtente/marco_rossi_nuovo.jpg
33	Anna	Bianchi Nuova	anna.bianchinuova@example.com	passAnnaN	1998-08-22	PropicUtente/anna_bianchi_nuova.png
\.


--
-- TOC entry 3541 (class 0 OID 16905)
-- Dependencies: 234
-- Data for Name: partecipante_sessionetelematica; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.partecipante_sessionetelematica (idpartecipante, idsessionetelematica) FROM stdin;
6	1
7	1
14	5
26	12
27	11
33	13
33	14
33	20
\.


--
-- TOC entry 3528 (class 0 OID 16779)
-- Dependencies: 221
-- Data for Name: possiede; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.possiede (idpartecipante, idcarta) FROM stdin;
6	5
7	6
12	10
13	11
14	12
26	19
27	20
32	31
33	32
\.


--
-- TOC entry 3548 (class 0 OID 16962)
-- Dependencies: 241
-- Data for Name: preparazioneingrediente; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.preparazioneingrediente (idricetta, idingrediente, quantitatotale, quanititaunitaria) FROM stdin;
1	1	0.50	0.05
1	2	300.00	30.00
2	3	2.00	0.20
2	4	1.00	0.10
3	5	1.50	0.15
3	6	0.50	0.05
8	1	1.00	0.10
8	12	0.02	0.00
9	14	0.20	0.02
10	17	0.50	0.05
10	15	0.01	0.00
10	16	0.01	0.00
17	25	0.30	0.03
18	27	0.40	0.04
19	29	1.00	0.10
20	30	0.50	0.05
21	31	0.30	0.03
22	32	0.60	0.06
22	33	0.20	0.02
\.


--
-- TOC entry 3544 (class 0 OID 16936)
-- Dependencies: 237
-- Data for Name: ricetta; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.ricetta (idricetta, nome) FROM stdin;
1	Tagliatelle all'Uovo
2	Millefoglie
3	Sushi Nigiri
8	Pane Casereccio
9	Mousse al Cioccolato Vegana
10	Sferificazione di Olio d'Oliva
11	Mojito Scomposto
17	Cioccolatini al Caramello Salato
18	Ravioli di Gamberi e Curry Verde
19	Pane di Semola Rimacinata
20	Tortellini di Zucca
21	Curry di Verdure e Ceci
22	Tacos al Pastor
\.


--
-- TOC entry 3533 (class 0 OID 16830)
-- Dependencies: 226
-- Data for Name: richiestapagamento; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.richiestapagamento (datarichiesta, statopagamento, importopagato, idcorso, idpartecipante) FROM stdin;
2025-07-01 10:00:00	Pagato	120.00	3	3
2025-08-01 12:00:00	Pagato	150.00	7	6
2025-08-15 09:30:00	Pagato	180.00	6	7
2025-09-10 14:00:00	Pagato	200.00	12	12
2025-09-25 10:00:00	Pagato	80.00	13	13
2025-10-15 11:00:00	Pagato	300.00	14	14
2026-01-20 10:00:00	Pagato	190.00	26	26
2026-03-01 11:30:00	Pagato	220.00	27	27
2025-06-28 00:00:00	Pagato	200.00	12	3
2025-06-28 00:00:00	Pagato	80.00	13	3
2025-06-29 00:00:00	Pagato	150.00	7	3
2025-08-25 10:00:00	Pagato	120.00	37	32
2025-09-30 14:00:00	Pagato	150.00	38	33
2025-10-25 09:00:00	Pagato	180.00	39	32
2025-11-20 16:00:00	Pagato	140.00	40	33
\.


--
-- TOC entry 3538 (class 0 OID 16870)
-- Dependencies: 231
-- Data for Name: sessione_presenza; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.sessione_presenza (idsessionepresenza, giorno, data, orario, durata, citta, via, cap, descrizione, idcorso, idchef) FROM stdin;
1	Giovedì	2025-07-17	18:00:00	02:00:00	Roma	Via del Corso 1	00187	Prima sessione di impasto	3	3
4	Martedì	2025-10-07	09:00:00	03:00:00	Bologna	Via Indipendenza 50	40121	Tecniche di impasto e lievitazione	13	11
5	Lunedì	2025-09-22	17:30:00	02:00:00	Firenze	Piazza della Signoria 10	50122	Ricette base vegane	12	12
12	Lunedì	2026-02-08	15:00:00	02:00:00	Torino	Via Roma 5	10121	Tecniche di temperaggio del cioccolato	26	20
13	Lunedì	2026-02-15	15:00:00	02:00:00	Torino	Via Roma 5	10121	Creazione praline e tavolette	26	20
14	Mercoledì	2026-04-22	09:00:00	04:00:00	Napoli	Via Toledo 10	80134	Impasti ad alta idratazione	28	11
15	Lunedì	2025-09-01	18:00:00	02:00:00	Bologna	Via del Sale 10	40121	Pasta fresca: impasti base	37	26
16	Lunedì	2025-09-08	18:00:00	02:00:00	Bologna	Via del Sale 10	40121	Pasta fresca: formati lunghi	37	26
17	Lunedì	2025-09-15	18:00:00	02:00:00	Bologna	Via del Sale 10	40121	Pasta fresca: ravioli e tortellini	37	26
18	Lunedì	2025-09-22	18:00:00	02:00:00	Bologna	Via del Sale 10	40121	Pasta fresca: gnocchi e pici	37	26
19	Sabato	2025-11-01	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Mousse e bavaresi	39	20
20	Lunedì	2025-11-03	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Tiramisù e zuppe inglesi	39	20
21	Mercoledì	2025-11-05	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Panna cotta e creme caramel	39	20
22	Venerdì	2025-11-07	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Biscotti mignon: frollini e cantucci	39	20
23	Domenica	2025-11-09	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Macarons e meringhe	39	20
24	Martedì	2025-11-11	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Eclairs e bignè	39	20
25	Giovedì	2025-11-13	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Monoporzioni moderne	39	20
26	Sabato	2025-11-15	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Gelati e sorbetti al cucchiaio	39	20
27	Lunedì	2025-11-17	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Mini cheesecake e tartellette	39	20
28	Mercoledì	2025-11-19	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Dolci senza cottura	39	20
29	Venerdì	2025-11-21	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Decorazioni per dolci al cucchiaio	39	20
30	Domenica	2025-11-23	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Finger food dolci	39	20
31	Martedì	2025-11-25	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Tecniche di glassatura	39	20
32	Giovedì	2025-11-27	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Cioccolatini e tartufi	39	20
33	Sabato	2025-11-29	10:00:00	02:30:00	Milano	Corso Garibaldi 20	20121	Presentazione finale e degustazione	39	20
\.


--
-- TOC entry 3545 (class 0 OID 16941)
-- Dependencies: 238
-- Data for Name: sessione_presenza_ricetta; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.sessione_presenza_ricetta (idricetta, idsessionepresenza) FROM stdin;
1	1
8	4
9	5
17	13
20	17
22	33
\.


--
-- TOC entry 3540 (class 0 OID 16888)
-- Dependencies: 233
-- Data for Name: sessione_telematica; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.sessione_telematica (idsessionetelematica, applicazione, codicechiamata, data, orario, durata, giorno, descrizione, idcorso, idchef) FROM stdin;
1	Zoom	GIA-2025-08-12-ABC	2025-08-12	19:00:00	01:30:00	Martedì	Sessione introduttiva sul riso	7	7
4	Google Meet	MIX-2026-01-15-XYZ	2026-01-15	20:00:00	01:30:00	Giovedì	Introduzione agli strumenti di mixology	15	11
5	Microsoft Teams	MOL-2025-11-05-QWE	2025-11-05	18:00:00	01:00:00	Mercoledì	Tecniche di sferificazione	14	13
11	Zoom	FUSI-2026-03-15-ABC	2026-03-15	19:00:00	01:30:00	Domenica	Introduzione alle spezie Fusion	27	21
12	Google Meet	PANA-2026-05-06-XYZ	2026-05-06	18:00:00	02:00:00	Mercoledì	Analisi dei difetti di lievitazione	28	11
13	Google Meet	VEG-2025-10-06-A	2025-10-06	19:00:00	01:30:00	Lunedì	Verdure di stagione: tecniche di cottura	38	27
14	Google Meet	VEG-2025-10-09-B	2025-10-09	19:00:00	01:30:00	Giovedì	Proteine vegetali: legumi e tofu	38	27
15	Google Meet	VEG-2025-10-13-C	2025-10-13	19:00:00	01:30:00	Lunedì	Cereali e pseudocereali	38	27
16	Google Meet	VEG-2025-10-16-D	2025-10-16	19:00:00	01:30:00	Giovedì	Salse e condimenti vegetariani	38	27
17	Google Meet	VEG-2025-10-20-E	2025-10-20	19:00:00	01:30:00	Lunedì	Piatti unici vegetariani	38	27
18	Google Meet	VEG-2025-10-23-F	2025-10-23	19:00:00	01:30:00	Giovedì	Dessert vegetariani e vegani	38	27
19	Microsoft Teams	MEX-2025-12-01-A	2025-12-01	18:00:00	01:45:00	Lunedì	Introduzione ai sapori messicani	40	7
20	Microsoft Teams	MEX-2025-12-08-B	2025-12-08	18:00:00	01:45:00	Lunedì	Tacos e Tortillas fatte in casa	40	7
21	Microsoft Teams	MEX-2025-12-15-C	2025-12-15	18:00:00	01:45:00	Lunedì	Salse e Guacamole	40	7
22	Microsoft Teams	MEX-2025-12-22-D	2025-12-22	18:00:00	01:45:00	Lunedì	Fajitas e Quesadillas	40	7
23	Microsoft Teams	MEX-2025-12-29-E	2025-12-29	18:00:00	01:45:00	Lunedì	Chili con carne e Tamales	40	7
\.


--
-- TOC entry 3535 (class 0 OID 16847)
-- Dependencies: 228
-- Data for Name: tipodicucina; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.tipodicucina (idtipocucina, nome) FROM stdin;
1	Italiana
2	Mediterranea
5	Francese
6	Giapponese
11	Vegana
12	Panificazione
13	Molecolare
14	Mixology
25	Cioccolateria
26	Fusion
30	Pasticceria
31	Pasta Fresca
32	Vegetariana
33	Messicana
\.


--
-- TOC entry 3536 (class 0 OID 16854)
-- Dependencies: 229
-- Data for Name: tipodicucina_corso; Type: TABLE DATA; Schema: public; Owner: name
--

COPY public.tipodicucina_corso (idtipocucina, idcorso) FROM stdin;
1	3
2	3
5	6
6	7
11	12
12	13
13	14
14	15
25	26
26	27
12	28
31	37
32	38
30	39
33	40
\.


--
-- TOC entry 3572 (class 0 OID 0)
-- Dependencies: 217
-- Name: carta_idcarta_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.carta_idcarta_seq', 32, true);


--
-- TOC entry 3573 (class 0 OID 0)
-- Dependencies: 222
-- Name: chef_idchef_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.chef_idchef_seq', 27, true);


--
-- TOC entry 3574 (class 0 OID 0)
-- Dependencies: 224
-- Name: corso_idcorso_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.corso_idcorso_seq', 40, true);


--
-- TOC entry 3575 (class 0 OID 0)
-- Dependencies: 239
-- Name: ingrediente_idingrediente_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.ingrediente_idingrediente_seq', 33, true);


--
-- TOC entry 3576 (class 0 OID 0)
-- Dependencies: 219
-- Name: partecipante_idpartecipante_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.partecipante_idpartecipante_seq', 33, true);


--
-- TOC entry 3577 (class 0 OID 0)
-- Dependencies: 236
-- Name: ricetta_idricetta_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.ricetta_idricetta_seq', 22, true);


--
-- TOC entry 3578 (class 0 OID 0)
-- Dependencies: 230
-- Name: sessione_presenza_idsessionepresenza_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.sessione_presenza_idsessionepresenza_seq', 33, true);


--
-- TOC entry 3579 (class 0 OID 0)
-- Dependencies: 232
-- Name: sessione_telematica_idsessionetelematica_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.sessione_telematica_idsessionetelematica_seq', 23, true);


--
-- TOC entry 3580 (class 0 OID 0)
-- Dependencies: 227
-- Name: tipodicucina_idtipocucina_seq; Type: SEQUENCE SET; Schema: public; Owner: name
--

SELECT pg_catalog.setval('public.tipodicucina_idtipocucina_seq', 33, true);


--
-- TOC entry 3332 (class 2606 OID 16924)
-- Name: adesione_sessionepresenza adesione_sessionepresenza_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.adesione_sessionepresenza
    ADD CONSTRAINT adesione_sessionepresenza_pkey PRIMARY KEY (idsessionepresenza, idpartecipante);


--
-- TOC entry 3302 (class 2606 OID 16768)
-- Name: carta carta_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.carta
    ADD CONSTRAINT carta_pkey PRIMARY KEY (idcarta);


--
-- TOC entry 3312 (class 2606 OID 16804)
-- Name: chef chef_email_key; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.chef
    ADD CONSTRAINT chef_email_key UNIQUE (email);


--
-- TOC entry 3314 (class 2606 OID 16802)
-- Name: chef chef_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.chef
    ADD CONSTRAINT chef_pkey PRIMARY KEY (idchef);


--
-- TOC entry 3316 (class 2606 OID 16814)
-- Name: corso corso_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.corso
    ADD CONSTRAINT corso_pkey PRIMARY KEY (idcorso);


--
-- TOC entry 3338 (class 2606 OID 16961)
-- Name: ingrediente ingrediente_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.ingrediente
    ADD CONSTRAINT ingrediente_pkey PRIMARY KEY (idingrediente);


--
-- TOC entry 3306 (class 2606 OID 16778)
-- Name: partecipante partecipante_email_key; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante
    ADD CONSTRAINT partecipante_email_key UNIQUE (email);


--
-- TOC entry 3308 (class 2606 OID 16776)
-- Name: partecipante partecipante_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante
    ADD CONSTRAINT partecipante_pkey PRIMARY KEY (idpartecipante);


--
-- TOC entry 3330 (class 2606 OID 16909)
-- Name: partecipante_sessionetelematica partecipante_sessionetelematica_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante_sessionetelematica
    ADD CONSTRAINT partecipante_sessionetelematica_pkey PRIMARY KEY (idpartecipante, idsessionetelematica);


--
-- TOC entry 3310 (class 2606 OID 16783)
-- Name: possiede possiede_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.possiede
    ADD CONSTRAINT possiede_pkey PRIMARY KEY (idpartecipante, idcarta);


--
-- TOC entry 3340 (class 2606 OID 16968)
-- Name: preparazioneingrediente preparazioneingrediente_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.preparazioneingrediente
    ADD CONSTRAINT preparazioneingrediente_pkey PRIMARY KEY (idricetta, idingrediente);


--
-- TOC entry 3334 (class 2606 OID 16940)
-- Name: ricetta ricetta_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.ricetta
    ADD CONSTRAINT ricetta_pkey PRIMARY KEY (idricetta);


--
-- TOC entry 3318 (class 2606 OID 16835)
-- Name: richiestapagamento richiestapagamento_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.richiestapagamento
    ADD CONSTRAINT richiestapagamento_pkey PRIMARY KEY (datarichiesta, idcorso, idpartecipante);


--
-- TOC entry 3326 (class 2606 OID 16876)
-- Name: sessione_presenza sessione_presenza_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza
    ADD CONSTRAINT sessione_presenza_pkey PRIMARY KEY (idsessionepresenza);


--
-- TOC entry 3336 (class 2606 OID 16945)
-- Name: sessione_presenza_ricetta sessione_presenza_ricetta_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza_ricetta
    ADD CONSTRAINT sessione_presenza_ricetta_pkey PRIMARY KEY (idricetta, idsessionepresenza);


--
-- TOC entry 3328 (class 2606 OID 16894)
-- Name: sessione_telematica sessione_telematica_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_telematica
    ADD CONSTRAINT sessione_telematica_pkey PRIMARY KEY (idsessionetelematica);


--
-- TOC entry 3324 (class 2606 OID 16858)
-- Name: tipodicucina_corso tipodicucina_corso_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina_corso
    ADD CONSTRAINT tipodicucina_corso_pkey PRIMARY KEY (idtipocucina, idcorso);


--
-- TOC entry 3320 (class 2606 OID 16853)
-- Name: tipodicucina tipodicucina_nome_key; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina
    ADD CONSTRAINT tipodicucina_nome_key UNIQUE (nome);


--
-- TOC entry 3322 (class 2606 OID 16851)
-- Name: tipodicucina tipodicucina_pkey; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina
    ADD CONSTRAINT tipodicucina_pkey PRIMARY KEY (idtipocucina);


--
-- TOC entry 3304 (class 2606 OID 17002)
-- Name: carta uq_carta_details; Type: CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.carta
    ADD CONSTRAINT uq_carta_details UNIQUE (ultimequattrocifre, datascadenza, circuito, intestatario);


--
-- TOC entry 3370 (class 2620 OID 17032)
-- Name: richiestapagamento trg_controllo_max_partecipanti_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_controllo_max_partecipanti_corso BEFORE INSERT OR UPDATE ON public.richiestapagamento FOR EACH ROW EXECUTE FUNCTION public.verifica_superamento_max_partecipanti_corso();


--
-- TOC entry 3360 (class 2620 OID 17047)
-- Name: carta trg_impedisci_dettagli_carta_duplicati; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_impedisci_dettagli_carta_duplicati BEFORE INSERT OR UPDATE ON public.carta FOR EACH ROW EXECUTE FUNCTION public.impedisci_carta_duplicata_per_partecipante();


--
-- TOC entry 3368 (class 2620 OID 17030)
-- Name: corso trg_impedisci_eliminazione_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_impedisci_eliminazione_corso BEFORE DELETE ON public.corso FOR EACH ROW EXECUTE FUNCTION public.impedisci_eliminazione_corso_se_iscritto();


--
-- TOC entry 3371 (class 2620 OID 17039)
-- Name: richiestapagamento trg_impedisci_pagamento_tardivo; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_impedisci_pagamento_tardivo BEFORE INSERT OR UPDATE ON public.richiestapagamento FOR EACH ROW EXECUTE FUNCTION public.verifica_data_pagamento_prima_inizio_corso();


--
-- TOC entry 3373 (class 2620 OID 17045)
-- Name: tipodicucina_corso trg_max_tipi_cucina_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_max_tipi_cucina_corso BEFORE INSERT ON public.tipodicucina_corso FOR EACH ROW EXECUTE FUNCTION public.verifica_max_tipi_cucina_per_corso();


--
-- TOC entry 3364 (class 2620 OID 17041)
-- Name: chef trg_valida_anni_esperienza_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_anni_esperienza_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.verifica_anni_esperienza_chef();


--
-- TOC entry 3369 (class 2620 OID 17043)
-- Name: corso trg_valida_date_corso; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_date_corso BEFORE INSERT OR UPDATE ON public.corso FOR EACH ROW EXECUTE FUNCTION public.verifica_intervallo_date_corso();


--
-- TOC entry 3365 (class 2620 OID 17034)
-- Name: chef trg_valida_eta_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_eta_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.verifica_intervallo_eta();


--
-- TOC entry 3361 (class 2620 OID 17035)
-- Name: partecipante trg_valida_eta_partecipante; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_eta_partecipante BEFORE INSERT OR UPDATE ON public.partecipante FOR EACH ROW EXECUTE FUNCTION public.verifica_intervallo_eta();


--
-- TOC entry 3372 (class 2620 OID 17037)
-- Name: richiestapagamento trg_valida_importo_pagamento; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_valida_importo_pagamento BEFORE INSERT OR UPDATE ON public.richiestapagamento FOR EACH ROW EXECUTE FUNCTION public.verifica_importo_pagamento_corrisponde_prezzo_corso();


--
-- TOC entry 3366 (class 2620 OID 17074)
-- Name: chef trg_validate_email_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_validate_email_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.validate_email_full();


--
-- TOC entry 3362 (class 2620 OID 17073)
-- Name: partecipante trg_validate_email_partecipante; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_validate_email_partecipante BEFORE INSERT OR UPDATE ON public.partecipante FOR EACH ROW EXECUTE FUNCTION public.validate_email_full();


--
-- TOC entry 3367 (class 2620 OID 17052)
-- Name: chef trg_verifica_email_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_email_chef BEFORE INSERT OR UPDATE ON public.chef FOR EACH ROW EXECUTE FUNCTION public.verifica_unicita_email();


--
-- TOC entry 3363 (class 2620 OID 17051)
-- Name: partecipante trg_verifica_email_partecipante; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_email_partecipante BEFORE INSERT OR UPDATE ON public.partecipante FOR EACH ROW EXECUTE FUNCTION public.verifica_unicita_email();


--
-- TOC entry 3374 (class 2620 OID 17027)
-- Name: sessione_presenza trg_verifica_sessione_presenza_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_sessione_presenza_chef BEFORE INSERT OR UPDATE ON public.sessione_presenza FOR EACH ROW EXECUTE FUNCTION public.verifica_sessioni_contemporanee_chef();


--
-- TOC entry 3375 (class 2620 OID 17028)
-- Name: sessione_telematica trg_verifica_sessione_telematica_chef; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trg_verifica_sessione_telematica_chef BEFORE INSERT OR UPDATE ON public.sessione_telematica FOR EACH ROW EXECUTE FUNCTION public.verifica_sessioni_contemporanee_chef();


--
-- TOC entry 3376 (class 2620 OID 17049)
-- Name: ricetta trigger_elimina_ingredienti_associati; Type: TRIGGER; Schema: public; Owner: name
--

CREATE TRIGGER trigger_elimina_ingredienti_associati BEFORE DELETE ON public.ricetta FOR EACH ROW EXECUTE FUNCTION public.elimina_ingredienti_della_ricetta();


--
-- TOC entry 3354 (class 2606 OID 16930)
-- Name: adesione_sessionepresenza adesione_sessionepresenza_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.adesione_sessionepresenza
    ADD CONSTRAINT adesione_sessionepresenza_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3355 (class 2606 OID 16925)
-- Name: adesione_sessionepresenza adesione_sessionepresenza_idsessionepresenza_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.adesione_sessionepresenza
    ADD CONSTRAINT adesione_sessionepresenza_idsessionepresenza_fkey FOREIGN KEY (idsessionepresenza) REFERENCES public.sessione_presenza(idsessionepresenza);


--
-- TOC entry 3343 (class 2606 OID 17075)
-- Name: corso fk_corso_chef; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.corso
    ADD CONSTRAINT fk_corso_chef FOREIGN KEY (idchef) REFERENCES public.chef(idchef);


--
-- TOC entry 3341 (class 2606 OID 17066)
-- Name: possiede fk_possiede_idcarta_carta_cascade; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.possiede
    ADD CONSTRAINT fk_possiede_idcarta_carta_cascade FOREIGN KEY (idcarta) REFERENCES public.carta(idcarta) ON DELETE CASCADE;


--
-- TOC entry 3352 (class 2606 OID 16910)
-- Name: partecipante_sessionetelematica partecipante_sessionetelematica_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante_sessionetelematica
    ADD CONSTRAINT partecipante_sessionetelematica_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3353 (class 2606 OID 16915)
-- Name: partecipante_sessionetelematica partecipante_sessionetelematica_idsessionetelematica_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.partecipante_sessionetelematica
    ADD CONSTRAINT partecipante_sessionetelematica_idsessionetelematica_fkey FOREIGN KEY (idsessionetelematica) REFERENCES public.sessione_telematica(idsessionetelematica);


--
-- TOC entry 3342 (class 2606 OID 16784)
-- Name: possiede possiede_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.possiede
    ADD CONSTRAINT possiede_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3358 (class 2606 OID 16974)
-- Name: preparazioneingrediente preparazioneingrediente_idingrediente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.preparazioneingrediente
    ADD CONSTRAINT preparazioneingrediente_idingrediente_fkey FOREIGN KEY (idingrediente) REFERENCES public.ingrediente(idingrediente);


--
-- TOC entry 3359 (class 2606 OID 16969)
-- Name: preparazioneingrediente preparazioneingrediente_idricetta_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.preparazioneingrediente
    ADD CONSTRAINT preparazioneingrediente_idricetta_fkey FOREIGN KEY (idricetta) REFERENCES public.ricetta(idricetta);


--
-- TOC entry 3344 (class 2606 OID 16836)
-- Name: richiestapagamento richiestapagamento_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.richiestapagamento
    ADD CONSTRAINT richiestapagamento_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3345 (class 2606 OID 16841)
-- Name: richiestapagamento richiestapagamento_idpartecipante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.richiestapagamento
    ADD CONSTRAINT richiestapagamento_idpartecipante_fkey FOREIGN KEY (idpartecipante) REFERENCES public.partecipante(idpartecipante);


--
-- TOC entry 3348 (class 2606 OID 16882)
-- Name: sessione_presenza sessione_presenza_idchef_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza
    ADD CONSTRAINT sessione_presenza_idchef_fkey FOREIGN KEY (idchef) REFERENCES public.chef(idchef);


--
-- TOC entry 3349 (class 2606 OID 16877)
-- Name: sessione_presenza sessione_presenza_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza
    ADD CONSTRAINT sessione_presenza_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3356 (class 2606 OID 16946)
-- Name: sessione_presenza_ricetta sessione_presenza_ricetta_idricetta_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza_ricetta
    ADD CONSTRAINT sessione_presenza_ricetta_idricetta_fkey FOREIGN KEY (idricetta) REFERENCES public.ricetta(idricetta);


--
-- TOC entry 3357 (class 2606 OID 16951)
-- Name: sessione_presenza_ricetta sessione_presenza_ricetta_idsessionepresenza_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_presenza_ricetta
    ADD CONSTRAINT sessione_presenza_ricetta_idsessionepresenza_fkey FOREIGN KEY (idsessionepresenza) REFERENCES public.sessione_presenza(idsessionepresenza);


--
-- TOC entry 3350 (class 2606 OID 16900)
-- Name: sessione_telematica sessione_telematica_idchef_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_telematica
    ADD CONSTRAINT sessione_telematica_idchef_fkey FOREIGN KEY (idchef) REFERENCES public.chef(idchef);


--
-- TOC entry 3351 (class 2606 OID 16895)
-- Name: sessione_telematica sessione_telematica_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.sessione_telematica
    ADD CONSTRAINT sessione_telematica_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3346 (class 2606 OID 16864)
-- Name: tipodicucina_corso tipodicucina_corso_idcorso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina_corso
    ADD CONSTRAINT tipodicucina_corso_idcorso_fkey FOREIGN KEY (idcorso) REFERENCES public.corso(idcorso);


--
-- TOC entry 3347 (class 2606 OID 16859)
-- Name: tipodicucina_corso tipodicucina_corso_idtipocucina_fkey; Type: FK CONSTRAINT; Schema: public; Owner: name
--

ALTER TABLE ONLY public.tipodicucina_corso
    ADD CONSTRAINT tipodicucina_corso_idtipocucina_fkey FOREIGN KEY (idtipocucina) REFERENCES public.tipodicucina(idtipocucina);


--
-- TOC entry 3554 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE adesione_sessionepresenza; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.adesione_sessionepresenza TO "Mario";


--
-- TOC entry 3555 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE carta; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.carta TO "Mario";


--
-- TOC entry 3556 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE chef; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.chef TO "Mario";


--
-- TOC entry 3557 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE corso; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.corso TO "Mario";


--
-- TOC entry 3558 (class 0 OID 0)
-- Dependencies: 240
-- Name: TABLE ingrediente; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.ingrediente TO "Mario";


--
-- TOC entry 3559 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE partecipante; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.partecipante TO "Mario";


--
-- TOC entry 3560 (class 0 OID 0)
-- Dependencies: 234
-- Name: TABLE partecipante_sessionetelematica; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.partecipante_sessionetelematica TO "Mario";


--
-- TOC entry 3561 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE possiede; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.possiede TO "Mario";


--
-- TOC entry 3562 (class 0 OID 0)
-- Dependencies: 241
-- Name: TABLE preparazioneingrediente; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.preparazioneingrediente TO "Mario";


--
-- TOC entry 3563 (class 0 OID 0)
-- Dependencies: 238
-- Name: TABLE sessione_presenza_ricetta; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.sessione_presenza_ricetta TO "Mario";


--
-- TOC entry 3564 (class 0 OID 0)
-- Dependencies: 242
-- Name: TABLE quantitapersessione; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.quantitapersessione TO "Mario";


--
-- TOC entry 3565 (class 0 OID 0)
-- Dependencies: 237
-- Name: TABLE ricetta; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.ricetta TO "Mario";


--
-- TOC entry 3566 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE richiestapagamento; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.richiestapagamento TO "Mario";


--
-- TOC entry 3567 (class 0 OID 0)
-- Dependencies: 231
-- Name: TABLE sessione_presenza; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.sessione_presenza TO "Mario";


--
-- TOC entry 3568 (class 0 OID 0)
-- Dependencies: 233
-- Name: TABLE sessione_telematica; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.sessione_telematica TO "Mario";


--
-- TOC entry 3569 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE tipodicucina; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.tipodicucina TO "Mario";


--
-- TOC entry 3570 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE tipodicucina_corso; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.tipodicucina_corso TO "Mario";


--
-- TOC entry 3571 (class 0 OID 0)
-- Dependencies: 243
-- Name: TABLE vista_statistiche_mensili_chef; Type: ACL; Schema: public; Owner: name
--

GRANT ALL ON TABLE public.vista_statistiche_mensili_chef TO "Mario";


-- Completed on 2025-06-29 21:35:52

--
-- PostgreSQL database dump complete
--

