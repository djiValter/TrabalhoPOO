package view;

import controller.AuthController;
import model.Admin;
import model.Cliente;
import model.Promotor;
import model.Utilizador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class LoginView extends JFrame {

    private static final Color AZUL = new Color(58, 86, 225);
    private static final Color AZUL_HOVER = new Color(43, 65, 185);
    private static final Color TEXTO = new Color(30, 41, 59);
    private static final Color CINZENTO = new Color(100, 116, 139);
    private static final Color BORDA = new Color(218, 225, 235);
    private static final Color FUNDO_CAMPO = new Color(248, 250, 252);

    private final AuthController authController = new AuthController();

    private final JTextField txtEmail = new JTextField(22);
    private final JPasswordField txtSenha = new JPasswordField(22);

    private final JButton btnLogin = new JButton("Entrar");
    private final JButton btnCadastrar = new JButton("Criar conta");

    private final JCheckBox chkMostrarSenha =
            new JCheckBox("Mostrar senha");

    private final JLabel lblEstado =
            new JLabel("Introduza os seus dados para continuar.");

    private final JLabel lblCapsLock = new JLabel(" ");

    private final char caracterSenha;

    private boolean aAutenticar = false;

    public LoginView() {
        caracterSenha = txtSenha.getEchoChar();

        setTitle("Login — Sistema de Gestão de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new GridLayout(1, 2));
        principal.add(criarPainelEsquerdo());
        principal.add(criarFormulario());

        setContentPane(principal);

        configurarEventos();
        configurarAtalhos();

        getRootPane().setDefaultButton(btnLogin);

        setVisible(true);

        SwingUtilities.invokeLater(() ->
                txtEmail.requestFocusInWindow()
        );
    }

    private JPanel criarPainelEsquerdo() {
        JPanel painel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                try {
                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setPaint(new GradientPaint(
                            0, 0, new Color(35, 48, 120),
                            getWidth(), getHeight(), AZUL
                    ));

                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fillOval(-100, -100, 320, 320);
                    g2.fillOval(
                            getWidth() - 180,
                            getHeight() - 200,
                            320,
                            320
                    );
                } finally {
                    g2.dispose();
                }
            }
        };

        painel.setBorder(new EmptyBorder(40, 35, 40, 35));

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(
                new BoxLayout(conteudo, BoxLayout.Y_AXIS)
        );

        JLabel marca = new JLabel("GESTÃO DE EVENTOS");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("SansSerif", Font.BOLD, 25));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(marca);
        conteudo.add(Box.createVerticalStrut(30));

        URL recurso = getClass().getResource("/view/Logo.png");

        if (recurso != null) {
            Image imagem = new ImageIcon(recurso)
                    .getImage()
                    .getScaledInstance(
                            300, 188, Image.SCALE_SMOOTH
                    );

            JLabel logo = new JLabel(new ImageIcon(imagem));
            logo.setAlignmentX(Component.LEFT_ALIGNMENT);

            conteudo.add(logo);
            conteudo.add(Box.createVerticalStrut(30));
        }

        JLabel titulo = new JLabel(
                "<html>Grandes momentos<br>começam aqui.</html>"
        );
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descricao = new JLabel(
                "<html>Organize eventos, disponibilize bilhetes<br>"
                        + "e acompanhe as suas vendas.</html>"
        );
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descricao.setForeground(new Color(220, 228, 255));
        descricao.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(18));
        conteudo.add(descricao);

        painel.add(conteudo);

        return painel;
    }

    private JPanel criarFormulario() {
        JPanel exterior = new JPanel(new GridBagLayout());
        exterior.setBackground(Color.WHITE);
        exterior.setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Bem-vindo de volta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Entre na sua conta para continuar."
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(CINZENTO);

        prepararCampo(txtEmail);
        prepararCampo(txtSenha);

        txtEmail.setToolTipText("Introduza o email da sua conta.");
        txtSenha.setToolTipText("Introduza a sua senha.");

        JLabel email = criarRotulo("Email");
        email.setLabelFor(txtEmail);

        JLabel senha = criarRotulo("Senha");
        senha.setLabelFor(txtSenha);

        chkMostrarSenha.setOpaque(false);
        chkMostrarSenha.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        chkMostrarSenha.setForeground(CINZENTO);
        chkMostrarSenha.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        chkMostrarSenha.setMnemonic(KeyEvent.VK_M);

        lblCapsLock.setForeground(new Color(180, 105, 0));
        lblCapsLock.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        estilizarBotao(
                btnLogin, AZUL, AZUL_HOVER, Color.WHITE
        );

        estilizarBotao(
                btnCadastrar,
                new Color(239, 243, 255),
                new Color(222, 230, 255),
                AZUL
        );

        btnLogin.setMnemonic(KeyEvent.VK_E);
        btnCadastrar.setMnemonic(KeyEvent.VK_C);

        lblEstado.setForeground(CINZENTO);
        lblEstado.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        adicionar(formulario, titulo, gbc, 0, 0, 8);
        adicionar(formulario, subtitulo, gbc, 1, 0, 22);
        adicionar(formulario, email, gbc, 2, 0, 7);
        adicionar(formulario, txtEmail, gbc, 3, 0, 16);
        adicionar(formulario, senha, gbc, 4, 0, 7);
        adicionar(formulario, txtSenha, gbc, 5, 0, 5);
        adicionar(formulario, lblCapsLock, gbc, 6, 0, 5);
        adicionar(formulario, chkMostrarSenha, gbc, 7, 0, 18);
        adicionar(formulario, btnLogin, gbc, 8, 0, 10);
        adicionar(formulario, btnCadastrar, gbc, 9, 0, 18);
        adicionar(formulario, lblEstado, gbc, 10, 0, 0);

        exterior.add(formulario);

        return exterior;
    }

    private JLabel criarRotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(TEXTO);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        return rotulo;
    }

    private void adicionar(
            JPanel painel,
            Component componente,
            GridBagConstraints gbc,
            int linha,
            int acima,
            int abaixo
    ) {
        gbc.gridy = linha;
        gbc.insets = new Insets(acima, 0, abaixo, 0);
        painel.add(componente, gbc);
    }

    private void prepararCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        campo.setForeground(TEXTO);
        campo.setBackground(FUNDO_CAMPO);
        campo.setCaretColor(AZUL);
        campo.setPreferredSize(new Dimension(300, 46));

        aplicarBorda(campo, BORDA);

        // EVENTOS DE FOCO: destaque ao entrar e sair do campo.
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                aplicarBorda(campo, AZUL);

                if (campo == txtSenha) {
                    actualizarCapsLock();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                aplicarBorda(campo, BORDA);

                if (campo == txtSenha) {
                    lblCapsLock.setText(" ");
                }
            }
        });
    }

    private void aplicarBorda(JTextField campo, Color cor) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
    }

    private void estilizarBotao(
            JButton botao,
            Color normal,
            Color hover,
            Color texto
    ) {
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setForeground(texto);
        botao.setBackground(normal);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(true);
        botao.setBorder(new EmptyBorder(13, 18, 13, 18));
        botao.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);

        // EVENTOS DO RATO: hover e efeito de pressão.
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Executado quando o ponteiro do rato entra na área do botão.
                if (botao.isEnabled()) {
                    botao.setBackground(hover);
                }
            }

            // Executado quando o ponteiro do rato sai da área do botão.
            @Override
            public void mouseExited(MouseEvent e) {
                // Repõe a cor original.
                botao.setBackground(normal);
            }

            // Executado quando um botão do rato é pressionado sobre o componente.
            @Override
            public void mousePressed(MouseEvent e) {
                // Verifica se o componente está habilitado
                // e se foi pressionado o botão esquerdo do rato.
                if (botao.isEnabled()
                        && SwingUtilities.isLeftMouseButton(e)) {
                    // Escurece a cor para criar o efeito de botão pressionado.
                    botao.setBackground(hover.darker());
                }
            }

            // Executado quando o botão do rato é libertado após a pressão.
            @Override
            public void mouseReleased(MouseEvent e) {
                botao.setBackground(
                        // Verifica se o botão está habilitado e se
                        // a posição actual do rato está dentro do componente.
                        botao.isEnabled()
                                && botao.contains(e.getPoint())
                                // Se a condição for verdadeira, aplica o destaque.
                                ? hover
                                // Caso contrário, aplica a cor original.
                                : normal
                );
            }
        });
    }

    private void configurarEventos() {
        // ACTION EVENT: funciona com rato e teclado.
        btnLogin.addActionListener(e -> entrar());

        btnCadastrar.addActionListener(e -> abrirCadastro());

        chkMostrarSenha.addActionListener(e ->
                txtSenha.setEchoChar(
                        chkMostrarSenha.isSelected()
                                ? (char) 0
                                : caracterSenha
                )
        );

        // KEY EVENT: verifica Caps Lock ao usar a senha.
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                actualizarCapsLock();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                actualizarCapsLock();
            }
        });
    }

    private void configurarAtalhos() {
        // KEY BINDINGS: atalhos válidos em toda a janela.
        registarAtalho(
                KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK),
                "focarEmail",
                () -> txtEmail.requestFocusInWindow()
        );

        registarAtalho(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
                "abrirCadastro",
                this::abrirCadastro
        );

        registarAtalho(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                "limpar",
                this::limparCampos
        );
    }

    private void registarAtalho(
            KeyStroke tecla,
            String nome,
            Runnable accao
    ) {
        JRootPane raiz = getRootPane();

        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(tecla, nome);

        raiz.getActionMap().put(nome, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!aAutenticar) {
                    accao.run();
                }
            }
        });
    }

    private void actualizarCapsLock() {
        try {
            boolean activo = Toolkit.getDefaultToolkit()
                    .getLockingKeyState(KeyEvent.VK_CAPS_LOCK);

            lblCapsLock.setText(
                    activo ? "Atenção: Caps Lock está activo." : " "
            );
        } catch (UnsupportedOperationException | SecurityException e) {
            // Alguns ambientes não permitem consultar esta tecla.
            lblCapsLock.setText(" ");
        }
    }

    private void abrirCadastro() {
        if (aAutenticar) {
            return;
        }

        new CadastroView();
        dispose();
    }

    private void limparCampos() {
        txtEmail.setText("");
        txtSenha.setText("");

        chkMostrarSenha.setSelected(false);
        txtSenha.setEchoChar(caracterSenha);

        lblCapsLock.setText(" ");
        lblEstado.setForeground(CINZENTO);
        lblEstado.setText("Campos limpos. Introduza os seus dados.");

        txtEmail.requestFocusInWindow();
    }

    private void definirCarregamento(boolean activo) {
        aAutenticar = activo;

        txtEmail.setEnabled(!activo);
        txtSenha.setEnabled(!activo);
        chkMostrarSenha.setEnabled(!activo);
        btnLogin.setEnabled(!activo);
        btnCadastrar.setEnabled(!activo);

        btnLogin.setText(activo ? "A entrar..." : "Entrar");

        setCursor(Cursor.getPredefinedCursor(
                activo ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR
        ));

        if (activo) {
            lblEstado.setForeground(CINZENTO);
            lblEstado.setText("A verificar as credenciais...");
        }
    }

    private void entrar() {
        if (aAutenticar) {
            return;
        }

        String email = txtEmail.getText().trim();
        char[] caracteres = txtSenha.getPassword();

        final String senha;

        try {
            if (email.isBlank() || caracteres.length == 0) {
                aviso("Preencha o email e a senha.");

                if (email.isBlank()) {
                    txtEmail.requestFocusInWindow();
                } else {
                    txtSenha.requestFocusInWindow();
                }

                return;
            }

            senha = new String(caracteres);
        } finally {
            Arrays.fill(caracteres, '\0');
        }

        definirCarregamento(true);

        // A consulta ao banco decorre fora da thread da interface.
        new SwingWorker<Utilizador, Void>() {
            @Override
            protected Utilizador doInBackground() {
                return authController.login(email, senha);
            }

            @Override
            protected void done() {
                if (!isDisplayable()) {
                    return;
                }

                definirCarregamento(false);

                try {
                    Utilizador utilizador = get();

                    if (utilizador == null) {
                        falhaLogin(
                                "Verifique as credenciais, o estado da conta "
                                        + "e a ligação ao banco."
                        );
                        return;
                    }

                    if (!utilizador.isEstado()) {
                        falhaLogin("A conta está desactivada.");
                        return;
                    }

                    if (utilizador instanceof Admin) {
                        new AdminView();

                    } else if (utilizador instanceof Promotor) {
                        new PromotorView((Promotor) utilizador);

                    } else if (utilizador instanceof Cliente) {
                        new UsuarioView((Cliente) utilizador);

                    } else {
                        falhaLogin("Perfil de utilizador desconhecido.");
                        return;
                    }

                    txtSenha.setText("");
                    dispose();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    falhaLogin("A autenticação foi interrompida.");

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    falhaLogin("Ocorreu um erro ao iniciar a sessão.");

                } catch (RuntimeException e) {
                    e.printStackTrace();
                    falhaLogin("Não foi possível abrir a tela do utilizador.");
                }
            }
        }.execute();
    }

    private void falhaLogin(String mensagem) {
        txtSenha.setText("");
        lblEstado.setForeground(new Color(185, 45, 55));
        lblEstado.setText("Não foi possível iniciar a sessão.");

        aviso(mensagem);
        txtSenha.requestFocusInWindow();
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Login",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginView::new);
    }
}