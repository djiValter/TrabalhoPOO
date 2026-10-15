import View.LoginView;
import View.SignUpView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            SignUpView signUpView = new SignUpView();

            JTabbedPane separadores = new JTabbedPane();
            separadores.addTab("Login", loginView.getPanel());
            separadores.addTab("Criar conta", signUpView.getPanel());

            JFrame janela = new JFrame("Teste das telas");
            janela.setContentPane(separadores);
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setSize(500, 450);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
