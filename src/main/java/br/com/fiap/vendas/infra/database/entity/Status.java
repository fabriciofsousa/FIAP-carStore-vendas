package br.com.fiap.vendas.infra.database.entity;

public enum Status {
    INICIADA("INICIADA"),
    CONCLUIDA("CONCLUIDA"),
    CANCELADA("CANCELADA");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public static Status validateStatus(String status) {
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException(
                "Status inválido: '" + status + "'. Valores válidos: INICIADA, CONCLUIDA, CANCELADA."
        );
    }
}
