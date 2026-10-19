

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
        // Inicializar os componentes manualmente
        panel1 = new JPanel(new GridLayout(5, 1, 5, 5));
        panel1.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        textField1 = new JTextField();
        passwordField1 = new JPasswordField();
        button1 = new JButton("Entrar");
        button2 = new JButton("Cadastrar");

        // Construir o formulário
        panel1.add(new JLabel("Email"));
        panel1.add(textField1);
        panel1.add(new JLabel("Palavra-passe"));
        panel1.add(passwordField1);

        JPanel botoes = new JPanel(new FlowLayout());
        botoes.add(button1);
        botoes.add(button2);
        panel1.add(botoes);

        // Eventos
        button1.addActionListener(e -> entrar());
        button2.addActionListener(e -> cadastrar());
    }

    private void entrar() {
        String email = textField1.getText().trim();
        char[] password = passwordField1.getPassword();

        try {
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

            JOptionPane.showMessageDialog(
                    panel1,
                    "Dados preenchidos. Falta ligar a autenticação."
            );
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void cadastrar() {
        JOptionPane.showMessageDialog(
                panel1,
                "Ainda falta implementar o registo de utilizadores."
        );
    }

    public JPanel getPanel() {
        return panel1;
    }
}
