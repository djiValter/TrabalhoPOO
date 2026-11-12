package view;

import controller.BilheteController;
import controller.CompraController;
import controller.EventoController;
import controller.UsuarioController;
import model.Bilhete;
import model.BilheteVIP;
import model.Cliente;
import model.Compra;
import model.Evento;
import model.Utilizador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.swing.table.DefaultTableCellRenderer;

public class UsuarioView extends JFrame {

    private final Cliente cliente;

    private final EventoController eventoController =
            new EventoController();

    private final BilheteController bilheteController =
            new BilheteController();

    private final CompraController compraController =
            new CompraController();

    private final UsuarioController usuarioController =
            new UsuarioController();

    private final DefaultTableModel modeloEventos = modelo(
            "ID", "Evento", "Data", "Hora", "Local", "Capacidade"
    );

    private final JTable tabelaEventos = tabela(modeloEventos);

    private final DefaultTableModel modeloBilhetes = modelo(
            "ID", "Tipo", "Preço unitário (MT)", "Disponíveis"
    );

    private final DefaultTableModel modeloCompras = modelo(
            "Compra", "Evento", "Tipo",
            "Quantidade", "Total pago (MT)", "Data"
    );


    private final JTable tabelaBilhetes = tabela(modeloBilhetes);

    private final JSpinner quantidade =
            new JSpinner(new SpinnerNumberModel(
                    1, 1, Integer.MAX_VALUE, 1
            ));

    private final JLabel lblEvento =
            new JLabel("Seleccione um evento.");

    private final JLabel lblHistorico =
            new JLabel("Clique em Actualizar histórico.");

    private Integer eventoSelecionadoId;

    // Painel que organiza os cartões em duas colunas.
    private final JPanel painelCards =
            new JPanel(new GridLayout(0, 2, 16, 16));

    // Permite seleccionar apenas um cartão de cada vez.
    private ButtonGroup grupoEventos = new ButtonGroup();

    private static final Color AZUL = new Color(58, 86, 225);
    private static final Color FUNDO = new Color(245, 247, 252);
    private static final Color BORDA = new Color(220, 226, 238);

