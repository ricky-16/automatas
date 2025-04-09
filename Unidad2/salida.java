package Unidad2;


import javax.swing.*;

public class salida {

    // Método para mostrar el resultado con scroll
    public static void imprimeConScroll(String mensaje, String titulo) {
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(mensaje);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}
