package com.progetto.Entity.EntityDto;

import java.time.LocalDate;

public class CartaDiCredito {

    public CartaDiCredito() {
    }

private String Intestatario;
private LocalDate DataScadenza;
private String UltimeQuattroCifre;
private String  Circuito;
private String IdCarta;

public CartaDiCredito(String Intestatario,LocalDate DataScadenza,String UltimeQuattroCifre, String Circuito ){
this.Intestatario=Intestatario;
this.DataScadenza=DataScadenza;
this.UltimeQuattroCifre=UltimeQuattroCifre;
this.Circuito=Circuito;
}
public String getIntestatario() {
    return Intestatario;
}

public void setIntestatario(String intestatario) {
    this.Intestatario = intestatario;
}

public LocalDate getDataScadenza() {
    return DataScadenza;
}

public void setDataScadenza(LocalDate dataScadenza) {
    this.DataScadenza = dataScadenza;
}

public String getUltimeQuattroCifre() {
    return UltimeQuattroCifre;
}

public void setUltimeQuattroCifre(String ultimeQuattroCifre) {
    this.UltimeQuattroCifre = ultimeQuattroCifre;
}

public String getCircuito() {
    return Circuito;
}

public void setCircuito(Circuito circuito) {
    this.Circuito = circuito;
}
public String getIdCarta() {
    return IdCarta;
}

public void setIdCarta(String idCarta) {
    this.IdCarta=idCarta;
}

}
