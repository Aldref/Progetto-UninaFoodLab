package com.progetto.Entity.entityDao;

public class ErrorCaricamentoPropic extends RuntimeException {
    public ErrorCaricamentoPropic() {
        super("Propic non trovata");
    }
}
