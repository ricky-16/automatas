package Unidad5;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class AnalizadorSintacticoMain {
    public static void main(String[] args) {
        try {
            // Leer la tabla de tokens generada en la práctica anterior
            List<Token> tokens = leerTablaTokens("C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Tabla de Tokens.txt");
            
            // Crear el analizador sintáctico
            AnalizadorSintactico analizador = new AnalizadorSintactico(tokens);
            
            // Realizar el análisis sintáctico
            boolean resultado = analizador.analizar();
            
            // Mostrar el resultado
            if (resultado) {
                JOptionPane.showMessageDialog(null, "Análisis sintáctico completado con éxito. La estructura del programa es correcta.");
                escribirResultado("C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Resultado Sintactico.txt", 
                                "Análisis sintáctico completado con éxito. La estructura del programa es correcta.");
            } else {
                String errores = analizador.getErrores();
                JOptionPane.showMessageDialog(null, "Se encontraron errores sintácticos:\n" + errores);
                escribirResultado("C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Resultado Sintactico.txt", 
                                "Se encontraron errores sintácticos:\n" + errores);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al realizar el análisis sintáctico: " + e.getMessage());
        }
    }
    
    // Método para leer la tabla de tokens
    private static List<Token> leerTablaTokens(String rutaArchivo) throws IOException {
        List<Token> tokens = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
        String linea;
        
        while ((linea = reader.readLine()) != null) {
            // Formato esperado: "Token{tipo=-1, valor='program', linea=1}"
            // Extraer los valores usando expresiones regulares o split
            try {
                int tipo = extraerTipo(linea);
                String valor = extraerValor(linea);
                int numLinea = extraerLinea(linea);
                
                tokens.add(new Token(tipo, valor, numLinea));
            } catch (Exception e) {
                System.out.println("Error al parsear la línea: " + linea);
            }
        }
        
        reader.close();
        return tokens;
    }
    
    // Métodos auxiliares para extraer información de la línea de token
    private static int extraerTipo(String linea) {
        // Extraer el tipo del token
        int inicio = linea.indexOf("tipo=") + 5;
        int fin = linea.indexOf(",", inicio);
        return Integer.parseInt(linea.substring(inicio, fin));
    }
    
    private static String extraerValor(String linea) {
        // Extraer el valor del token
        int inicio = linea.indexOf("valor='") + 7;
        int fin = linea.indexOf("'", inicio);
        return linea.substring(inicio, fin);
    }
    
    private static int extraerLinea(String linea) {
        // Extraer el número de línea
        int inicio = linea.indexOf("linea=") + 6;
        int fin = linea.indexOf("}", inicio);
        return Integer.parseInt(linea.substring(inicio, fin));
    }
    
    // Método para escribir el resultado del análisis
    private static void escribirResultado(String rutaArchivo, String contenido) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo));
            writer.write(contenido);
            writer.close();
            System.out.println("Resultado guardado en: " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al escribir el resultado: " + e.getMessage());
        }
    }
}
