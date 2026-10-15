package View;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginView {
    private JPanel panel1;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton button1;
    private JButton button2;

    public LoginView() {
        button1.addActionListener(e -> entrar());
        button2.addActionListener(e -> cadastrar());
    }


    private void entrar() {
        String email = textField1.getText().trim();
        char[] password = passwordField1.getPassword();

        if (email.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(
                    panel1,
                    "Preenche o email e a palavra-passe.",
                    "Campos em falta",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(
                    panel1,
                    "Introduz um endereço de email válido.",
                    "Email inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Aqui deverás verificar os dados com a tua base de dados ou serviço.
        JOptionPane.showMessageDialog(
                panel1,
                "Os dados foram preenchidos. Falta ligar a autenticação."
        );

        Arrays.fill(password, '\0');
    }

    private void cadastrar() {
        // Aqui deverás abrir o ecrã de registo ou guardar o novo utilizador.
        JOptionPane.showMessageDialog(
                panel1,
                "Ainda falta implementar o registo de utilizadores."
        );
    }

    public JPanel getPanel() {
        return panel1;
    }

}




