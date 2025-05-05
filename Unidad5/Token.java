package Unidad5;

public class Token {
    private String lexema;
    private int codigo;
    private int tipo;
    private int linea;

    public Token(String lexema, int codigo, int tipo, int linea) {
        this.lexema = lexema;
        this.codigo = codigo;
        this.tipo = tipo;
        this.linea = linea;
    }

    public String getLexema() {
        return lexema;
    }

    public int getCodigo() {
        return codigo;
    }

    public int getTipo() {
        return tipo;
    }

    public int getLinea() {
        return linea;
    }

    @Override
    public String toString() {
        return "Token{" +
                "lexema='" + lexema + '\'' +
                ", codigo=" + codigo +
                ", tipo=" + tipo +
                ", linea=" + linea +
                '}';
    }
}
