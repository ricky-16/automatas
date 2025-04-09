package Unidad2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class APP {
    public static void main(String[] args) {
        Object[] opciones = {
                "1.- Escribir cadena", 
                "2.- Validar Cadena con java", 
                "3.- Validar Cadena con PHYTON", 
                "4.- Salir"
            };

        String tipo = "";

       
        do {
            tipo = (String) JOptionPane.showInputDialog(null, "Selecciona una opción", "Menú",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
            if (tipo == null) {
                tipo = "4.- Salir";
            }

            switch (tipo) {
                case "1.- Escribir cadena":
                    String input = JOptionPane.showInputDialog("Ingresa una cadena para validar:");
                    if (input != null) {
                        ingresarPalabras(input);  
                    }
                    break;

                case "2.- Validar Cadena con java":
                    realizarValidacion(1);
                    break;

                case "3.- Validar Cadena con PHYTON":
                    realizarValidacion(2);
                    break;

                case "4.- Salir":
                    JOptionPane.showMessageDialog(null, "Gracias por usar el programa");
                    break;
            }
        } while (!tipo.equals("4.- Salir"));
    }

    static List<String> palabrasDeEntrada = new ArrayList<>();
    static List<String> palabrasValidadas = new ArrayList<>();
    static List<String> palabrasNoValidas = new ArrayList<>();

    
    private static void ingresarPalabras(String input) {
        if (input != null && !input.isEmpty()) {
            palabrasDeEntrada = new ArrayList<>(Arrays.asList(input.split("\\s+")));
        } else {
            JOptionPane.showMessageDialog(null, "No se ingresaron palabras.");
        }
    }

    // Método de validación con diferentes expresiones regulares dependiendo del caso
    private static void realizarValidacion(int opcion) {
        // Limpiar las listas antes de comenzar la comparación
        palabrasValidadas.clear();
        palabrasNoValidas.clear();

        switch (opcion) {
            case 1:
                // Validación de números enteros y reales en Java
                String expresionRegular1 = "^-?\\d+(\\.\\d+)?$";
                Pattern pattern1 = Pattern.compile(expresionRegular1);

                for (String palabra : palabrasDeEntrada) {
                    boolean coincidencia = pattern1.matcher(palabra).matches();
                    if (coincidencia) {
                        palabrasValidadas.add(palabra);
                    } else {
                        palabrasNoValidas.add(palabra);
                    }
                }

                mostrarResultados("Validación de Números", palabrasValidadas, palabrasNoValidas);
                break;

            case 2:
                // Expresión regular para detectar constantes String en Python
                String expresionRegular2 = "^(\"[^\"]*\"|'[^']*'|\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?''')$";
                Pattern pattern2 = Pattern.compile(expresionRegular2);

                for (String palabra : palabrasDeEntrada) {
                    boolean coincidencia = pattern2.matcher(palabra).matches();
                    if (coincidencia) {
                        palabrasValidadas.add(palabra);
                    } else {
                        palabrasNoValidas.add(palabra);
                    }
                }

                mostrarResultados("Validación de Constantes String en Python", palabrasValidadas, palabrasNoValidas);
                break;
        }
    }
        // Método para mostrar los resultados de las validaciones
        private static void mostrarResultados(String lenguaje, List<String> validadas, List<String> noValidas) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Palabras que cumplen con el ").append(lenguaje).append(":\n");
        for (String palabra : validadas) {
            resultado.append("- ").append(palabra).append("\n");
        }
        resultado.append("\nPalabras que NO cumplen con el ").append(lenguaje).append(":\n");
        for (String palabra : noValidas) {
            resultado.append("- ").append(palabra).append("\n");
        }

        JOptionPane.showMessageDialog(null, resultado.toString(), "Resultados de " + lenguaje, 
        		JOptionPane.INFORMATION_MESSAGE);
    }
}
