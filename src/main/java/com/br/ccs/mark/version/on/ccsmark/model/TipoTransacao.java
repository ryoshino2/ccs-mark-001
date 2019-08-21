package com.br.ccs.mark.version.on.ccsmark.model;

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
}
