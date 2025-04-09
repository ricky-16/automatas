package Unidad1;

public class Articulo {
    private String nombre;
    private int existencia;
    private double costo;

    public Articulo(String nombre, int existencia, double costo) {
        this.nombre = nombre;
        this.existencia = existencia;
        this.costo = costo;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getExistencia() {
        return existencia;
    }

    public double getCosto() {
        return costo;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setExistencia(int existencia) {
        if (existencia >= 0) {
            this.existencia = existencia;
        } else {
            System.out.println("Error: La existencia no puede ser negativa.");
        }
    }

    public void setCosto(double costo) {
        if (costo >= 0) {
            this.costo = costo;
        } else {
            System.out.println("Error: El costo no puede ser negativo.");
        }
    }	

    // Convertir el objeto a un formato de texto para el archivo
    public String toFileString() {
        return nombre + "," + existencia + "," + costo;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Existencia: " + existencia + ", Costo: $" + costo;
    }

    // Crear un objeto Articulo desde una línea de texto del archivo
    public static Articulo fromFileString(String linea) {
        String[] datos = linea.split(",");
        if (datos.length == 3) {
            return new Articulo(datos[0], Integer.parseInt(datos[1]), Double.parseDouble(datos[2]));
        }
        return null;
    }
}
