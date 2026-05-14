package api.servico.adega.enums;

import lombok.Getter;

@Getter
public enum FormaPagamento {
    DINHEIRO("Dinheiro"),
    PIX("PIX"),
    CARTAO_DEBITO("Cartão de Débito"),
    CARTAO_CREDITO("Cartão de Crédito");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public static FormaPagamento paraEnum(String texto) {
        for (FormaPagamento f : values()) {
            if (f.name().equalsIgnoreCase(texto) || f.getDescricao().equalsIgnoreCase(texto)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Forma de pagamento inválida: " + texto);
    }
}