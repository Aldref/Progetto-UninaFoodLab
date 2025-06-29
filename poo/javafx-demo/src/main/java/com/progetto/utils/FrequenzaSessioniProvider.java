package com.progetto.utils;

import com.progetto.Entity.entityDao.BarraDiRicercaDao;
import java.util.List;

public class FrequenzaSessioniProvider {
    private static List<String> frequenze;

    public static List<String> getFrequenze() {
        if (frequenze == null) {
            BarraDiRicercaDao dao = new BarraDiRicercaDao();
            frequenze = dao.CeraEnumFrequenza();
        }
        return frequenze;
    }
}
