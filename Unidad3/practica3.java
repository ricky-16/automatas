package Unidad3;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class practica3 {
    public static void main(String[] args) {
        Object[] opciones = {"1.- Leer Archivo", "2.- Ver Tabla de Palabras Válidas", "3.- Salir"};
        String tipo;

        // Limpiar los archivos de salida al inicio
        limpiarArchivoSalida("Salida.txt");
        limpiarArchivoSalida("Invalidas.txt");

        do {
            tipo = (String) JOptionPane.showInputDialog(null, "Selecciona una opción", "Menú",
                    JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
            if (tipo == null) tipo = "3.- Salir";

            switch (tipo) {
                case "1.- Leer Archivo":
                    procesarArchivo();
                    JOptionPane.showMessageDialog(null, "Archivo leído, revisa salida.txt e invalidas.txt");
                    break;
                case "2.- Ver Tabla de Palabras Válidas":
                    mostrarTabla();
                    break;
                case "3.- Salir":
                    JOptionPane.showMessageDialog(null, "programa finalizado");
                    break;
            }
        } while (!tipo.equals("3.- Salir"));
    }
    
    // AFD para validar el Lenguaje 3
    public static boolean validarLenguaje3(String cadena) {
        int estado = 0;

        for (int i = 0; i < cadena.length(); i++) {
            char c = cadena.charAt(i);

            switch (estado) {
                case 0: // Estado inicial, espera [A-Z] o '&' o '%'
                    if (Character.isUpperCase(c) || c == '&' || c == '%') {
                        estado = 1; // Transición al estado 1
                    } else {
                        return false;
                    }
                    break;
                case 1: // Estado 1, puede seguir con [A-Z] o '&' o '%' o pasar a la siguiente parte
                    if (Character.isUpperCase(c) || c == '&' || c == '%') {
                        estado = 1; // Sigue en el mismo estado
                    } else if (Character.isLowerCase(c) || Character.isDigit(c) || c == '_') {
                        estado = 2; // Transición al estado 2
                    } else {
                        return false;
                    }
                    break;
                case 2: // Estado 2, solo acepta [A-Z] [a-z] _ [0-9]
                    if (Character.isUpperCase(c) || Character.isLowerCase(c) || Character.isDigit(c) || c == '_') {
                        estado = 2; // Permanece en el estado 2
                    } else {
                        return false;
                    }
                    break;
            }
        }
         return estado == 1 || estado == 2;
    }
    public static boolean validarCadena(String cadena) {
        int estado = 0;

        for (int i = 0; i < cadena.length(); i++) {
            char c = cadena.charAt(i);

            switch (estado) {
                case 0:
                    if (Character.isDigit(c)) {
                        estado = 1;
                    } else {
                        return false;
                    }
                    break;
                case 1:
                    if (Character.isDigit(c)) {
                        estado = 1;
                    } else if (Character.isLetter(c) || c == '&') {
                        estado = 2;
                    } else if (c == '/') {
                        estado = 3;
                    } else {
                        return false;
                    }
                    break;
                case 2:
                    if (Character.isLetter(c) || c == '&') {
                        estado = 2;
                    } else if (c == '/') {
                        estado = 3;
                    } else {
                        return false;
                    }
                    break;
                case 3:
                    return false;
            }
        }

        return estado == 1 || estado == 2 || estado == 3;
    }
    public static void limpiarArchivoSalida(String nombreArchivo) {
        String ruta = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad3\\" + nombreArchivo;
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al limpiar el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }

    public static List<String[]> procesarArchivo() {
        String entrada = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad3\\Entrada.txt";
        String salida = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad3\\Salida.txt";
        String invalidas = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad3\\Invalidas.txt";

        List<String[]> palabrasValidas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(entrada));
             BufferedWriter bwValidas = new BufferedWriter(new FileWriter(salida, true));
             BufferedWriter bwInvalidas = new BufferedWriter(new FileWriter(invalidas, true))) {
            
            File file = new File(salida);
            if (file.length() == 0) {
                bwValidas.write("CADENA,LENGUAJE\n");
            }
            
            file = new File(invalidas);
            if (file.length() == 0) {
                bwInvalidas.write("CADENA,LENGUAJE\n");
            }

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] palabras = linea.split("\\s+");
                
                for (String palabra : palabras) {
                    if (!palabra.isEmpty()) {
                        boolean esLenguaje1 = validarCadena(palabra);
                        boolean esLenguaje3 = validarLenguaje3(palabra);
                        String lenguaje;
                        
                        if (esLenguaje1) {
                            lenguaje = "Lenguaje1";
                        } else if (esLenguaje3) {
                            lenguaje = "Lenguaje3";
                        } else {
                            lenguaje = "Invalido";
                        }
                        
                        String resultadoLinea = palabra + "," + lenguaje + "\n";
                        
                        if (esLenguaje1 || esLenguaje3) {
                            palabrasValidas.add(new String[]{palabra, lenguaje});
                            bwValidas.write(resultadoLinea);
                        } else {
                            bwInvalidas.write(resultadoLinea);
                        }
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer/escribir archivos: " + e.getMessage());
        }

        return palabrasValidas;
    }

public static void mostrarTabla() {
    List<String[]> palabrasValidas = procesarArchivo();

    if (palabrasValidas.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No se encontraron palabras válidas.");
    } else {
        StringBuilder tabla = new StringBuilder("CADENA,LENGUAJE\n");
        for (String[] palabraInfo : palabrasValidas) {
            tabla.append(palabraInfo[0]).append(",").append(palabraInfo[1]).append("\n");
        }
        JOptionPane.showMessageDialog(null, tabla.toString());
    }
}
}