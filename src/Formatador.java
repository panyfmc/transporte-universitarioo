import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formata dinheiro e datas no padrão brasileiro para exibir no terminal. */
public final class Formatador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatador() {
    }

    public static String moeda(double valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }

    public static String data(LocalDate data) {
        return data.format(FORMATO_DATA);
    }
}
