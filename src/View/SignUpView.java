package View;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SignUpView {
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JPasswordField passwordField1;
    private JCheckBox checkBox1;
    private JButton cadastrarButton;
    private JButton voltarButton;

    public SignUpView() {
        // A checkbox mostra ou oculta a palavra-passe
        char caracterOriginal = passwordField1.getEchoChar();

        checkBox1.addActionListener(e -> {
            if (checkBox1.isSelected()) {
                passwordField1.setEchoChar((char) 0);
            } else {
                passwordField1.setEchoChar(caracterOriginal);
            }
        });

        cadastrarButton.addActionListener(e -> cadastrar());
        voltarButton.addActionListener(e -> voltar());
    }

    private void cadastrar() {
        String nome = textField1.getText().trim();
        String email = textField2.getText().trim();
        char[] senha = passwordField1.getPassword();

        try {
            if (nome.isEmpty() || email.isEmpty() || senha.length == 0) {
                mostrarAviso("Preenche o nome, o email e a palavra-passe.");
                return;
            }

            if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                mostrarAviso("Introduz um endereço de email válido.");
                return;
            }

            if (senha.length < 8) {
                mostrarAviso("A palavra-passe deve ter pelo menos 8 caracteres.");
                return;
            }

            // Aqui deves guardar o utilizador na base de dados.
            // Neste exemplo, os dados são apenas validados.
            JOptionPane.showMessageDialog(
                    panel1,
                    "Dados validados com sucesso.\\n"
                            + "Falta ligar o cadastro à base de dados.",
                    "Cadastro",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } finally {
            // Limpa a cópia da palavra-passe da memória
            Arrays.fill(senha, '\0');
        }
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(
                panel1,
                mensagem,
                "Verifica os dados",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void voltar() {
        Window janela = SwingUtilities.getWindowAncestor(panel1);

        if (janela != null) {
            janela.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignUpView view = new SignUpView();

            JFrame frame = new JFrame("Criar conta");
            frame.setContentPane(view.panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


}
