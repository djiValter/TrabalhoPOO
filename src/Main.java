import database.DatabaseInitializer;
import view.LoginView;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.criarAdminPadrao();

            SwingUtilities.invokeLater(LoginView::new);

        } catch (SQLException e) {
            e.printStackTrace();

            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            "Não foi possível inicializar o sistema.\n"
                                    + "Verifique a ligação ao PostgreSQL, "
                                    + "o driver JDBC e a tabela usuarios.\n\n"
                                    + e.getMessage(),
                            "Erro de inicialização",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        }
    }
}