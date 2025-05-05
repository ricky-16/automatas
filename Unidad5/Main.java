package Unidad5;

public class Main {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Tabla de Tokens.txt";
        
        System.out.println("Analizando archivo: " + filePath);
        
        TokenAnalyzer analyzer = new TokenAnalyzer(filePath);
        boolean isValid = analyzer.analyze();
        
        if (isValid) {
            System.out.println("¡Análisis sintáctico exitoso! La estructura del programa es correcta.");
        } else {
            System.out.println("Análisis sintáctico fallido. Se encontraron errores:");
            System.out.println(analyzer.getErrorMessages());
        }
    }
}
