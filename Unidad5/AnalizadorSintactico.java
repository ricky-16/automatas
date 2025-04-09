package Unidad5;


import java.io.*;
import java.util.*;
import javax.swing.*;

public class AnalizadorSintactico {
    private List<Token> tokens;
    private int posicionActual;
    private Token tokenActual;
    private StringBuilder errores;

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicionActual = 0;
        this.errores = new StringBuilder();
        avanzar();
    }

    // Método principal para iniciar el análisis sintáctico
    public boolean analizar() {
        try {
            programa();
            if (posicionActual < tokens.size()) {
                reportarError("Se esperaba fin de archivo, pero hay más tokens");
                return false;
            }
            return errores.length() == 0;
        } catch (Exception e) {
            reportarError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    // Obtener los errores encontrados
    public String getErrores() {
        return errores.toString();
    }

    // Avanzar al siguiente token
    private void avanzar() {
        if (posicionActual < tokens.size()) {
            tokenActual = tokens.get(posicionActual++);
        } else {
            tokenActual = null;
        }
    }

    // Verificar si el token actual coincide con el tipo esperado
    private boolean coincidir(int tipoToken) {
        if (tokenActual != null && tokenActual.getTipo() == tipoToken) {
            avanzar();
            return true;
        }
        return false;
    }

    // Verificar si el token actual es una palabra reservada específica
    private boolean coincidirPalabraReservada(String palabra) {
        if (tokenActual != null && tokenActual.getValor().equals(palabra)) {
            avanzar();
            return true;
        }
        return false;
    }

    // Esperar un token específico, reportar error si no coincide
    private boolean esperar(int tipoToken, String mensaje) {
        if (coincidir(tipoToken)) {
            return true;
        }
        reportarError(mensaje);
        return false;
    }

    // Esperar una palabra reservada específica, reportar error si no coincide
    private boolean esperarPalabraReservada(String palabra, String mensaje) {
        if (coincidirPalabraReservada(palabra)) {
            return true;
        }
        reportarError(mensaje);
        return false;
    }

    // Reportar un error
    private void reportarError(String mensaje) {
        String ubicacion = tokenActual != null ? 
            " en línea " + tokenActual.getLinea() + ", token: " + tokenActual.getValor() :
            " al final del archivo";
        errores.append("Error sintáctico").append(ubicacion).append(": ").append(mensaje).append("\n");
    }

    // Implementación de las reglas gramaticales

    // <programa> ::= "program" <identificador> ";" <bloque_principal>
    private void programa() {
        esperarPalabraReservada("program", "Se esperaba 'program'");
        esperar(-51, "Se esperaba un identificador después de 'program'");
        esperar(-73, "Se esperaba ';' después del nombre del programa");
        bloquePrincipal();
    }

    // <bloque_principal> ::= <declaracion_variables> <declaraciones_subprogramas> "begin" <instrucciones> "end"
    private void bloquePrincipal() {
        declaracionVariables();
        declaracionesSubprogramas();
        esperarPalabraReservada("begin", "Se esperaba 'begin'");
        instrucciones();
        esperarPalabraReservada("end", "Se esperaba 'end'");
    }

    // <declaracion_variables> ::= "var" <lista_declaraciones> | ε
    private void declaracionVariables() {
        if (coincidirPalabraReservada("var")) {
            listaDeclaraciones();
        }
    }

    // <lista_declaraciones> ::= <declaracion> ";" <lista_declaraciones> | <declaracion> ";" | ε
    private void listaDeclaraciones() {
        // Verificamos si hay una declaración
        if (tokenActual != null && tokenActual.getTipo() == -51) {
            declaracion();
            esperar(-73, "Se esperaba ';' después de la declaración");
            
            // Verificamos si hay más declaraciones
            if (tokenActual != null && tokenActual.getTipo() == -51) {
                listaDeclaraciones();
            }
        }
    }

    // <declaracion> ::= <lista_identificadores> ":" <tipo> | <identificador> "array" "[" <lista_indices> "]" ":" <tipo>
    private void declaracion() {
        if (tokenActual != null && tokenActual.getTipo() == -51) {
            String primerIdentificador = tokenActual.getValor();
            avanzar();
            
            // Verificamos si es una declaración de array
            if (coincidirPalabraReservada("array")) {
                esperar(-76, "Se esperaba '[' después de 'array'");
                listaIndices();
                esperar(-77, "Se esperaba ']' después de los índices");
                esperar(-75, "Se esperaba ':' después de la declaración de array");
                tipo();
            } else {
                // Es una declaración normal
                // Verificamos si hay más identificadores
                while (coincidir(-74)) { // -74 es la coma
                    esperar(-51, "Se esperaba un identificador después de ','");
                }
                esperar(-75, "Se esperaba ':' después de la lista de identificadores");
                tipo();
            }
        } else {
            reportarError("Se esperaba un identificador para iniciar la declaración");
        }
    }

    // <lista_indices> ::= <entero> "," <entero>
    private void listaIndices() {
        esperar(-61, "Se esperaba un entero como índice");
        esperar(-74, "Se esperaba ',' entre índices");
        esperar(-61, "Se esperaba un entero como segundo índice");
    }

    // <tipo> ::= "int" | "real" | "bool" | "string"
    private void tipo() {
        if (coincidirPalabraReservada("int") || 
            coincidirPalabraReservada("real") || 
            coincidirPalabraReservada("bool") || 
            coincidirPalabraReservada("string")) {
            return;
        }
        reportarError("Se esperaba un tipo de dato (int, real, bool, string)");
    }

    // <declaraciones_subprogramas> ::= <subprograma> <declaraciones_subprogramas> | ε
    private void declaracionesSubprogramas() {
        if (tokenActual != null && 
            (tokenActual.getValor().equals("procedure") || tokenActual.getValor().equals("function"))) {
            subprograma();
            declaracionesSubprogramas();
        }
    }

    // <subprograma> ::= <procedimiento> | <funcion>
    private void subprograma() {
        if (tokenActual != null) {
            if (tokenActual.getValor().equals("procedure")) {
                procedimiento();
            } else if (tokenActual.getValor().equals("function")) {
                funcion();
            } else {
                reportarError("Se esperaba 'procedure' o 'function'");
            }
        }
    }

    // <procedimiento> ::= "procedure" <identificador> "(" <parametros> ")" ";" <cuerpo_subprograma>
    private void procedimiento() {
        esperarPalabraReservada("procedure", "Se esperaba 'procedure'");
        esperar(-51, "Se esperaba un identificador después de 'procedure'");
        esperar(-71, "Se esperaba '(' después del nombre del procedimiento");
        parametros();
        esperar(-72, "Se esperaba ')' después de los parámetros");
        esperar(-73, "Se esperaba ';' después de la declaración del procedimiento");
        cuerpoSubprograma();
    }

    // <funcion> ::= "function" <identificador> "(" <parametros> ")" ":" <tipo> <cuerpo_subprograma>
    private void funcion() {
        esperarPalabraReservada("function", "Se esperaba 'function'");
        esperar(-51, "Se esperaba un identificador después de 'function'");
        esperar(-71, "Se esperaba '(' después del nombre de la función");
        parametros();
        esperar(-72, "Se esperaba ')' después de los parámetros");
        esperar(-75, "Se esperaba ':' después de la declaración de la función");
        tipo();
        cuerpoSubprograma();
    }

    // <cuerpo_subprograma> ::= <declaracion_variables> "begin" <instrucciones> "end" ";"
    private void cuerpoSubprograma() {
        declaracionVariables();
        esperarPalabraReservada("begin", "Se esperaba 'begin'");
        instrucciones();
        esperarPalabraReservada("end", "Se esperaba 'end'");
        esperar(-73, "Se esperaba ';' después de 'end'");
    }

    // <parametros> ::= <identificador> ":" <tipo> | <identificador> ":" <tipo> "," <parametros> | ε
    private void parametros() {
        if (tokenActual != null && tokenActual.getTipo() == -51) {
            avanzar();
            esperar(-75, "Se esperaba ':' después del identificador en los parámetros");
            tipo();
            
            // Verificamos si hay más parámetros
            if (coincidir(-74)) { // -74 es la coma
                parametros();
            }
        }
    }

    // <instrucciones> ::= <instruccion> ";" <instrucciones> | <instruccion> ";" | ε
    private void instrucciones() {
        if (tokenActual != null && !tokenActual.getValor().equals("end") && 
            !tokenActual.getValor().equals("else") && !tokenActual.getValor().equals("until")) {
            instruccion();
            esperar(-73, "Se esperaba ';' después de la instrucción");
            
            // Verificamos si hay más instrucciones
            if (tokenActual != null && !tokenActual.getValor().equals("end") && 
                !tokenActual.getValor().equals("else") && !tokenActual.getValor().equals("until")) {
                instrucciones();
            }
        }
    }

    // <instruccion> ::= <asignacion> | <llamada_funcion_o_proc> | <condicional_if> | <bucle_while> | <bucle_repeat> | <entrada_salida>
    private void instruccion() {
        if (tokenActual == null) {
            reportarError("Se esperaba una instrucción");
            return;
        }
        
        switch (tokenActual.getValor()) {
            case "if":
                condicionalIf();
                break;
            case "while":
                bucleWhile();
                break;
            case "repeat":
                bucleRepeat();
                break;
            case "write":
            case "read":
                entradaSalida();
                break;
            default:
                if (tokenActual.getTipo() == -51) {
                    // Puede ser asignación o llamada a función/procedimiento
                    String identificador = tokenActual.getValor();
                    avanzar();
                    
                    if (tokenActual != null) {
                        if (tokenActual.getValor().equals("=")) {
                            // Es una asignación
                            retroceder(); // Volvemos al identificador
                            asignacion();
                        } else if (tokenActual.getValor().equals("[")) {
                            // Es una asignación a un elemento de array
                            retroceder(); // Volvemos al identificador
                            asignacion();
                        } else if (tokenActual.getValor().equals("(")) {
                            // Es una llamada a función o procedimiento
                            retroceder(); // Volvemos al identificador
                            llamadaFuncionOProc();
                        } else {
                            reportarError("Se esperaba '=', '[' o '(' después del identificador");
                        }
                    } else {
                        reportarError("Token inesperado después del identificador");
                    }
                } else {
                    reportarError("Se esperaba una instrucción válida");
                }
                break;
        }
    }

    // Método auxiliar para retroceder un token
    private void retroceder() {
        if (posicionActual > 0) {
            posicionActual--;
            tokenActual = tokens.get(posicionActual - 1);
        }
    }

    // <asignacion> ::= <variable> "=" <expresion>
    private void asignacion() {
        variable();
        esperar(-32, "Se esperaba '=' en la asignación"); // -32 es el operador de asignación
        expresion();
    }

    // <variable> ::= <identificador> | <identificador> "[" <lista_expresiones> "]"
    private void variable() {
        esperar(-51, "Se esperaba un identificador");
        
        // Verificamos si es un acceso a array
        if (tokenActual != null && tokenActual.getValor().equals("[")) {
            avanzar();
            listaExpresiones();
            esperar(-77, "Se esperaba ']' después de los índices");
        }
    }

    // <llamada_funcion_o_proc> ::= <identificador> "(" <lista_expresiones> ")"
    private void llamadaFuncionOProc() {
        esperar(-51, "Se esperaba un identificador de función o procedimiento");
        esperar(-71, "Se esperaba '(' después del nombre de la función o procedimiento");
        listaExpresiones();
        esperar(-72, "Se esperaba ')' después de los argumentos");
    }

    // <condicional_if> ::= "if" "(" <expresion> ")" "then" <bloque_if> <opcional_else>
    private void condicionalIf() {
        esperarPalabraReservada("if", "Se esperaba 'if'");
        esperar(-71, "Se esperaba '(' después de 'if'");
        expresion();
        esperar(-72, "Se esperaba ')' después de la condición");
        esperarPalabraReservada("then", "Se esperaba 'then'");
        bloqueIf();
        opcionalElse();
    }

    // <opcional_else> ::= "else" <bloque_if> | ε
    private void opcionalElse() {
        if (coincidirPalabraReservada("else")) {
            bloqueIf();
        }
    }

    // <bloque_if> ::= "begin" <instrucciones> "end" | <instruccion>
    private void bloqueIf() {
        if (coincidirPalabraReservada("begin")) {
            instrucciones();
            esperarPalabraReservada("end", "Se esperaba 'end'");
        } else {
            instruccion();
        }
    }

    // <bucle_while> ::= "while" "(" <expresion> ")" "do" <bloque_if>
    private void bucleWhile() {
        esperarPalabraReservada("while", "Se esperaba 'while'");
        esperar(-71, "Se esperaba '(' después de 'while'");
        expresion();
        esperar(-72, "Se esperaba ')' después de la condición");
        esperarPalabraReservada("do", "Se esperaba 'do'");
        bloqueIf();
    }

    // <bucle_repeat> ::= "repeat" <bloque_if> "until" "(" <expresion> ")"
    private void bucleRepeat() {
        esperarPalabraReservada("repeat", "Se esperaba 'repeat'");
        bloqueIf();
        esperarPalabraReservada("until", "Se esperaba 'until'");
        esperar(-71, "Se esperaba '(' después de 'until'");
        expresion();
        esperar(-72, "Se esperaba ')' después de la condición");
    }

    // <entrada_salida> ::= "write" "(" <expresion> ")" | "read" "(" <variable> ")"
    private void entradaSalida() {
        if (coincidirPalabraReservada("write")) {
            esperar(-71, "Se esperaba '(' después de 'write'");
            expresion();
            esperar(-72, "Se esperaba ')' después de la expresión");
        } else if (coincidirPalabraReservada("read")) {
            esperar(-71, "Se esperaba '(' después de 'read'");
            variable();
            esperar(-72, "Se esperaba ')' después de la variable");
        } else {
            reportarError("Se esperaba 'write' o 'read'");
        }
    }

    // <expresion> ::= <expresion_simple> <op_relacional> <expresion_simple> | <expresion_simple>
    private void expresion() {
        expresionSimple();
        
        // Verificamos si hay un operador relacional
        if (tokenActual != null && (
            tokenActual.getTipo() == -28 || // ==
            tokenActual.getTipo() == -29 || // !=
            tokenActual.getTipo() == -33 || // <
            tokenActual.getTipo() == -34 || // <=
            tokenActual.getTipo() == -35 || // >
            tokenActual.getTipo() == -36    // >=
        )) {
            opRelacional();
            expresionSimple();
        }
    }

    // <expresion_simple> ::= <termino> | <expresion_simple> <op_aditivo> <termino>
    private void expresionSimple() {
        termino();
        
        // Verificamos si hay un operador aditivo
        while (tokenActual != null && (
            tokenActual.getTipo() == -26 || // +
            tokenActual.getTipo() == -27    // -
        )) {
            opAditivo();
            termino();
        }
    }

    // <termino> ::= <factor> | <termino> <op_multiplicativo> <factor>
    private void termino() {
        factor();
        
        // Verificamos si hay un operador multiplicativo
        while (tokenActual != null && (
            tokenActual.getTipo() == -23 || // *
            tokenActual.getTipo() == -24    // /
        )) {
            opMultiplicativo();
            factor();
        }
    }

    // <factor> ::= <numero> | <variable> | <constante_booleana> | <cadena> | "(" <expresion> ")" | "-" <factor> | <llamada_funcion_o_proc>
    private void factor() {
        if (tokenActual == null) {
            reportarError("Se esperaba un factor");
            return;
        }
        
        switch (tokenActual.getTipo()) {
            case -61: // entero
            case -62: // real
                avanzar(); // Consumimos el número
                break;
            case -51: // identificador
                // Puede ser variable o llamada a función
                String identificador = tokenActual.getValor();
                avanzar();
                
                if (tokenActual != null && tokenActual.getValor().equals("(")) {
                    // Es una llamada a función
                    retroceder(); // Volvemos al identificador
                    llamadaFuncionOProc();
                } else if (tokenActual != null && tokenActual.getValor().equals("[")) {
                    // Es un acceso a array
                    retroceder(); // Volvemos al identificador
                    variable();
                } else {
                    // Es una variable simple, ya consumimos el identificador
                }
                break;
            case -21: // true
            case -22: // false
                avanzar(); // Consumimos la constante booleana
                break;
            case -63: // cadena
                avanzar(); // Consumimos la cadena
                break;
            default:
                if (tokenActual.getValor().equals("(")) {
                    avanzar();
                    expresion();
                    esperar(-72, "Se esperaba ')' después de la expresión");
                } else if (tokenActual.getValor().equals("-")) {
                    avanzar();
                    factor();
                } else {
                    reportarError("Factor no válido");
                }
                break;
        }
    }

    // <lista_expresiones> ::= <expresion> | <expresion> "," <lista_expresiones> | ε
    private void listaExpresiones() {
        if (tokenActual != null && !tokenActual.getValor().equals(")") && !tokenActual.getValor().equals("]")) {
            expresion();
            
            // Verificamos si hay más expresiones
            if (coincidir(-74)) { // -74 es la coma
                listaExpresiones();
            }
        }
    }

    // <op_relacional> ::= "==" | "!=" | "<" | "<=" | ">" | ">="
    private void opRelacional() {
        if (tokenActual != null && (
            tokenActual.getTipo() == -28 || // ==
            tokenActual.getTipo() == -29 || // !=
            tokenActual.getTipo() == -33 || // <
            tokenActual.getTipo() == -34 || // <=
            tokenActual.getTipo() == -35 || // >
            tokenActual.getTipo() == -36    // >=
        )) {
            avanzar();
        } else {
            reportarError("Se esperaba un operador relacional");
        }
    }

    // <op_aditivo> ::= "+" | "-"
    private void opAditivo() {
        if (tokenActual != null && (
            tokenActual.getTipo() == -26 || // +
            tokenActual.getTipo() == -27    // -
        )) {
            avanzar();
        } else {
            reportarError("Se esperaba un operador aditivo");
        }
    }

    // <op_multiplicativo> ::= "*" | "/"
    private void opMultiplicativo() {
        if (tokenActual != null && (
            tokenActual.getTipo() == -23 || // *
            tokenActual.getTipo() == -24    // /
        )) {
            avanzar();
        } else {
            reportarError("Se esperaba un operador multiplicativo");
        }
    }
}
