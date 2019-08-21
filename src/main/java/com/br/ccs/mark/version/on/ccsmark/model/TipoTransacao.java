package com.br.ccs.mark.version.on.ccsmark.model;

import java.util.Random;

public enum TipoTransacao {
    DEBIT("DEBIT"),
    CREDIT("CREDIT");

    private String description;

    TipoTransacao(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static TipoTransacao pegarTransacaoAleatoria() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
