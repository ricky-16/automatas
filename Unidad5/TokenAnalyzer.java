package Unidad5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenAnalyzer {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;
    private boolean hasError;
    private StringBuilder errorMessages;

    public TokenAnalyzer(String filePath) {
        tokens = new ArrayList<>();
        currentTokenIndex = 0;
        hasError = false;
        errorMessages = new StringBuilder();
        loadTokens(filePath);
    }

    private void loadTokens(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorar líneas vacías
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Dividir la línea en partes usando tabulaciones
                String[] parts = line.split("\t");
                
                // Verificar que la línea tenga el formato correcto
                if (parts.length >= 4) {
                    String lexema = parts[0];
                    int codigo = Integer.parseInt(parts[1]);
                    int tipo = Integer.parseInt(parts[2]);
                    int linea = Integer.parseInt(parts[3]);
                    
                    // Ignorar comentarios (código -100)
                    if (codigo != -100) {
                        tokens.add(new Token(lexema, codigo, tipo, linea));
                    }
                }
            }
            
            if (!tokens.isEmpty()) {
                currentToken = tokens.get(0);
            }
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir valores numéricos: " + e.getMessage());
        }
    }

    public boolean analyze() {
        if (currentToken == null) {
            errorMessages.append("El archivo no contiene tokens válidos.\n");
            hasError = true;
            return false;
        }
        programa();
        return !hasError;
    }


    public String getErrorMessages() {
        return errorMessages.toString();
    }

    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    private void error(String expected) {
        hasError = true;
        errorMessages.append("Error en línea ").append(currentToken.getLinea())
                    .append(": Se esperaba ").append(expected)
                    .append(" pero se encontró '").append(currentToken.getLexema())
                    .append("'\n");
    }

    private boolean match(String lexema) {
        if (currentToken.getLexema().equals(lexema)) {
            advance();
            return true;
        }
        error("'" + lexema + "'");
        return false;
    }

    private boolean matchCodigo(int codigo) {
        if (currentToken.getCodigo() == codigo) {
            advance();
            return true;
        }
        error("token con código " + codigo);
        return false;
    }

    // <programa> ::= "program" <identificador> ";" <bloque_principal>
    private void programa() {
        if (match("program")) {
            identificador();
            match(";");
            bloquePrincipal();
        }
    }

    // <bloque_principal> ::= <declaracion_variables> <declaraciones_subprogramas> "begin" <instrucciones> "end"
    private void bloquePrincipal() {
        declaracionVariables();
        declaracionesSubprogramas();
        match("begin");
        instrucciones();
        match("end");
    }

    // <declaracion_variables> ::= "var" <lista_declaraciones>
    private void declaracionVariables() {
        match("var");
        listaDeclaraciones();
    }

    // <lista_declaraciones> ::= <declaracion> ";" <lista_declaraciones> | <declaracion> ";"
    private void listaDeclaraciones() {
        declaracion();
        match(";");
        
        // Verificar si hay más declaraciones
        if (currentToken.getLexema().equals("var") || 
            (currentTokenIndex < tokens.size() - 1 && 
             tokens.get(currentTokenIndex + 1).getLexema().equals(":"))) {
            listaDeclaraciones();
        }
    }

    // <declaracion> ::= <lista_identificadores> ":" <tipo> | <identificador> "array" "[" <lista_indices> "]" ":" <tipo>
    private void declaracion() {
        // Guardar el índice actual para poder retroceder si es necesario
        int savedIndex = currentTokenIndex;
        Token savedToken = currentToken;
        
        // Intentar la primera alternativa: <lista_identificadores> ":" <tipo>
        try {
            listaIdentificadores();
            if (match(":")) {
                tipo();
                return;
            }
        } catch (Exception e) {
            // Restaurar el estado si falla
            currentTokenIndex = savedIndex;
            currentToken = savedToken;
        }
        
        // Segunda alternativa: <identificador> "array" "[" <lista_indices> "]" ":" <tipo>
        identificador();
        match("array");
        match("[");
        listaIndices();
        match("]");
        match(":");
        tipo();
    }

    // <lista_identificadores> ::= <identificador> | <identificador> "," <lista_identificadores>
    private void listaIdentificadores() {
        identificador();
        if (currentToken.getLexema().equals(",")) {
            match(",");
            listaIdentificadores();
        }
    }

    // <lista_indices> ::= <entero> "," <entero>
    private void listaIndices() {
        entero();
        match(",");
        entero();
    }

    // <tipo> ::= "int" | "real" | "bool" | "string"
    private void tipo() {
        if (currentToken.getLexema().equals("int") || 
            currentToken.getLexema().equals("real") || 
            currentToken.getLexema().equals("bool") || 
            currentToken.getLexema().equals("string")) {
            advance();
        } else {
            error("un tipo (int, real, bool, string)");
        }
    }

    // <declaraciones_subprogramas> ::= <subprograma> <declaraciones_subprogramas> | ε
    private void declaracionesSubprogramas() {
        if (currentToken.getLexema().equals("procedure") || 
            currentToken.getLexema().equals("function")) {
            subprograma();
            declaracionesSubprogramas();
        }
        // ε (epsilon) - no hacer nada
    }

    // <subprograma> ::= <procedimiento> | <funcion>
    private void subprograma() {
        if (currentToken.getLexema().equals("procedure")) {
            procedimiento();
        } else if (currentToken.getLexema().equals("function")) {
            funcion();
        } else {
            error("'procedure' o 'function'");
        }
    }

    // <procedimiento> ::= "procedure" <identificador> "(" <parametros> ")" ";" <cuerpo_subprograma>
    private void procedimiento() {
        match("procedure");
        identificador();
        match("(");
        parametros();
        match(")");
        match(";");
        cuerpoSubprograma();
    }

    // <funcion> ::= "function" <identificador> "(" <parametros> ")" ":" <tipo> <cuerpo_subprograma>
    private void funcion() {
        match("function");
        identificador();
        match("(");
        parametros();
        match(")");
        match(":");
        tipo();
        cuerpoSubprograma();
    }

    // <cuerpo_subprograma> ::= "var" <declaracion> ";" "begin" <instrucciones> "end" ";"
    private void cuerpoSubprograma() {
        match("var");
        declaracion();
        match(";");
        match("begin");
        instrucciones();
        match("end");
        match(";");
    }

    // <parametros> ::= <identificador> ":" <tipo> | <identificador> ":" <tipo> "," <parametros>
    private void parametros() {
        identificador();
        match(":");
        tipo();
        if (currentToken.getLexema().equals(",")) {
            match(",");
            parametros();
        }
    }

    // <instrucciones> ::= <instruccion> ";" <instrucciones> | <instruccion> ";"
    private void instrucciones() {
        instruccion();
        match(";");
        
        // Verificar si hay más instrucciones
        if (!currentToken.getLexema().equals("end") && 
        	    !currentToken.getLexema().equals("else") && 
        	    !currentToken.getLexema().equals("until")) {
        	    // Verifica que no se esté llamando a instrucciones innecesarias
        	    if (currentTokenIndex > tokens.size()) {
        	        instrucciones();
        	    }}
        	}


    // <instruccion> ::= <asignacion> | <llamada_funcion_o_proc> | <condicional_if> | <bucle_while> | <bucle_repeat> | <entrada_salida>
    private void instruccion() {
        if (currentToken.getCodigo() == -51) { // Identificador
            // Guardar el estado actual
            int savedIndex = currentTokenIndex;
            Token savedToken = currentToken;
            
            // Avanzar para ver el siguiente token
            advance();
            
            if (currentToken.getLexema().equals("=")) {
                // Es una asignación
                currentTokenIndex = savedIndex;
                currentToken = savedToken;
                asignacion();
            } else if (currentToken.getLexema().equals("[")) {
                // Es una asignación a un array
                currentTokenIndex = savedIndex;
                currentToken = savedToken;
                asignacion();
            } else if (currentToken.getLexema().equals("(")) {
                // Es una llamada a función o procedimiento
                currentTokenIndex = savedIndex;
                currentToken = savedToken;
                llamadaFuncionOProc();
            } else {
                error("'=', '[' o '('");
            }
        } else if (currentToken.getLexema().equals("if")) {
            condicionalIf();
        } else if (currentToken.getLexema().equals("while")) {
            bucleWhile();
        } else if (currentToken.getLexema().equals("repeat")) {
            bucleRepeat();
        } else if (currentToken.getLexema().equals("write") || 
                   currentToken.getLexema().equals("read")) {
            entradaSalida();
        } else {
            error("una instrucción válida");
        }
    }

    // <asignacion> ::= <variable> "=" <expresion>
    private void asignacion() {
        variable();
        match("=");
        expresion();
    }

    // <variable> ::= <identificador> | <identificador> "[" <lista_expresiones> "]"
    private void variable() {
        identificador();
        if (currentToken.getLexema().equals("[")) {
            match("[");
            listaExpresiones();
            match("]");
        }
    }

    // <llamada_funcion_o_proc> ::= <identificador> "(" <lista_expresiones> ")"
    private void llamadaFuncionOProc() {
        identificador();
        match("(");
        listaExpresiones();
        match(")");
    }

    // <condicional_if> ::= "if" "(" <expresion> ")" "then" <bloque_if> <opcional_else>
    private void condicionalIf() {
        match("if");
        match("(");
        expresion();
        match(")");
        match("then");
        bloqueIf();
        opcionalElse();
    }

    // <opcional_else> ::= "else" <bloque_if> | ε
    private void opcionalElse() {
        if (currentToken.getLexema().equals("else")) {
            match("else");
            bloqueIf();
        }
        // ε (epsilon) - no hacer nada
    }

    // <bloque_if> ::= "begin" <instrucciones> "end"
    private void bloqueIf() {
        match("begin");
        instrucciones();
        match("end");
    }

    // <bucle_while> ::= "while" "(" <expresion> ")" "do" <bloque_if>
    private void bucleWhile() {
        match("while");
        match("(");
        expresion();
        match(")");
        match("do");
        bloqueIf();
    }

    // <bucle_repeat> ::= "repeat" <bloque_if> "until" "(" <expresion> ")"
    private void bucleRepeat() {
        match("repeat");
        bloqueIf();
        match("until");
        match("(");
        expresion();
        match(")");
    }

    // <entrada_salida> ::= "write" "(" <expresion> ")" | "read" "(" <variable> ")"
    private void entradaSalida() {
        if (currentToken.getLexema().equals("write")) {
            match("write");
            match("(");
            expresion();
            match(")");
        } else if (currentToken.getLexema().equals("read")) {
            match("read");
            match("(");
            variable();
            match(")");
        } else {
            error("'write' o 'read'");
        }
    }

    // <expresion> ::= <expresion_simple> <op_relacional> <expresion_simple> | <expresion_simple>
    private void expresion() {
        expresionSimple();
        if (esOperadorRelacional(currentToken.getLexema())) {
            opRelacional();
            expresionSimple();
        }
    }

    // <expresion_simple> ::= <termino> | <expresion_simple> <op_aditivo> <termino>
    private void expresionSimple() {
        termino();
        while (esOperadorAditivo(currentToken.getLexema())) {
            opAditivo();
            termino();
        }
    }

    // <termino> ::= <factor> | <termino> <op_multiplicativo> <factor>
    private void termino() {
        factor();
        while (esOperadorMultiplicativo(currentToken.getLexema())) {
            opMultiplicativo();
            factor();
        }
    }

    // <factor> ::= <numero> | <variable> | <constante_booleana> | <cadena> | "(" <expresion> ")" | "-" <factor>
    private void factor() {
        if (currentToken.getCodigo() == -61 || currentToken.getCodigo() == -62) {
            // <numero>
            numero();
        } else if (currentToken.getCodigo() == -51) {
            // <variable>
            variable();
        } else if (currentToken.getLexema().equals("true") || 
                   currentToken.getLexema().equals("false")) {
            // <constante_booleana>
            constanteBooleana();
        } else if (currentToken.getCodigo() == -63) {
            // <cadena>
            cadena();
        } else if (currentToken.getLexema().equals("(")) {
            // "(" <expresion> ")"
            match("(");
            expresion();
            match(")");
        } else if (currentToken.getLexema().equals("-")) {
            // "-" <factor>
            match("-");
            factor();
        } else {
            error("un factor válido");
        }
    }

    // <lista_expresiones> ::= <expresion> | <expresion> "," <lista_expresiones>
    private void listaExpresiones() {
        expresion();
        if (currentToken.getLexema().equals(",")) {
            match(",");
            listaExpresiones();
        }
    }

    // <op_relacional> ::= "==" | "!=" | "<" | "<=" | ">" | ">="
    private void opRelacional() {
        if (esOperadorRelacional(currentToken.getLexema())) {
            advance();
        } else {
            error("un operador relacional");
        }
    }

    // <op_aditivo> ::= "+" | "-"
    private void opAditivo() {
        if (currentToken.getLexema().equals("+") || 
            currentToken.getLexema().equals("-")) {
            advance();
        } else {
            error("'+' o '-'");
        }
    }

    // <op_multiplicativo> ::= "*" | "/"
    private void opMultiplicativo() {
        if (currentToken.getLexema().equals("*") || 
            currentToken.getLexema().equals("/")) {
            advance();
        } else {
            error("'*' o '/'");
        }
    }

    // <constante_booleana> ::= "true" | "false"
    private void constanteBooleana() {
        if (currentToken.getLexema().equals("true") || 
            currentToken.getLexema().equals("false")) {
            advance();
        } else {
            error("'true' o 'false'");
        }
    }

    // <cadena> ::= "\"" <caracteres> "\""
    private void cadena() {
        if (currentToken.getCodigo() == -63) {
            advance();
        } else {
            error("una cadena");
        }
    }

    // <identificador> ::= <letra> { <letra> | <digito> }*
    private void identificador() {
        if (currentToken.getCodigo() == -51) {
            advance();
        } else {
            error("un identificador");
        }
    }

    // <numero> ::= <entero> | <real>
    private void numero() {
        if (currentToken.getCodigo() == -61 || currentToken.getCodigo() == -62) {
            advance();
        } else {
            error("un número");
        }
    }

    // <entero> ::= <digito>+
    private void entero() {
        if (currentToken.getCodigo() == -61) {
            advance();
        } else {
            error("un número entero");
        }
    }

    // Métodos auxiliares para verificar tipos de operadores
    private boolean esOperadorRelacional(String lexema) {
        return lexema.equals("==") || lexema.equals("!=") || 
               lexema.equals("<") || lexema.equals("<=") || 
               lexema.equals(">") || lexema.equals(">=");
    }

    private boolean esOperadorAditivo(String lexema) {
        return lexema.equals("+") || lexema.equals("-");
    }

    private boolean esOperadorMultiplicativo(String lexema) {
        return lexema.equals("*") || lexema.equals("/");
    }

}

