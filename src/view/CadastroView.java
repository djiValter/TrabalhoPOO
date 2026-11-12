package view;

import controller.UsuarioController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CadastroView extends JFrame {

    private static final Color AZUL = new Color(58, 86, 225);
    private static final Color AZUL_HOVER = new Color(43, 65, 185);
    private static final Color TEXTO = new Color(30, 41, 59);
    private static final Color CINZENTO = new Color(100, 116, 139);
    private static final Color BORDA = new Color(218, 225, 235);
    private static final Color FUNDO_CAMPO = new Color(248, 250, 252);

    private final UsuarioController usuarioController =
            new UsuarioController();

    private final JTextField txtNome = new JTextField(22);
    private final JTextField txtEmail = new JTextField(22);

    private final JPasswordField txtSenha =
            new JPasswordField(22);

    private final JPasswordField txtConfirmarSenha =
            new JPasswordField(22);

    private final JButton btnCadastrar =
            new JButton("Criar conta");

    private final JButton btnVoltar =
            new JButton("Voltar ao login");

    private final JCheckBox chkMostrarSenha =
            new JCheckBox("Mostrar senhas");

    private final JLabel lblCapsLock = new JLabel(" ");

    private final JLabel lblEstado =
            new JLabel("Preencha os dados para criar a sua conta.");

    private final char caracterSenha;
    private final char caracterConfirmacao;

    private boolean aCadastrar = false;

    public CadastroView() {
        caracterSenha = txtSenha.getEchoChar();
        caracterConfirmacao = txtConfirmarSenha.getEchoChar();

        setTitle("Cadastro — Sistema de Gestão de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 760);
        setMinimumSize(new Dimension(940, 740));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new GridLayout(1, 2));
        principal.add(criarPainelEsquerdo());
        principal.add(criarFormulario());

        setContentPane(principal);

        configurarEventos();
        configurarAtalhos();

        // TECLADO: Enter activa o botão de cadastro,
        // quando a tecla não é consumida pelo componente com foco.
        getRootPane().setDefaultButton(btnCadastrar);

        setVisible(true);

        // Coloca inicialmente o cursor no campo do nome.
        SwingUtilities.invokeLater(() ->
                txtNome.requestFocusInWindow()
        );
    }

    private JPanel criarPainelEsquerdo() {
        JPanel painel = new JPanel(new GridBagLayout()) {

            // SOBRESCRITA: desenha o fundo personalizado do painel.
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
        marca.setFont(new Font("SansSerif", Font.BOLD, 25));
        marca.setForeground(Color.WHITE);
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
                "<html>O próximo momento<br>começa consigo.</html>"
        );
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descricao = new JLabel(
                "<html>Crie a sua conta, descubra eventos<br>"
                        + "e compre os seus bilhetes.</html>"
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

        JLabel titulo = new JLabel("Crie a sua conta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Cadastro de cliente");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(CINZENTO);

        prepararCampo(txtNome);
        prepararCampo(txtEmail);
        prepararCampo(txtSenha);
        prepararCampo(txtConfirmarSenha);

        txtNome.setToolTipText("Introduza o seu nome.");
        txtEmail.setToolTipText("Exemplo: nome@email.com");
        txtSenha.setToolTipText("Mínimo de 4 caracteres.");
        txtConfirmarSenha.setToolTipText("Repita a senha.");

        chkMostrarSenha.setOpaque(false);
        chkMostrarSenha.setForeground(CINZENTO);
        chkMostrarSenha.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        chkMostrarSenha.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // MNEMÓNICO: normalmente Alt + M mostra ou oculta as senhas.
        chkMostrarSenha.setMnemonic(KeyEvent.VK_M);

        lblCapsLock.setForeground(new Color(180, 105, 0));
        lblCapsLock.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        lblEstado.setForeground(CINZENTO);
        lblEstado.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        estilizarBotao(
                btnCadastrar, AZUL, AZUL_HOVER, Color.WHITE
        );

        estilizarBotao(
                btnVoltar,
                new Color(239, 243, 255),
                new Color(222, 230, 255),
                AZUL
        );

        // MNEMÓNICOS: normalmente Alt + C e Alt + V.
        btnCadastrar.setMnemonic(KeyEvent.VK_C);
        btnVoltar.setMnemonic(KeyEvent.VK_V);

        adicionar(formulario, titulo, gbc, 0, 0, 6);
        adicionar(formulario, subtitulo, gbc, 1, 0, 20);

        adicionar(
                formulario,
                criarRotulo("Nome", txtNome),
                gbc, 2, 0, 5
        );
        adicionar(formulario, txtNome, gbc, 3, 0, 12);

        adicionar(
                formulario,
                criarRotulo("Email", txtEmail),
                gbc, 4, 0, 5
        );
        adicionar(formulario, txtEmail, gbc, 5, 0, 12);

        adicionar(
                formulario,
                criarRotulo("Senha", txtSenha),
                gbc, 6, 0, 5
        );
        adicionar(formulario, txtSenha, gbc, 7, 0, 12);

        adicionar(
                formulario,
                criarRotulo("Confirmar senha", txtConfirmarSenha),
                gbc, 8, 0, 5
        );
        adicionar(formulario, txtConfirmarSenha, gbc, 9, 0, 4);

        adicionar(formulario, lblCapsLock, gbc, 10, 0, 3);
        adicionar(formulario, chkMostrarSenha, gbc, 11, 0, 14);
        adicionar(formulario, btnCadastrar, gbc, 12, 0, 9);
        adicionar(formulario, btnVoltar, gbc, 13, 0, 14);
        adicionar(formulario, lblEstado, gbc, 14, 0, 0);

        exterior.add(formulario);

        return exterior;
    }

    private JLabel criarRotulo(String texto, JComponent campo) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        rotulo.setForeground(TEXTO);

        // Associa o rótulo ao respectivo campo.
        rotulo.setLabelFor(campo);

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
        campo.setPreferredSize(new Dimension(300, 44));

        aplicarBorda(campo, BORDA);

        // EVENTOS DE FOCO: entrar e sair de um campo.
        campo.addFocusListener(new FocusAdapter() {

            // Executado ao seleccionar o campo com rato ou teclado.
            @Override
            public void focusGained(FocusEvent e) {
                aplicarBorda(campo, AZUL);

                if (campo instanceof JPasswordField) {
                    actualizarCapsLock();
                }
            }

            // Executado quando outro componente recebe o foco.
            @Override
            public void focusLost(FocusEvent e) {
                aplicarBorda(campo, BORDA);

                if (campo instanceof JPasswordField) {
                    lblCapsLock.setText(" ");
                }
            }
        });
    }

    private void aplicarBorda(JTextField campo, Color cor) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 1, true),
                new EmptyBorder(9, 12, 9, 12)
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

        // EVENTOS DO RATO: alteram a aparência do botão.
        botao.addMouseListener(new MouseAdapter() {

            // O ponteiro entra no botão.
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botao.isEnabled()) {
                    botao.setBackground(hover);
                }
            }

            // O ponteiro sai do botão.
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(normal);
            }

            // O botão esquerdo do rato é pressionado.
            @Override
            public void mousePressed(MouseEvent e) {
                if (botao.isEnabled()
                        && SwingUtilities.isLeftMouseButton(e)) {
                    botao.setBackground(hover.darker());
                }
            }

            // O botão do rato é libertado.
            @Override
            public void mouseReleased(MouseEvent e) {
                botao.setBackground(
                        botao.isEnabled()
                                && botao.contains(e.getPoint())
                                ? hover
                                : normal
                );
            }
        });
    }

    private void configurarEventos() {
        // EVENTO DE ACÇÃO: submete o formulário.
        btnCadastrar.addActionListener(e -> cadastrar());

        // EVENTO DE ACÇÃO: regressa ao login.
        btnVoltar.addActionListener(e -> voltarAoLogin());

        // EVENTO DE ACÇÃO: mostra ou oculta as duas senhas.
        chkMostrarSenha.addActionListener(e -> {
            boolean mostrar = chkMostrarSenha.isSelected();

            txtSenha.setEchoChar(
                    mostrar ? (char) 0 : caracterSenha
            );

            txtConfirmarSenha.setEchoChar(
                    mostrar ? (char) 0 : caracterConfirmacao
            );
        });

        // EVENTOS DE TECLADO: verificam o Caps Lock.
        KeyAdapter eventosSenha = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                actualizarCapsLock();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                actualizarCapsLock();
            }
        };

        // O mesmo listener é utilizado nos dois campos.
        txtSenha.addKeyListener(eventosSenha);
        txtConfirmarSenha.addKeyListener(eventosSenha);
    }

    private void configurarAtalhos() {
        // Ctrl + L: coloca o cursor no email.
        registarAtalho(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_L,
                        InputEvent.CTRL_DOWN_MASK
                ),
                "focarEmail",
                () -> txtEmail.requestFocusInWindow()
        );

        // Ctrl + Backspace: limpa o formulário.
        // O atalho fica limitado a Ctrl + Shift + L para
        // preservar a edição normal de palavras nos campos.
        registarAtalho(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_L,
                        InputEvent.CTRL_DOWN_MASK
                                | InputEvent.SHIFT_DOWN_MASK
                ),
                "limparFormulario",
                this::limparFormulario
        );

        // Escape: regressa à tela de login.
        registarAtalho(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                "voltarLogin",
                this::voltarAoLogin
        );
    }

    private void registarAtalho(
            KeyStroke tecla,
            String nome,
            Runnable accao
    ) {
        JRootPane raiz = getRootPane();

        // Associa a tecla ao nome da acção.
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(tecla, nome);

        // Associa o nome à operação que será executada.
        raiz.getActionMap().put(nome, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!aCadastrar) {
                    accao.run();
                }
            }
        });
    }

    private void actualizarCapsLock() {
        // Método auxiliar chamado pelos eventos de foco e teclado.
        try {
            boolean activo = Toolkit.getDefaultToolkit()
                    .getLockingKeyState(KeyEvent.VK_CAPS_LOCK);

            lblCapsLock.setText(
                    activo
                            ? "Atenção: Caps Lock está activo."
                            : " "
            );
        } catch (UnsupportedOperationException | SecurityException e) {
            lblCapsLock.setText(" ");
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtEmail.setText("");
        txtSenha.setText("");
        txtConfirmarSenha.setText("");

        chkMostrarSenha.setSelected(false);
        txtSenha.setEchoChar(caracterSenha);
        txtConfirmarSenha.setEchoChar(caracterConfirmacao);

        lblCapsLock.setText(" ");
        lblEstado.setForeground(CINZENTO);
        lblEstado.setText("Preencha os dados para criar a sua conta.");

        txtNome.requestFocusInWindow();
    }

    private void voltarAoLogin() {
        if (aCadastrar) {
            return;
        }

        new LoginView();
        dispose();
    }

    private void definirCarregamento(boolean activo) {
        aCadastrar = activo;

        // Impede alterações e submissões repetidas durante o cadastro.
        txtNome.setEnabled(!activo);
        txtEmail.setEnabled(!activo);
        txtSenha.setEnabled(!activo);
        txtConfirmarSenha.setEnabled(!activo);
        chkMostrarSenha.setEnabled(!activo);
        btnCadastrar.setEnabled(!activo);
        btnVoltar.setEnabled(!activo);

        btnCadastrar.setText(
                activo ? "A criar conta..." : "Criar conta"
        );

        setCursor(Cursor.getPredefinedCursor(
                activo ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR
        ));

        if (activo) {
            lblEstado.setForeground(CINZENTO);
            lblEstado.setText("A guardar os dados...");
        }
    }

    private void cadastrar() {
        if (aCadastrar) {
            return;
        }

        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();

        char[] caracteres = txtSenha.getPassword();
        char[] confirmacao = txtConfirmarSenha.getPassword();

        final String senha;

        try {
            if (nome.isBlank()) {
                erroCampo("Introduza o seu nome.", txtNome);
                return;
            }

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                erroCampo("Introduza um email válido.", txtEmail);
                return;
            }

            // Mantém o mínimo de 4 caracteres do teu código original.
            if (caracteres.length < 4) {
                erroCampo(
                        "A senha deve ter pelo menos 4 caracteres.",
                        txtSenha
                );
                return;
            }

            boolean apenasEspacos = true;

            for (char caractere : caracteres) {
                if (!Character.isWhitespace(caractere)) {
                    apenasEspacos = false;
                    break;
                }
            }

            if (apenasEspacos) {
                erroCampo(
                        "A senha não pode conter apenas espaços.",
                        txtSenha
                );
                return;
            }

            if (!Arrays.equals(caracteres, confirmacao)) {
                erroCampo(
                        "As senhas não coincidem.",
                        txtConfirmarSenha
                );
                return;
            }

            // O controller existente recebe a senha como String.
            senha = new String(caracteres);

        } finally {
            // Limpa os arrays temporários, incluindo quando há erro.
            Arrays.fill(caracteres, '\0');
            Arrays.fill(confirmacao, '\0');
        }

        definirCarregamento(true);

        /*
         * SwingWorker executa a operação no banco em segundo plano,
         * mantendo a interface disponível para ser redesenhada.
         */
        new SwingWorker<Boolean, Void>() {

            // Executado fora da thread da interface.
            @Override
            protected Boolean doInBackground() {
                return usuarioController.criarCliente(
                        nome,
                        email,
                        senha
                );
            }

            // Executado na thread da interface quando a tarefa termina.
            @Override
            protected void done() {
                if (!isDisplayable()) {
                    return;
                }

                definirCarregamento(false);

                try {
                    if (Boolean.TRUE.equals(get())) {
                        txtSenha.setText("");
                        txtConfirmarSenha.setText("");

                        JOptionPane.showMessageDialog(
                                CadastroView.this,
                                "Conta criada com sucesso! Faça o login.",
                                "Cadastro concluído",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        voltarAoLogin();

                    } else {
                        falhaCadastro(
                                "Não foi possível cadastrar. "
                                        + "O email pode já existir ou pode "
                                        + "ter ocorrido uma falha no banco."
                        );
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    falhaCadastro("O cadastro foi interrompido.");

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    falhaCadastro("Ocorreu um erro ao cadastrar.");

                } catch (RuntimeException e) {
                    e.printStackTrace();
                    falhaCadastro(
                            "Não foi possível concluir a operação. "
                                    + "Se já recebeu a confirmação do cadastro, "
                                    + "tente entrar pela tela de login."
                    );
                }
            }
        }.execute();
    }

    private void erroCampo(String mensagem, JTextField campo) {
        lblEstado.setForeground(new Color(185, 45, 55));
        lblEstado.setText("Verifique os dados do formulário.");

        aviso(mensagem);
        campo.requestFocusInWindow();
    }

    private void falhaCadastro(String mensagem) {
        lblEstado.setForeground(new Color(185, 45, 55));
        lblEstado.setText("Não foi possível concluir a operação.");
        aviso(mensagem);
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Cadastro",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CadastroView::new);
    }
}