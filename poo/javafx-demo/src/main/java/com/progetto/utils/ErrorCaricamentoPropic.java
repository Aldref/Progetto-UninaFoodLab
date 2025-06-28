package com.progetto.utils;

public class ErrorCaricamentoPropic extends RuntimeException {
    public ErrorCaricamentoPropic() {
        super("Propic non trovata");
    }
}
