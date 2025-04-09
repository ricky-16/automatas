package Unidad1;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.*;

public class AppArticulo {
	private static final String ARCHIVO = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad1\\Articulos";
    private static ArrayList<Articulo> articulos = new ArrayList<>();

    private static void verificarArchivo() {
        File archivo = new File(ARCHIVO);
        boolean archivoVacio = !archivo.exists() || archivo.length() == 0;
        if (archivoVacio) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO, false))) {
                writer.println("Nombre,Existencia,Costo");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al crear el archivo.");
            }
        }
    }
    private static void ingresarArticulos() {
        int seguir;
        do {
            try {
                String nombre = JOptionPane.showInputDialog("Ingrese el nombre del artículo").trim();
                int existencia = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad en existencia"));
                double costo = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el costo del artículo"));

                if (existencia < 0 || costo < 0) {
                    JOptionPane.showMessageDialog(null, "Error: La existencia y el costo deben ser valores positivos.");
                } else {
                    Articulo nuevoArticulo = new Articulo(nombre, existencia,costo);
                    articulos.add(nuevoArticulo);
                    guardarArticuloEnArchivo(nuevoArticulo);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Error: Ingrese valores numéricos válidos.");
            }
            seguir = JOptionPane.showConfirmDialog(null, "¿Desea capturar otro artículo?");
        } while (seguir == JOptionPane.YES_OPTION);
    }

    private static void guardarArticuloEnArchivo(Articulo articulo) {
        try (FileWriter fw = new FileWriter(ARCHIVO, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Escribir los datos del artículo
            out.println(articulo.toFileString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el archivo.");
        }
    }

    private static void mostrarArticulos() {
        articulos.clear();
        leerArticulosDesdeArchivo();

        if (articulos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay artículos para mostrar.");
        } else {
            StringBuilder mensaje = new StringBuilder("Artículos:\n\n");
            for (Articulo articulo : articulos) {
                mensaje.append(articulo.toString()).append("\n");
            }
            JOptionPane.showMessageDialog(null, mensaje.toString());
        }
    }

    private static void leerArticulosDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            boolean primeraLinea = true; // Para saltar la cabecera

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { 
                    primeraLinea = false; // Saltar la cabecera
                    continue;
                }
                Articulo articulo = Articulo.fromFileString(linea);
                if (articulo != null) {
                    articulos.add(articulo);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo.");
        }
    }

    public static void main(String[] args) {
        verificarArchivo(); // Asegura que la cabecera se escriba antes de empezar

        Object[] opciones = { "1.- Ingresar artículos", "2.- Mostrar artículos", "3.- Salir" };
        String tipo = "";
        do {
            tipo = (String) JOptionPane.showInputDialog(null, "Selecciona una opción", "Menú",
                    JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
            if (tipo == null) {
                tipo = "3.- Salir";
            }
            switch (tipo) {
                case "1.- Ingresar artículos":
                    ingresarArticulos();
                    break;
                case "2.- Mostrar artículos":
                    mostrarArticulos();
                    break;
                case "3.- Salir":
                    break;
            }
        } while (!tipo.equals("3.- Salir"));
    }
} 