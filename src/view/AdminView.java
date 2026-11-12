package view;

import controller.CompraController;
import controller.EventoController;
import controller.UsuarioController;
import model.Cliente;
import model.Compra;
import model.Evento;
import model.Promotor;
import model.Utilizador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AdminView extends JFrame {

    private final UsuarioController usuarioController =
            new UsuarioController();

    private final EventoController eventoController =
            new EventoController();

    private final CompraController compraController =
            new CompraController();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel conteudo = new JPanel(cardLayout);

    private static final Color AZUL = new Color(41, 128, 185);

    public AdminView() {
        setTitle("Administrador - Sistema de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 12));
        menu.setBackground(AZUL);
        menu.setBorder(new EmptyBorder(25, 15, 25, 15));
        menu.setPreferredSize(new Dimension(210, 0));

        conteudo.add(criarPainelUtilizadores(false), "clientes");
        conteudo.add(criarPainelUtilizadores(true), "promotores");
        conteudo.add(criarPainelEventos(), "eventos");
        conteudo.add(criarPainelEstatisticas(), "estatisticas");

        adicionarNavegacao(menu, "Gerir clientes", "clientes");
        adicionarNavegacao(menu, "Gerir promotores", "promotores");
        adicionarNavegacao(menu, "Ver eventos", "eventos");
        adicionarNavegacao(menu, "Estatísticas", "estatisticas");

        JButton sair = botao("Sair");
        sair.addActionListener(e -> {
            new LoginView();
            dispose();
        });

        menu.add(sair);

        add(menu, BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);

        setVisible(true);
    }

    private void adicionarNavegacao(
            JPanel menu,
            String texto,
            String painel
    ) {
        JButton button = botao(texto);
        button.addActionListener(e ->
                cardLayout.show(conteudo, painel)
        );
        menu.add(button);
    }

    private JPanel criarPainelUtilizadores(boolean promotores) {
        JPanel panel = painelBase();

        JLabel titulo = titulo(
                promotores ? "Gestão de Promotores" : "Gestão de Clientes"
        );

        JTextField pesquisa = new JTextField(18);
        JButton pesquisar = botao("Pesquisar");
        JButton recarregar = botao("Recarregar");

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        filtros.add(pesquisa);
        filtros.add(pesquisar);
        filtros.add(recarregar);

        topo.add(titulo, BorderLayout.NORTH);
        topo.add(filtros, BorderLayout.SOUTH);

        DefaultTableModel modelo = modelo(
                "ID", "Nome", "Email", "Estado"
        );

        JTable tabela = tabela(modelo);

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(modelo);

        tabela.setRowSorter(sorter);

        Runnable filtrar = () -> {
            String termo = pesquisa.getText().trim();

            sorter.setRowFilter(
                    termo.isEmpty()
                            ? null
                            : RowFilter.regexFilter(
                            "(?iu)" + Pattern.quote(termo),
                            1,
                            2
                    )
            );
        };

        pesquisar.addActionListener(e -> filtrar.run());
        pesquisa.addActionListener(e -> filtrar.run());

        JTextField nome = new JTextField(20);
        JTextField email = new JTextField(20);
        JPasswordField senha = new JPasswordField(20);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 10));
        formulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Dados — deixe a senha vazia para a manter na edição"
                )
        );

        formulario.add(new JLabel("Nome:"));
        formulario.add(nome);
        formulario.add(new JLabel("Email:"));
        formulario.add(email);
        formulario.add(new JLabel("Senha:"));
        formulario.add(senha);

        Runnable limpar = () -> {
            tabela.clearSelection();
            nome.setText("");
            email.setText("");
            senha.setText("");
        };

        Runnable carregar = () ->
                executar(usuarioController::listarTodos, utilizadores -> {
                    limpar.run();
                    modelo.setRowCount(0);

                    for (Utilizador u : utilizadores) {
                        boolean incluir = promotores
                                ? u instanceof Promotor
                                : u instanceof Cliente;

                        if (incluir) {
                            modelo.addRow(new Object[]{
                                    u.getId(),
                                    u.getNome(),
                                    u.getEmail(),
                                    u.isEstado() ? "Activo" : "Inactivo"
                            });
                        }
                    }
                });

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int linha = tabela.getSelectedRow();

            if (linha >= 0) {
                int indice = tabela.convertRowIndexToModel(linha);

                nome.setText(String.valueOf(modelo.getValueAt(indice, 1)));
                email.setText(String.valueOf(modelo.getValueAt(indice, 2)));
                senha.setText("");
            }
        });

        JButton novo = botao("Novo");
        JButton salvar = botao("Criar");
        JButton atualizar = botao("Actualizar");
        JButton desativar = botao("Desactivar");
        JButton reativar = botao("Reactivar");
        JButton remover = botao("Remover");

        novo.addActionListener(e -> limpar.run());
        recarregar.addActionListener(e -> carregar.run());

        salvar.addActionListener(e -> {
            String valorNome = nome.getText().trim();
            String valorEmail = email.getText().trim();
            String valorSenha = new String(senha.getPassword());

            if (!validarDados(valorNome, valorEmail, valorSenha, true)) {
                return;
            }

            executar(
                    () -> promotores
                            ? usuarioController.criarPromotor(
                            valorNome, valorEmail, valorSenha
                    )
                            : usuarioController.criarCliente(
                            valorNome, valorEmail, valorSenha
                    ),
                    sucesso -> resultado(sucesso, carregar)
            );
        });

        atualizar.addActionListener(e -> {
            Integer id = idSelecionado(tabela);

            if (id == null) {
                return;
            }

            String valorNome = nome.getText().trim();
            String valorEmail = email.getText().trim();
            String valorSenha = new String(senha.getPassword());

            if (!validarDados(valorNome, valorEmail, valorSenha, false)) {
                return;
            }

            executar(() -> {
                Utilizador u = usuarioController.buscarPorId(id);

                if (u == null) {
                    return false;
                }

                boolean tipoCorreto = promotores
                        ? u instanceof Promotor
                        : u instanceof Cliente;

                if (!tipoCorreto) {
                    return false;
                }

                u.setNome(valorNome);
                u.setEmail(valorEmail);

                if (!valorSenha.isEmpty()) {
                    u.setSenha(valorSenha);
                }

                return usuarioController.atualizar(u);
            }, sucesso -> resultado(sucesso, carregar));
        });

        desativar.addActionListener(e ->
                alterarEstado(tabela, false, carregar)
        );

        reativar.addActionListener(e ->
                alterarEstado(tabela, true, carregar)
        );

        remover.addActionListener(e -> {
            Integer id = idSelecionado(tabela);

            if (id == null) {
                return;
            }

            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja remover definitivamente este utilizador?",
                    "Confirmar remoção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (resposta == JOptionPane.YES_OPTION) {
                executar(
                        () -> usuarioController.eliminar(id),
                        sucesso -> resultado(sucesso, carregar)
                );
            }
        });

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acoes.add(novo);
        acoes.add(salvar);
        acoes.add(atualizar);
        acoes.add(desativar);
        acoes.add(reativar);
        acoes.add(remover);

        JPanel rodape = new JPanel(new BorderLayout(10, 10));
        rodape.add(formulario, BorderLayout.CENTER);
        rodape.add(acoes, BorderLayout.SOUTH);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        panel.add(rodape, BorderLayout.SOUTH);

        // Só começa a consulta depois de a interface estar construída.
        SwingUtilities.invokeLater(carregar);

        return panel;
    }

    private void alterarEstado(
            JTable tabela,
            boolean estado,
            Runnable carregar
    ) {
        Integer id = idSelecionado(tabela);

        if (id != null) {
            executar(
                    () -> usuarioController.alterarEstado(id, estado),
                    sucesso -> resultado(sucesso, carregar)
            );
        }
    }

    private JPanel criarPainelEventos() {
        JPanel panel = painelBase();
        Color fundo = new Color(245, 247, 252);
        panel.setBackground(fundo);

        JPanel cards = new JPanel(new GridLayout(0, 2, 16, 16));
        cards.setOpaque(false);

        // Mantém os cartões no topo sem os esticar na vertical.
        JPanel areaCards = new JPanel(new BorderLayout());
        areaCards.setBackground(fundo);
        areaCards.setBorder(new EmptyBorder(5, 5, 5, 5));
        areaCards.add(cards, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(areaCards);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        JLabel resumo = new JLabel("A carregar eventos...");
        resumo.setForeground(new Color(100, 116, 139));
        resumo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton atualizar = botao("Actualizar eventos");

        Runnable carregar = () ->
                executar(eventoController::listarTodos, eventos -> {
                    cards.removeAll();

                    int activos = 0;

                    for (Evento evento : eventos) {
                        cards.add(criarCardEventoAdmin(evento));

                        if (evento.isEstado()) {
                            activos++;
                        }
                    }

                    resumo.setText(
                            eventos.size() + " eventos"
                                    + "  |  " + activos + " activos"
                                    + "  |  " + (eventos.size() - activos)
                                    + " inactivos"
                    );

                    if (eventos.isEmpty()) {
                        JLabel vazio = new JLabel(
                                "Ainda não existem eventos.",
                                SwingConstants.CENTER
                        );

                        vazio.setPreferredSize(new Dimension(300, 150));
                        vazio.setForeground(new Color(100, 116, 139));
                        cards.add(vazio);
                    }

                    cards.revalidate();
                    cards.repaint();
                });

        // EVENTO DE ACÇÃO: actualiza os cartões a partir do banco.
        atualizar.addActionListener(e -> carregar.run());

        JPanel cabecalho = new JPanel(new BorderLayout(10, 10));
        cabecalho.setOpaque(false);
        cabecalho.add(titulo("Eventos"), BorderLayout.WEST);
        cabecalho.add(atualizar, BorderLayout.EAST);

        JPanel topo = new JPanel(new BorderLayout(0, 8));
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(resumo, BorderLayout.SOUTH);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        // Inicia a consulta depois de construir a interface.
        SwingUtilities.invokeLater(carregar);

        return panel;
    }

    private JPanel criarCardEventoAdmin(Evento evento) {
        Color bordaNormal = new Color(220, 226, 238);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(310, 310));

        aplicarBordaCardAdmin(card, bordaNormal);

        // Cabeçalho: ID e estado do evento.
        JLabel identificador = new JLabel("EVENTO #" + evento.getId());
        identificador.setFont(new Font("SansSerif", Font.BOLD, 12));
        identificador.setForeground(AZUL);

        JLabel estado = new JLabel(
                evento.isEstado() ? "ACTIVO" : "INACTIVO"
        );

        estado.setOpaque(true);
        estado.setFont(new Font("SansSerif", Font.BOLD, 11));
        estado.setBorder(new EmptyBorder(5, 8, 5, 8));

        estado.setBackground(
                evento.isEstado()
                        ? new Color(220, 245, 232)
                        : new Color(253, 232, 232)
        );

        estado.setForeground(
                evento.isEstado()
                        ? new Color(25, 125, 75)
                        : new Color(175, 55, 55)
        );

        JPanel cabecalho = new JPanel(new BorderLayout(8, 0));
        cabecalho.setOpaque(false);
        cabecalho.add(identificador, BorderLayout.WEST);
        cabecalho.add(estado, BorderLayout.EAST);

        JLabel nome = new JLabel(
                "<html><body style='width:170px'>"
                        + escaparHtmlAdmin(evento.getNome())
                        + "</body></html>"
        );

        nome.setFont(new Font("SansSerif", Font.BOLD, 21));
        nome.setForeground(new Color(30, 41, 59));
        nome.setVerticalAlignment(SwingConstants.TOP);

        String data = evento.getData() == null
                ? "Por definir"
                : evento.getData().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        String hora = evento.getHora() == null
                ? "Por definir"
                : evento.getHora().format(
                DateTimeFormatter.ofPattern("HH:mm")
        );

        String promotor = evento.getPromotor() == null
                ? "Não disponível"
                : evento.getPromotor().getNome();

        JLabel informacoes = new JLabel(
                "<html><body style='width:170px'>"
                        + "<b>Data:</b> " + data
                        + " &nbsp; <b>Hora:</b> " + hora
                        + "<br><br><b>Local:</b> "
                        + escaparHtmlAdmin(evento.getLocal())
                        + "<br><b>Capacidade:</b> "
                        + evento.getCapacidade() + " pessoas"
                        + "<br><b>Promotor:</b> "
                        + escaparHtmlAdmin(promotor)
                        + "</body></html>"
        );

        informacoes.setFont(new Font("SansSerif", Font.PLAIN, 13));
        informacoes.setForeground(new Color(100, 116, 139));
        informacoes.setVerticalAlignment(SwingConstants.TOP);

        JPanel corpo = new JPanel(new BorderLayout(0, 12));
        corpo.setOpaque(false);
        corpo.add(nome, BorderLayout.NORTH);
        corpo.add(informacoes, BorderLayout.CENTER);


        JButton detalhes = botao("Ver detalhes");

        // EVENTO DE ACÇÃO: funciona com rato e teclado.
        detalhes.addActionListener(e ->
                executar(
                        () -> eventoController.buscarPorId(evento.getId()),
                        actual -> {
                            if (actual == null) {
                                aviso("O evento já não existe. Actualize a lista.");
                                return;
                            }

                            mostrarDetalhesEventoAdmin(actual);
                        }
                )
        );

        // EVENTOS DO RATO: efeito visual no botão.
        detalhes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                detalhes.setBackground(AZUL.darker());
                aplicarBordaCardAdmin(card, AZUL);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                detalhes.setBackground(AZUL);
                aplicarBordaCardAdmin(card, bordaNormal);
            }
        });

        // EVENTOS DE FOCO: destaque ao navegar com Tab.
        detalhes.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                aplicarBordaCardAdmin(card, AZUL);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                aplicarBordaCardAdmin(card, bordaNormal);
            }
        });

        card.add(cabecalho, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        card.add(detalhes, BorderLayout.SOUTH);

        return card;
    }

    private void aplicarBordaCardAdmin(JPanel card, Color cor) {
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 2, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
    }

    private void mostrarDetalhesEventoAdmin(Evento evento) {
        String data = evento.getData() == null
                ? "Por definir"
                : evento.getData().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        String hora = evento.getHora() == null
                ? "Por definir"
                : evento.getHora().format(
                DateTimeFormatter.ofPattern("HH:mm")
        );

        String promotor = evento.getPromotor() == null
                ? "Não disponível"
                : evento.getPromotor().getNome();

        // JTextArea apresenta texto simples e permite copiar os detalhes.
        JTextArea texto = new JTextArea(
                "Evento: " + evento.getNome()
                        + "\n\nDescrição: "
                        + (evento.getDescricao() == null
                        ? "" : evento.getDescricao())
                        + "\n\nData: " + data
                        + "\nHora: " + hora
                        + "\nLocal: " + evento.getLocal()
                        + "\nCapacidade: " + evento.getCapacidade()
                        + "\nPromotor: " + promotor
                        + "\nEstado: "
                        + (evento.isEstado() ? "Activo" : "Inactivo")
        );

        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        texto.setBorder(new EmptyBorder(12, 12, 12, 12));
        texto.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(texto);
        scroll.setPreferredSize(new Dimension(460, 330));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Detalhes do evento",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String escaparHtmlAdmin(String texto) {
        if (texto == null) {
            return "";
        }

        // Impede que nomes e locais sejam interpretados como código HTML.
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private JPanel criarPainelEstatisticas() {
        JPanel panel = painelBase();

        JLabel resumo = new JLabel("Carregue as estatísticas.");
        JButton atualizar = botao("Actualizar estatísticas");

        DefaultTableModel modelo = modelo(
                "Evento", "Bilhetes vendidos", "Receita bruta (MT)",
                "Ocupação", "Estado"
        );

        JTable tabela = tabela(modelo);

        atualizar.addActionListener(e ->
                executar(() -> new DadosRelatorio(
                        eventoController.listarTodos(),
                        compraController.listarTodos()
                ), dados -> {
                    modelo.setRowCount(0);

                    long totalVendidos = 0;
                    double receitaTotal = 0;

                    for (Evento evento : dados.eventos) {
                        long vendidos = 0;
                        double receita = 0;

                        for (Compra compra : dados.compras) {
                            if (compra.getBilhete() == null
                                    || compra.getBilhete().getEvento() == null) {
                                continue;
                            }

                            int eventoId =
                                    compra.getBilhete().getEvento().getId();

                            if (eventoId == evento.getId()) {
                                vendidos += compra.getQuantidade();
                                receita += compra.getValorTotal();
                            }
                        }

                        totalVendidos += vendidos;
                        receitaTotal += receita;

                        double ocupacao = evento.getCapacidade() > 0
                                ? vendidos * 100.0 / evento.getCapacidade()
                                : 0;

                        String estado;

                        if (!evento.isEstado()) {
                            estado = "Inactivo";
                        } else if (vendidos >= evento.getCapacidade()) {
                            estado = "Lotado";
                        } else {
                            estado = "Activo";
                        }

                        modelo.addRow(new Object[]{
                                evento.getNome(),
                                vendidos,
                                String.format("%.2f", receita),
                                String.format("%.1f%%", ocupacao),
                                estado
                        });
                    }

                    resumo.setText(String.format(
                            "Eventos: %d | Bilhetes vendidos: %d | "
                                    + "Receita bruta: %.2f MT",
                            dados.eventos.size(),
                            totalVendidos,
                            receitaTotal
                    ));
                })
        );

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        topo.add(titulo("Estatísticas de vendas"), BorderLayout.NORTH);
        topo.add(resumo, BorderLayout.CENTER);
        topo.add(atualizar, BorderLayout.SOUTH);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return panel;
    }

    private boolean validarDados(
            String nome,
            String email,
            String senha,
            boolean criacao
    ) {
        if (nome.isBlank() || email.isBlank()) {
            aviso("Preencha o nome e o email.");
            return false;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            aviso("Digite um email válido.");
            return false;
        }

        if (criacao && senha.isBlank()) {
            aviso("Informe a senha.");
            return false;
        }

        if (!senha.isEmpty()
                && (senha.isBlank() || senha.length() < 4)) {
            aviso("A senha deve ter pelo menos 4 caracteres.");
            return false;
        }

        return true;
    }

    private Integer idSelecionado(JTable tabela) {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            aviso("Seleccione um registo na tabela.");
            return null;
        }

        int indice = tabela.convertRowIndexToModel(linha);

        return ((Number) tabela.getModel().getValueAt(indice, 0))
                .intValue();
    }

    private void resultado(boolean sucesso, Runnable carregar) {
        if (sucesso) {
            JOptionPane.showMessageDialog(
                    this,
                    "Operação realizada com sucesso."
            );
            carregar.run();
        } else {
            aviso(
                    "Não foi possível concluir a operação. "
                            + "Verifique os dados, emails duplicados "
                            + "ou registos relacionados no banco."
            );
        }
    }

    private <T> void executar(
            Callable<T> tarefa,
            Consumer<T> aoConcluir
    ) {
        // Impede novas operações nesta janela durante o pedido.
        setEnabled(false);

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarefa.call();
            }

            @Override
            protected void done() {
                setEnabled(true);

                if (!isDisplayable()) {
                    return;
                }

                try {
                    aoConcluir.accept(get());
                } catch (Exception e) {
                    e.printStackTrace();
                    aviso("Ocorreu um erro ao executar a operação.");
                }
            }
        }.execute();
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Atenção",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private JPanel painelBase() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(AZUL);
        return label;
    }

    private JButton botao(String texto) {
        String nome = texto.toLowerCase(Locale.ROOT);

        Color inicio;
        Color fim;

        // Cores associadas à finalidade da operação.
        if (nome.contains("remover") || nome.equals("sair")) {
            // Vermelho: remoção e saída.
            inicio = new Color(190, 35, 75);
            fim = new Color(145, 25, 65);

        } else if (nome.contains("desactivar")) {
            // Laranja: desactivação.
            inicio = new Color(165, 80, 15);
            fim = new Color(125, 55, 20);

        } else if (nome.contains("criar") || nome.contains("reactivar")) {
            // Verde: criação e reactivação.
            inicio = new Color(15, 120, 100);
            fim = new Color(15, 85, 100);

        } else {
            // Azul e violeta: navegação e consultas.
            inicio = new Color(55, 65, 180);
            fim = new Color(100, 45, 175);
        }

        return new BotaoFuturista(texto, inicio, fim);
    }

    private DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    private JTable tabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(30);
        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tabela.getTableHeader().setReorderingAllowed(false);
        return tabela;
    }

    private static class DadosRelatorio {
        private final List<Evento> eventos;
        private final List<Compra> compras;

        private DadosRelatorio(
                List<Evento> eventos,
                List<Compra> compras
        ) {
            this.eventos = eventos;
            this.compras = compras;
        }
    }

    /*
     * HERANÇA:
     * BotaoFuturista herda o comportamento de JButton.
     *
     * Mantém ActionListener, navegação por Tab e activação com Espaço.
     * Personaliza apenas a apresentação.
     */
    private static class BotaoFuturista extends JButton {

        private final Color inicio;
        private final Color fim;

        private boolean ratoSobre = false;

        public BotaoFuturista(
                String texto,
                Color inicio,
                Color fim
        ) {
            super(texto);

            this.inicio = inicio;
            this.fim = fim;

            setFont(new Font("SansSerif", Font.BOLD, 13));
            setForeground(Color.WHITE);

            // O fundo e a borda serão desenhados em paintComponent().
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);

            // Espaço reservado para o texto e para o brilho exterior.
            setBorder(new EmptyBorder(14, 18, 14, 18));

            setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );

            /*
             * EVENTOS DO RATO:
             * registam a entrada e a saída do ponteiro.
             */
            addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    ratoSobre = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    ratoSobre = false;
                    repaint();
                }
            });

            /*
             * EVENTOS DE FOCO:
             * actualizam o destaque ao navegar pelo teclado.
             */
            addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    repaint();
                }
            });

            /*
             * EVENTO DE ALTERAÇÃO DO MODELO:
             * redesenha quando o botão é pressionado, libertado
             * ou activado através do teclado.
             */
            getModel().addChangeListener(e -> repaint());
        }

        /*
         * SOBRESCRITA:
         * desenha a aparência personalizada do botão.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            try {
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int largura = getWidth() - 9;
                int altura = getHeight() - 9;

                if (largura <= 0 || altura <= 0) {
                    return;
                }

                boolean pressionado = getModel().isPressed()
                        && getModel().isArmed();

                boolean destacado = isEnabled()
                        && (ratoSobre || isFocusOwner());

                Color corInicio = inicio;
                Color corFim = fim;

                if (!isEnabled()) {
                    corInicio = new Color(100, 110, 130);
                    corFim = new Color(75, 85, 105);

                } else if (pressionado) {
                    corInicio = inicio.darker();
                    corFim = fim.darker();

                } else if (destacado) {
                    // Clareia ligeiramente sem perder o contraste do texto.
                    corInicio = misturar(inicio, Color.WHITE, 0.10f);
                    corFim = misturar(fim, Color.WHITE, 0.10f);
                }

                // Brilho exterior no hover e no foco.
                if (destacado) {
                    for (int i = 3; i >= 1; i--) {
                        g2.setColor(
                                new Color(90, 175, 255, 30)
                        );

                        g2.fillRoundRect(
                                4 - i,
                                4 - i,
                                largura + i * 2,
                                altura + i * 2,
                                20,
                                20
                        );
                    }
                }

                // Fundo com gradiente diagonal.
                g2.setPaint(new GradientPaint(
                        4, 4, corInicio,
                        getWidth(), getHeight(), corFim
                ));

                g2.fillRoundRect(
                        4, 4, largura, altura, 18, 18
                );

                // Reflexo discreto na metade superior.
                g2.setPaint(new GradientPaint(
                        0, 4, new Color(255, 255, 255, 35),
                        0, 4 + altura / 2f,
                        new Color(255, 255, 255, 0)
                ));

                g2.fillRoundRect(
                        5, 5,
                        largura - 2,
                        Math.max(1, altura / 2),
                        16, 16
                );

                // Contorno luminoso.
                g2.setColor(
                        destacado
                                ? new Color(150, 220, 255)
                                : new Color(255, 255, 255, 65)
                );

                g2.setStroke(new BasicStroke(
                        destacado ? 1.5f : 1f
                ));

                g2.drawRoundRect(
                        4, 4, largura, altura, 18, 18
                );

                // Indicador adicional de foco para utilização com Tab.
                if (isFocusOwner() && isEnabled()) {
                    g2.setColor(Color.WHITE);

                    g2.drawRoundRect(
                            7, 7,
                            Math.max(1, largura - 6),
                            Math.max(1, altura - 6),
                            13, 13
                    );
                }

            } finally {
                g2.dispose();
            }

            // JButton continua responsável por desenhar o texto e o ícone.
            super.paintComponent(g);
        }

        private static Color misturar(
                Color primeira,
                Color segunda,
                float proporcao
        ) {
            float inversa = 1f - proporcao;

            return new Color(
                    Math.round(
                            primeira.getRed() * inversa
                                    + segunda.getRed() * proporcao
                    ),
                    Math.round(
                            primeira.getGreen() * inversa
                                    + segunda.getGreen() * proporcao
                    ),
                    Math.round(
                            primeira.getBlue() * inversa
                                    + segunda.getBlue() * proporcao
                    )
            );
        }
    }
}