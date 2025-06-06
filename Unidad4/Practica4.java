package Unidad4;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;
import java.util.List;

public class Practica4 {

	public static void main(String[] args) {
		if (iniciarProceso()) {
			JOptionPane.showMessageDialog(null, "Proceso completado con éxito.");
		} else {
			JOptionPane.showMessageDialog(null, "Hubo un problema al procesar el archivo.");
		}
	}

	static String nombreArchivo = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Entrada.txt";

	private static boolean archivoVerificarContenido() {
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
			return br.readLine() != null;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + e.getMessage());
			return false;
		}
	}

	private static boolean cargarArchivo() {
	    if (!archivoVerificarContenido()) {
	        JOptionPane.showMessageDialog(null, "El archivo está vacío o no existe.");
	        return false;
	    }

	    try {
	         List<String> lineas = Files.readAllLines(Paths.get(nombreArchivo));

	       
	        for (int i = 0; i < lineas.size(); i++) {
	            String linea = lineas.get(i);
	            int lineaActual = i + 1;
	            Lectura(linea, lineaActual);
	        }

	        JOptionPane.showMessageDialog(null, "Archivo leído correctamente.");
	        return true;

	    } catch (IOException e) {
	        JOptionPane.showMessageDialog(null, "Error de lectura: " + e.getMessage());
	        return false;
	    }
	}
	private static boolean iniciarProceso() {
		if (!cargarArchivo()) {
			JOptionPane.showMessageDialog(null, "No se pudo cargar el archivo.");
			return false;
		}

		try {
			analizarTokens();
			Errores();
			escribirArchivo();
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
			return false;
		}
	}

	private static void analizarTokens() {
		for (Datos token : palabrasArchivo) {
			identificarPalabraReservada(token);
			identificarIdentificadores(token);
			identificarCaracter(token);
			identificarEntero(token);
			identificarDecimal(token);
			identificarCadena(token);
			identificarComentario(token);
			identificarOperadorAritmetico(token);
			identificarOperadorRelacional(token);
			identificarOperadorLogico(token);
		}
	}

	private static void Lectura(String linea, int numLinea) {
		// Modificado para capturar comentarios completos o incompletos
		String patron = "/\\*.*?\\*/|/\\*.*|\".*?\"|&&|\\|\\||!|==|!=|<=|>=|<|>|\\+|-|\\*|/|%|=|[(),;:\\[\\]]|-?\\d+\\.\\d+|-?\\d+|"
				+ "[a-z][a-zA-Z0-9_]*|\\s+";
		Pattern regex = Pattern.compile(patron);
		Matcher coincidencia = regex.matcher(linea);

		int posicionActual = 0;

		while (coincidencia.find()) {
			if (coincidencia.start() > posicionActual) {
				String textoNoCoincide = linea.substring(posicionActual, coincidencia.start()).trim();
				if (!textoNoCoincide.isEmpty()) {
					palabrasArchivo.addLast(new Datos(textoNoCoincide, numLinea));
				}
			}
			
			String token = coincidencia.group().trim();
			if (token.isEmpty()) continue;
			  
			if (!token.matches("\\s+")) {
				palabrasArchivo.addLast(new Datos(token, numLinea));
				System.out.println("TOKEN EXTRAÍDO: " + token);
			}

			posicionActual = coincidencia.end();
		}

		if (posicionActual < linea.length()) {
			String textoRestante = linea.substring(posicionActual).trim();
			if (!textoRestante.isEmpty()) {
				palabrasArchivo.addLast(new Datos(textoRestante, numLinea));
			}
		}
	}

	private static void identificarIdentificadores(Datos token) {
		String identificador = token.getPalabra();
		if (identificador.matches("[a-z][a-zA-Z0-9_]*")) {
			token.setId(-2);
			token.setToken(-51);
		}
	}

	private static void identificarCaracter(Datos token) {
		String palabra = token.getPalabra();
		if (palabra.matches("[(),;:\\[\\]]")) {
			token.setId(-1);
			switch (palabra) {
			case "(" -> token.setToken(-71);
			case ")" -> token.setToken(-72);
			case ";" -> token.setToken(-73);
			case "," -> token.setToken(-74);
			case ":" -> token.setToken(-75);
			case "[" -> token.setToken(-76);
			case "]" -> token.setToken(-77);
			}
		}
	}

	private static void identificarEntero(Datos token) {
		if (token.getPalabra().matches("-?\\d+")) {
			token.setId(-1);
			token.setToken(-61);
		}
	}

	private static void identificarDecimal(Datos token) {
		if (token.getPalabra().matches("-?\\d+\\.\\d+")) {
			token.setId(-1);
			token.setToken(-62);
		}
	}

	private static void identificarCadena(Datos token) {
		if (token.getPalabra().matches("\".*?\"")) {
			token.setId(-1);
			token.setToken(-63);
		}
	}

	private static void identificarComentario(Datos token) {
		// Identificar comentarios completos
		if (token.getPalabra().matches("/\\*.*?\\*/")) {
			token.setId(-1);
			token.setToken(-100);
		}
		// Identificar comentarios incompletos
		else if (token.getPalabra().matches("/\\*.*")) {
			token.setId(-1);
			// No asignar token para que se considere un error
		}
	}

	private static void identificarOperadorAritmetico(Datos token) {
		String op = token.getPalabra();
		if (op.matches("\\+|-|\\*|/|%|=")) {
			token.setId(-1);
			switch (op) {
			case "+" -> token.setToken(-26);
			case "-" -> token.setToken(-27);
			case "*" -> token.setToken(-23);
			case "/" -> token.setToken(-24);
			case "%" -> token.setToken(-25);
			case "=" -> token.setToken(-32);
			}
		}
	}

	private static void identificarOperadorRelacional(Datos token) {
		String op = token.getPalabra();
		if (op.matches("==|!=|<=|>=|<|>")) {
			token.setId(-1);
			switch (op) {
			case "==" -> token.setToken(-28);
			case "!=" -> token.setToken(-29);
			case "<=" -> token.setToken(-34);
			case ">=" -> token.setToken(-36);
			case "<" -> token.setToken(-33);
			case ">" -> token.setToken(-35);
			}
		}
	}

	private static void identificarPalabraReservada(Datos token) {
	    String pal = token.getPalabra();

	    if (pal.matches(
	            "program|begin|end|read|write|int|real|string|bool|if|else|then|while|do|repeat|until|var|procedure|function|"
	            + "array|true|false")) {
	        token.setId(-1);

	        switch (pal) {
	        case "program" -> token.setToken(-1);
	        case "begin" -> token.setToken(-2);
	        case "end" -> token.setToken(-3);
	        case "read" -> token.setToken(-4);
	        case "write" -> token.setToken(-5);
	        case "int" -> token.setToken(-6);
	        case "real" -> token.setToken(-7);
	        case "string" -> token.setToken(-8);
	        case "bool" -> token.setToken(-9);
	        case "if" -> token.setToken(-10);
	        case "else" -> token.setToken(-11);
	        case "then" -> token.setToken(-12);
	        case "while" -> token.setToken(-13);
	        case "do" -> token.setToken(-14);
	        case "repeat" -> token.setToken(-15);
	        case "until" -> token.setToken(-16);
	        case "var" -> token.setToken(-17);
	        case "procedure" -> token.setToken(-18);
	        case "function" -> token.setToken(-19);
	        case "array" -> token.setToken(-20);
	        case "true" -> token.setToken(-21);
	        case "false" -> token.setToken(-22);
	        }
	    }
	}

	private static void identificarOperadorLogico(Datos token) {
	    String op = token.getPalabra();
	    if (op.matches("&&|\\|\\||!")) {
	        token.setId(-1);
	        switch (op) {
	        case "&&" -> token.setToken(-30);
	        case "||" -> token.setToken(-31);
	        case "!" -> token.setToken(-37);
	        }
	    }
	}


	static LinkedList<Datos> palabrasArchivo = new LinkedList<>();
	static LinkedList<Datos> erroresArchivo = new LinkedList<>();

	public static void Errores() {
		for (Datos palabra : palabrasArchivo) {
			// Detectar comentarios incompletos
			if (palabra.getPalabra().matches("/\\*.*") && !palabra.getPalabra().matches("/\\*.*?\\*/")) {
				erroresArchivo.add(new Datos(
					palabra.getPalabra(),
					palabra.getToken(),
					palabra.getId(),
					palabra.getPos(),
					palabra.getLineaSintaxis()
				));
			}
			// Detectar cadenas incompletas
			else if (palabra.getPalabra().matches("\"[^\"\n\r]*") && !palabra.getPalabra().matches("\".*?\"")) {
				erroresArchivo.add(new Datos(
					palabra.getPalabra(),
					palabra.getToken(),
					palabra.getId(),
					palabra.getPos(),
					palabra.getLineaSintaxis()
				));
			}
			// Detectar los caracteres específicos que queremos como errores
			else if (palabra.getToken() == 0 && !palabra.getPalabra().trim().isEmpty()) {
				String[] errorCharacters = {"D", "$", "I", "."};
				boolean isErrorChar = false;
				for (String errorChar : errorCharacters) {
					if (palabra.getPalabra().equals(errorChar)) {
						isErrorChar = true;
						break;
					}
				}
				
				if (isErrorChar) {
					erroresArchivo.add(new Datos(
						palabra.getPalabra(),
						palabra.getToken(),
						palabra.getId(),
						palabra.getPos(),
						palabra.getLineaSintaxis()
					));
				}
			}
		}
	}

	private static void escribirArchivo() {
		try {
			String rutaTokens = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Tabla de Tokens.txt";
			String rutaErrores = "C:\\Users\\Ricardo\\eclipse-workspace\\Automatas 1\\src\\Unidad4\\Tabla de Errores.txt";

			BufferedWriter writerTokens = new BufferedWriter(new FileWriter(rutaTokens));
			BufferedWriter writerErrores = new BufferedWriter(new FileWriter(rutaErrores));

			for (Datos datos : palabrasArchivo) {
				if (datos.getToken() != 0) {
					writerTokens.write(datos.toString());
					writerTokens.newLine();
				}
			}

			for (Datos error : erroresArchivo) {
				String tipoError;
				String detalleError = "";
				String palabra = error.getPalabra();

				if (palabra.matches("[{}()\\[\\];,:]")) {
					tipoError = "Caracter invalido";
				} else if (palabra.matches("\"[^\n\r]*") && !palabra.matches("\".*?\"")) {
					tipoError = "Cadena faltante";
					detalleError = " - Falta comilla de cierre (\")";
				} else if (palabra.matches("/\\*.*") && !palabra.matches("/\\*.*?\\*/")) {
					tipoError = "Comentario faltante";
					
				// Se eliminó el detalle específico para cada carácter
				} else if (palabra.matches("[^a-zA-Z0-9_\\s]")) {
					tipoError = "Caracter invalido";
				} else if (palabra.matches("[A-Z]")) {
					tipoError = "Caracter invalido";
				} else {
					tipoError = "Error desconocido";
				}

				writerErrores.write(tipoError + ": " + palabra + detalleError + ", " + error.getPos());
				writerErrores.newLine();
			}

			writerTokens.close();
			writerErrores.close();

			JOptionPane.showMessageDialog(null, "Archivos generados exitosamente.");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al escribir los archivos: " + e.getMessage());
		}
	}
}
