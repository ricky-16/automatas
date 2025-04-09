package Unidad5;


public class Token {
    private int tipo;
    private String valor;
    private int linea;

    public Token(int tipo, String valor, int linea) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
    }

    public int getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getLinea() {
        return linea;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tipo=" + tipo +
                ", valor='" + valor + '\'' +
                ", linea=" + linea +
                '}';
    }
}