    // Compatível com chamadas que usam Cliente ou Utilizador.
    public UsuarioView(Utilizador utilizador) {
        if (!(utilizador instanceof Cliente)
                || utilizador.getId() <= 0
                || !utilizador.isEstado()) {
            throw new IllegalArgumentException(
                    "É necessário um cliente autenticado e activo."
            );
        }

        this.cliente = (Cliente) utilizador;

        setTitle("Painel do Cliente — " + cliente.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topo = new JPanel(new BorderLayout());

        topo.add(
                new JLabel("Bem-vindo, " + cliente.getNome()),
                BorderLayout.WEST
        );

        JButton sair = new JButton("Sair");

        // Vermelho com texto branco.
        estilizarBotaoCor(sair, new Color(180, 40, 50));

        sair.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                    this,
                    "Deseja terminar a sessão?",
                    "Sair",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) {
                new LoginView();
                dispose();
            }
        });

        topo.add(sair, BorderLayout.EAST);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Comprar bilhetes", painelCompra());
        abas.addTab("Minhas compras", painelHistorico());

        principal.add(topo, BorderLayout.NORTH);
        principal.add(abas, BorderLayout.CENTER);

        setContentPane(principal);
        setVisible(true);

        carregarEventos();
    }

    private JPanel painelCompra() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(FUNDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Descubra o seu próximo evento");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        titulo.setForeground(new Color(30, 41, 59));

        JButton atualizar = new JButton("Actualizar eventos");

        // EVENTO DE ACÇÃO: volta a consultar os eventos.
        atualizar.addActionListener(e -> carregarEventos());

        JPanel topo = new JPanel(new BorderLayout(12, 0));
        topo.setOpaque(false);
        topo.add(titulo, BorderLayout.CENTER);
        topo.add(atualizar, BorderLayout.EAST);

        painelCards.setOpaque(false);

        /*
         * O painel exterior mantém os cartões no topo.
         * Evita que sejam esticados verticalmente quando há poucos.
         */
        JPanel conteudoCards = new JPanel(new BorderLayout());
        conteudoCards.setBackground(FUNDO);
        conteudoCards.setBorder(new EmptyBorder(8, 8, 8, 8));
        conteudoCards.add(painelCards, BorderLayout.NORTH);

        JScrollPane scrollEventos = new JScrollPane(conteudoCards);
        scrollEventos.setBorder(BorderFactory.createEmptyBorder());
        scrollEventos.getVerticalScrollBar().setUnitIncrement(20);
        scrollEventos.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        JPanel bilhetes = new JPanel(new BorderLayout(10, 10));
        bilhetes.setBackground(Color.WHITE);
        bilhetes.setBorder(new EmptyBorder(12, 12, 12, 12));

        lblEvento.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblEvento.setForeground(AZUL);

        bilhetes.add(lblEvento, BorderLayout.NORTH);
        bilhetes.add(
                new JScrollPane(tabelaBilhetes),
                BorderLayout.CENTER
        );

        JSplitPane divisao = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollEventos,
                bilhetes
        );

        divisao.setBorder(BorderFactory.createEmptyBorder());
        divisao.setResizeWeight(0.7);
        divisao.setDividerLocation(320);
        divisao.setContinuousLayout(true);

        JButton comprar = new JButton("Comprar bilhete");
        comprar.setBackground(AZUL);
        comprar.setForeground(Color.WHITE);
        comprar.setOpaque(true);
        comprar.setBorderPainted(false);
        comprar.setFont(new Font("SansSerif", Font.BOLD, 14));
        comprar.setBorder(new EmptyBorder(11, 18, 11, 18));
        comprar.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // EVENTO DE ACÇÃO: compra o bilhete seleccionado na tabela.
        comprar.addActionListener(e -> comprar());

        JButton detalhes = new JButton("Detalhes do evento");

        // EVENTO DE ACÇÃO: apresenta os detalhes do cartão seleccionado.
        detalhes.addActionListener(e -> mostrarDetalhes());

        quantidade.setPreferredSize(new Dimension(80, 35));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        acoes.setOpaque(false);
        acoes.add(new JLabel("Quantidade:"));
        acoes.add(quantidade);
        acoes.add(detalhes);
        acoes.add(comprar);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(divisao, BorderLayout.CENTER);
        panel.add(acoes, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarEventos() {
        executar(eventoController::listarTodos, eventos -> {
            eventoSelecionadoId = null;

            painelCards.removeAll();
            grupoEventos = new ButtonGroup();

            modeloBilhetes.setRowCount(0);
            lblEvento.setText("Seleccione um cartão para consultar os bilhetes.");
            quantidade.setValue(1);

            int total = 0;

            for (Evento evento : eventos) {
                if (!disponivel(evento)) {
                    continue;
                }

                JToggleButton card = criarCardEvento(evento);

                grupoEventos.add(card);
                painelCards.add(card);

                total++;
            }

            if (total == 0) {
                JLabel vazio = new JLabel(
                        "Não existem eventos disponíveis neste momento.",
                        SwingConstants.CENTER
                );

                vazio.setFont(new Font("SansSerif", Font.PLAIN, 15));
                vazio.setForeground(new Color(100, 116, 139));
                vazio.setPreferredSize(new Dimension(300, 160));

                painelCards.add(vazio);
            }

            // Actualiza a organização e o desenho dos componentes.
            painelCards.revalidate();
            painelCards.repaint();
        });
    }

    private void carregarBilhetes(int eventoId) {
        executar(
                () -> bilheteController.listarPorEvento(eventoId),
                bilhetes -> {
                    // Ignora resultados de um evento que já não está seleccionado.
                    if (eventoSelecionadoId == null
                            || eventoSelecionadoId.intValue() != eventoId) {
                        return;
                    }

                    modeloBilhetes.setRowCount(0);

                    for (Bilhete bilhete : bilhetes) {
                        if (!bilhete.isEstado()
                                || bilhete.getQuantidade() <= 0) {
                            continue;
                        }

                        modeloBilhetes.addRow(new Object[]{
                                bilhete.getId(),
                                bilhete instanceof BilheteVIP
                                        ? "VIP" : "NORMAL",
                                bilhete.calcularPrecoFinal(),
                                bilhete.getQuantidade()
                        });
                    }
                }
        );
    }

    private void mostrarDetalhes() {
        if (eventoSelecionadoId == null) {
            aviso("Seleccione um evento.");
            return;
        }

        int id = eventoSelecionadoId;

        executar(
                () -> eventoController.buscarPorId(id),
                evento -> {
                    if (evento == null) {
                        aviso("O evento já não existe.");
                        return;
                    }

                    String nomePromotor = evento.getPromotor() == null
                            ? "Não disponível"
                            : evento.getPromotor().getNome();

                    JOptionPane.showMessageDialog(
                            this,
                            "Evento: " + evento.getNome()
                                    + "\nDescrição: "
                                    + (evento.getDescricao() == null
                                    ? "" : evento.getDescricao())
                                    + "\nData: " + evento.getData()
                                    + "\nHora: " + evento.getHora()
                                    + "\nLocal: " + evento.getLocal()
                                    + "\nCapacidade: " + evento.getCapacidade()
                                    + "\nPromotor: " + nomePromotor,
                            "Detalhes do evento",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
        );
    }

    private void comprar() {
        Integer bilheteId = idSelecionado(tabelaBilhetes);

        if (bilheteId == null || eventoSelecionadoId == null) {
            aviso("Seleccione um evento e um bilhete.");
            return;
        }

        try {
            quantidade.commitEdit();
        } catch (java.text.ParseException e) {
            aviso("Informe uma quantidade inteira válida.");
            return;
        }

        int qtd = ((Number) quantidade.getValue()).intValue();

        if (qtd <= 0) {
            aviso("A quantidade deve ser positiva.");
            return;
        }

        int linha = tabelaBilhetes.convertRowIndexToModel(
                tabelaBilhetes.getSelectedRow()
        );

        int stockExibido = ((Number) modeloBilhetes
                .getValueAt(linha, 3)).intValue();

        if (qtd > stockExibido) {
            aviso("A quantidade excede o stock apresentado.");
            return;
        }

        double preco = ((Number) modeloBilhetes
                .getValueAt(linha, 2)).doubleValue();

        int eventoId = eventoSelecionadoId;

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Comprar " + qtd + " bilhete(s)?"
                        + "\nTotal apresentado: "
                        + String.format("%.2f MT", preco * qtd)
                        + "\nA compra usa o preço actual guardado no banco.",
                "Confirmar compra",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        executar(() -> {
            Utilizador atual =
                    usuarioController.buscarPorId(cliente.getId());

            if (!(atual instanceof Cliente) || !atual.isEstado()) {
                throw new IllegalStateException(
                        "A conta do cliente está indisponível."
                );
            }

            Bilhete bilhete = bilheteController.buscarPorId(bilheteId);
            Evento evento = eventoController.buscarPorId(eventoId);

            if (bilhete == null
                    || bilhete.getEvento() == null
                    || bilhete.getEvento().getId() != eventoId
                    || !bilhete.isEstado()
                    || !disponivel(evento)) {
                throw new IllegalStateException(
                        "O evento ou o bilhete já não está disponível."
                );
            }

            if (bilhete.getQuantidade() < qtd) {
                throw new IllegalStateException(
                        "O stock disponível é insuficiente."
                );
            }

            if (Double.compare(
                    bilhete.calcularPrecoFinal(), preco
            ) != 0) {
                throw new IllegalStateException(
                        "O preço foi alterado. Actualize os bilhetes "
                                + "e confirme novamente a compra."
                );
            }

            return compraController.comprar(
                    (Cliente) atual,
                    bilhete,
                    qtd
            );

        }, sucesso -> {
            if (sucesso) {
                JOptionPane.showMessageDialog(
                        this,
                        "Compra realizada com sucesso!"
                );
                quantidade.setValue(1);
            } else {
                aviso(
                        "Não foi possível realizar a compra. "
                                + "O stock pode ter mudado ou pode "
                                + "ter ocorrido uma falha no banco."
                );
            }

            carregarBilhetes(eventoId);
        });
    }

    private JPanel painelHistorico() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JButton atualizar = new JButton("Actualizar histórico");

        // Verde com texto branco.
        estilizarBotaoCor(atualizar, new Color(25, 120, 75));

        atualizar.addActionListener(e ->
                executar(
                        () -> compraController.listarPorCliente(
                                cliente.getId()
                        ),
                        compras -> {
                            modeloCompras.setRowCount(0);

                            double total = 0;

                            for (Compra compra : compras) {
                                Bilhete bilhete = compra.getBilhete();

                                total += compra.getValorTotal();

                                modeloCompras.addRow(new Object[]{
                                        compra.getId(),
                                        bilhete.getEvento().getNome(),
                                        bilhete instanceof BilheteVIP
                                                ? "VIP" : "NORMAL",
                                        compra.getQuantidade(),
                                        compra.getValorTotal(),
                                        compra.getDataCompra()
                                });
                            }

                            lblHistorico.setText(String.format(
                                    "Compras: %d | Total: %.2f MT",
                                    compras.size(),
                                    total
                            ));
                        }
                )
        );

        panel.add(lblHistorico, BorderLayout.NORTH);
        panel.add(
                new JScrollPane(tabela(modeloCompras)),
                BorderLayout.CENTER
        );
        panel.add(atualizar, BorderLayout.SOUTH);

        return panel;
    }

    private boolean disponivel(Evento evento) {
        return evento != null
                && evento.isEstado()
                && evento.getData() != null
                && evento.getHora() != null
                && LocalDateTime.of(
                evento.getData(), evento.getHora()
        ).isAfter(LocalDateTime.now());
    }

    private Integer idSelecionado(JTable tabela) {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            return null;
        }

        int indice = tabela.convertRowIndexToModel(linha);

        return ((Number) tabela.getModel().getValueAt(indice, 0))
                .intValue();
    }

    private <T> void executar(
            Callable<T> tarefa,
            Consumer<T> aoConcluir
    ) {
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

                    Throwable causa = e.getCause() != null
                            ? e.getCause() : e;

                    aviso(
                            causa instanceof IllegalStateException
                                    ? causa.getMessage()
                                    : "Não foi possível concluir a operação."
                    );
                }
            }
        }.execute();
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(
                this,
                texto,
                "Atenção",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    private static JTable tabela(DefaultTableModel modelo) {
        // Cores locais: podem ser usadas mesmo durante a criação dos atributos.
        Color azul = new Color(30, 65, 115);
        Color texto = new Color(35, 48, 65);

        JTable tabela = new JTable(modelo);

        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setForeground(texto);
        tabela.setBackground(Color.WHITE);

        // Aumenta o espaço vertical de cada registo.
        tabela.setRowHeight(40);

        // Linhas horizontais discretas.
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(233, 238, 245));
        tabela.setIntercellSpacing(new Dimension(0, 1));

        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabela.setSelectionBackground(new Color(218, 232, 252));
        tabela.setSelectionForeground(new Color(20, 45, 85));
        tabela.setFillsViewportHeight(true);

        // ==================== CABEÇALHO ====================

        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(true);
        tabela.getTableHeader().setPreferredSize(
                new Dimension(0, 44)
        );

        tabela.getTableHeader().setDefaultRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {
                        super.getTableCellRendererComponent(
                                table, value, isSelected,
                                hasFocus, row, column
                        );

                        setText(value == null ? "" : value.toString());
                        setOpaque(true);
                        setBackground(azul);
                        setForeground(Color.WHITE);
                        setFont(new Font("SansSerif", Font.BOLD, 13));
                        setHorizontalAlignment(SwingConstants.LEFT);
                        setBorder(new EmptyBorder(10, 12, 10, 12));

                        return this;
                    }
                }
        );

        // ==================== CÉLULAS ====================

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {
                        super.getTableCellRendererComponent(
                                table, value, isSelected,
                                hasFocus, row, column
                        );

                        String coluna = table.getColumnName(column);
                        String valor = value == null ? "" : value.toString();

                        // Reinicia as propriedades porque o renderer é reutilizado.
                        setText(valor);
                        setOpaque(true);
                        setFont(table.getFont());
                        setHorizontalAlignment(SwingConstants.LEFT);
                        setToolTipText(valor.isEmpty() ? null : valor);

                        // Alterna as cores para facilitar a leitura das linhas.
                        setBackground(
                                isSelected
                                        ? table.getSelectionBackground()
                                        : row % 2 == 0
                                          ? Color.WHITE
                                          : new Color(246, 248, 252)
                        );

                        setForeground(
                                isSelected
                                        ? table.getSelectionForeground()
                                        : texto
                        );

                        // Indicação de foco para navegação pelo teclado.
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(
                                        hasFocus
                                                ? new Color(90, 135, 200)
                                                : getBackground(),
                                        1
                                ),
                                new EmptyBorder(7, 11, 7, 11)
                        ));

                        // Valores numéricos alinhados à direita.
                        if (value instanceof Number) {
                            setHorizontalAlignment(SwingConstants.RIGHT);

                            // Formatação visual dos preços e totais.
                            if (coluna.contains("(MT)")) {
                                setText(String.format(
                                        "%.2f",
                                        ((Number) value).doubleValue()
                                ));
                            }
                        }

                        // Destaca o tipo de bilhete.
                        if ("Tipo".equalsIgnoreCase(coluna)) {
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setFont(table.getFont().deriveFont(Font.BOLD));

                            if (!isSelected
                                    && "VIP".equalsIgnoreCase(valor)) {
                                setForeground(new Color(115, 75, 155));
                            }
                        }

                        // Formata datas quando o modelo contém LocalDateTime.
                        if (value instanceof LocalDateTime) {
                            setText(((LocalDateTime) value).format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy HH:mm"
                                    )
                            ));
                        }

                        return this;
                    }
                };

        // Aplica a apresentação a todas as colunas.
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            javax.swing.table.TableColumn coluna =
                    tabela.getColumnModel().getColumn(i);

            coluna.setCellRenderer(renderer);

            String nome = tabela.getColumnName(i);

            if ("ID".equalsIgnoreCase(nome)
                    || "Compra".equalsIgnoreCase(nome)) {
                coluna.setPreferredWidth(65);

            } else if ("Evento".equalsIgnoreCase(nome)) {
                coluna.setPreferredWidth(230);

            } else if ("Data".equalsIgnoreCase(nome)) {
                coluna.setPreferredWidth(160);

            } else {
                coluna.setPreferredWidth(130);
            }
        }

        return tabela;
    }

    private JToggleButton criarCardEvento(Evento evento) {
        String data = evento.getData().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        String hora = evento.getHora().format(
                DateTimeFormatter.ofPattern("HH:mm")
        );

        JToggleButton card = new JToggleButton();

        card.setLayout(new BorderLayout(0, 12));
        card.setPreferredSize(new Dimension(290, 245));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setContentAreaFilled(true);
        card.setFocusPainted(true);

        card.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        card.setToolTipText(
                "Seleccionar evento: " + evento.getNome()
        );

        // Nome utilizado por tecnologias de acessibilidade.
        card.getAccessibleContext().setAccessibleName(
                "Seleccionar evento " + evento.getNome()
        );

        aplicarEstiloCard(card, false);

        JLabel etiqueta = new JLabel("EVENTO  •  " + data);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 12));
        etiqueta.setForeground(AZUL);

        JLabel titulo = new JLabel(
                "<html><body style='width: 190px'>"
                        + escaparHtml(evento.getNome())
                        + "</body></html>"
        );

        titulo.setFont(new Font("SansSerif", Font.BOLD, 21));
        titulo.setForeground(new Color(30, 41, 59));
        titulo.setVerticalAlignment(SwingConstants.TOP);

        JLabel informacoes = new JLabel(
                "<html>"
                        + "<b>Hora:</b> " + hora
                        + "<br><b>Local:</b> "
                        + escaparHtml(evento.getLocal())
                        + "<br><b>Capacidade:</b> "
                        + evento.getCapacidade() + " pessoas"
                        + "</html>"
        );

        informacoes.setFont(new Font("SansSerif", Font.PLAIN, 13));
        informacoes.setForeground(new Color(100, 116, 139));

        JLabel rodape = new JLabel("Ver bilhetes disponíveis  →");
        rodape.setFont(new Font("SansSerif", Font.BOLD, 13));
        rodape.setForeground(AZUL);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(titulo, BorderLayout.NORTH);
        centro.add(informacoes, BorderLayout.CENTER);

        card.add(etiqueta, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        /*
         * EVENTO DE ITEM:
         * actualiza o destaque quando o cartão é seleccionado
         * ou quando outro cartão passa a estar seleccionado.
         */
        card.addItemListener(e ->
                aplicarEstiloCard(card, card.isSelected())
        );

        /*
         * EVENTO DE ACÇÃO:
         * funciona com clique do rato ou activação pelo teclado.
         */
        card.addActionListener(e -> {
            eventoSelecionadoId = evento.getId();

            modeloBilhetes.setRowCount(0);
            quantidade.setValue(1);

            // O conteúdo é apresentado como texto simples.
            lblEvento.setText(
                    "Bilhetes de: " + evento.getNome()
            );

            carregarBilhetes(evento.getId());
        });

        // EVENTOS DO RATO: efeito visual ao entrar e sair do cartão.
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!card.isSelected()) {
                    card.setBackground(new Color(245, 248, 255));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                aplicarEstiloCard(card, card.isSelected());
            }
        });

        return card;
    }

    private void aplicarEstiloCard(
            JToggleButton card,
            boolean seleccionado
    ) {
        card.setBackground(
                seleccionado
                        ? new Color(234, 240, 255)
                        : Color.WHITE
        );

        /*
         * A borda tem sempre a mesma espessura para evitar
         * que o conteúdo se desloque durante a selecção.
         */
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        seleccionado ? AZUL : BORDA,
                        2,
                        true
                ),
                new EmptyBorder(18, 18, 18, 18)
        ));
    }

    private String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }

        // Evita interpretar caracteres do nome/local como HTML.
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void estilizarBotaoCor(JButton botao, Color normal) {
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setForeground(Color.WHITE);
        botao.setBackground(normal);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);
        botao.setFocusPainted(true);

        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normal.darker()),
                new EmptyBorder(10, 16, 10, 16)
        ));

        botao.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // EVENTO: destaca o botão quando o rato entra.
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (botao.isEnabled()) {
                    botao.setBackground(normal.darker());
                }
            }

            // EVENTO: repõe a cor quando o rato sai.
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(normal);
            }
        });
    }
}