package view;

import controller.BilheteController;
import controller.CompraController;
import controller.EventoController;
import model.Bilhete;
import model.BilheteVIP;
import model.Compra;
import model.Evento;
import model.Promotor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class PromotorView extends JFrame {

    private final Promotor promotor;

    private final EventoController eventoController =
            new EventoController();

    private final BilheteController bilheteController =
            new BilheteController();

    private final CompraController compraController =
            new CompraController();

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(
                            java.time.format.ResolverStyle.STRICT
                    );

    private final DateTimeFormatter formatoHora =
            DateTimeFormatter.ofPattern("HH:mm")
                    .withResolverStyle(
                            java.time.format.ResolverStyle.STRICT
                    );

    private final DefaultTableModel modeloEventos = modelo(
            "ID", "Nome", "Data", "Hora",
            "Local", "Capacidade", "Estado"
    );

    private final DefaultTableModel modeloBilhetes = modelo(
            "ID", "Tipo", "Preço (MT)", "Stock", "Estado"
    );

    private final DefaultTableModel modeloVendas = modelo(
            "Compra", "Cliente", "Evento", "Tipo",
            "Quantidade", "Total (MT)", "Data"
    );

    private final JTable tabelaEventos = tabela(modeloEventos);
    private final JTable tabelaBilhetes = tabela(modeloBilhetes);

    private final JTextField txtNome = new JTextField();
    private final JTextArea txtDescricao = new JTextArea(3, 20);
    private final JTextField txtLocal = new JTextField();
    private final JTextField txtData = new JTextField();
    private final JTextField txtHora = new JTextField();
    private final JTextField txtCapacidade = new JTextField();

    private final JCheckBox chkActivo =
            new JCheckBox("Evento activo", true);

    private final JComboBox<String> cbTipo =
            new JComboBox<>(new String[]{"NORMAL", "VIP"});

    private final JTextField txtPreco = new JTextField();
    private final JTextField txtQuantidade = new JTextField();

    private final JLabel lblEventoBilhetes =
            new JLabel("Seleccione um evento na primeira aba.");

    private final JLabel lblResumo =
            new JLabel("Clique em Actualizar vendas.");

    private Evento eventoSelecionado;

    private final JComboBox<Evento> cbEventosBilhetes =
            new JComboBox<>();

    private boolean aCarregarEventosBilhetes = false;

    private Evento eventoDosBilhetes() {
        return (Evento) cbEventosBilhetes.getSelectedItem();
    }

    public PromotorView(Promotor promotor) {
        if (promotor == null
                || promotor.getId() <= 0
                || !promotor.isEstado()) {
            throw new IllegalArgumentException(
                    "É necessário um promotor autenticado e activo."
            );
        }

        this.promotor = promotor;

        setTitle("Painel do Promotor — " + promotor.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> {
            new LoginView();
            dispose();
        });

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(
                new JLabel("Bem-vindo, " + promotor.getNome()),
                BorderLayout.WEST
        );
        topo.add(sair, BorderLayout.EAST);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Eventos", painelEventos());
        abas.addTab("Bilhetes do evento", painelBilhetes());
        abas.addTab("Vendas", painelVendas());

        principal.add(topo, BorderLayout.NORTH);
        principal.add(abas, BorderLayout.CENTER);

        setContentPane(principal);

        // Aplica o estilo depois de todos os componentes serem criados.
        aplicarEstiloFormal(principal);

        setVisible(true);

        carregarEventos();
    }

    private JPanel painelEventos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JPanel campos = new JPanel(new GridLayout(0, 2, 10, 10));
        campos.setBorder(
                BorderFactory.createTitledBorder("Dados do evento")
        );

        campos.add(new JLabel("Nome:"));
        campos.add(txtNome);
        campos.add(new JLabel("Descrição:"));
        campos.add(new JScrollPane(txtDescricao));
        campos.add(new JLabel("Local:"));
        campos.add(txtLocal);
        campos.add(new JLabel("Data (dd/MM/aaaa):"));
        campos.add(txtData);
        campos.add(new JLabel("Hora (HH:mm):"));
        campos.add(txtHora);
        campos.add(new JLabel("Capacidade:"));
        campos.add(txtCapacidade);
        campos.add(new JLabel("Estado:"));
        campos.add(chkActivo);

        JButton novo = new JButton("Novo");
        JButton criar = new JButton("Criar");
        JButton atualizar = new JButton("Actualizar");
        JButton remover = new JButton("Remover");
        JButton recarregar = new JButton("Recarregar");

        novo.addActionListener(e -> limparEvento());
        criar.addActionListener(e -> guardarEvento(false));
        atualizar.addActionListener(e -> guardarEvento(true));
        remover.addActionListener(e -> removerEvento());
        recarregar.addActionListener(e -> carregarEventos());

        JPanel botoes = new JPanel(new FlowLayout());
        botoes.add(novo);
        botoes.add(criar);
        botoes.add(atualizar);
        botoes.add(remover);
        botoes.add(recarregar);

        JPanel formulario = new JPanel(new BorderLayout(10, 10));
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(botoes, BorderLayout.SOUTH);

        tabelaEventos.getSelectionModel()
                .addListSelectionListener(e -> {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    Integer id = idSelecionado(tabelaEventos);

                    if (id == null) {
                        return;
                    }

                    executar(
                            () -> eventoController.buscarPorId(id),
                            evento -> {
                                if (!pertenceAoPromotor(evento)) {
                                    aviso("Evento indisponível.");
                                    return;
                                }

                                eventoSelecionado = evento;

                                txtNome.setText(evento.getNome());
                                txtDescricao.setText(evento.getDescricao());
                                txtLocal.setText(evento.getLocal());
                                txtData.setText(
                                        evento.getData().format(formatoData)
                                );
                                txtHora.setText(
                                        evento.getHora().format(formatoHora)
                                );
                                txtCapacidade.setText(
                                        String.valueOf(evento.getCapacidade())
                                );
                                chkActivo.setSelected(evento.isEstado());
                            }
                    );
                });

        panel.add(new JScrollPane(tabelaEventos), BorderLayout.CENTER);
        panel.add(formulario, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarEventos() {
        executar(
                () -> eventoController.listarPorPromotor(promotor.getId()),
                eventos -> {
                    limparEvento();
                    modeloEventos.setRowCount(0);

                    aCarregarEventosBilhetes = true;

                    try {
                        cbEventosBilhetes.removeAllItems();

                        for (Evento evento : eventos) {
                            modeloEventos.addRow(new Object[]{
                                    evento.getId(),
                                    evento.getNome(),
                                    evento.getData().format(formatoData),
                                    evento.getHora().format(formatoHora),
                                    evento.getLocal(),
                                    evento.getCapacidade(),
                                    evento.isEstado() ? "Activo" : "Inactivo"
                            });

                            cbEventosBilhetes.addItem(evento);
                        }

                        cbEventosBilhetes.setSelectedIndex(-1);
                    } finally {
                        aCarregarEventosBilhetes = false;
                    }

                    modeloBilhetes.setRowCount(0);

                    lblEventoBilhetes.setText(
                            eventos.isEmpty()
                                    ? "Ainda não existem eventos deste promotor."
                                    : "Seleccione um evento na lista."
                    );
                }
        );
    }

    private void limparEvento() {
        tabelaEventos.clearSelection();
        eventoSelecionado = null;

        txtNome.setText("");
        txtDescricao.setText("");
        txtLocal.setText("");
        txtData.setText("");
        txtHora.setText("");
        txtCapacidade.setText("");
        chkActivo.setSelected(true);
    }

    private void guardarEvento(boolean edicao) {
        if (edicao && eventoSelecionado == null) {
            aviso("Seleccione um evento para actualizar.");
            return;
        }

        final Evento evento;

        try {
            String nome = txtNome.getText().trim();
            String local = txtLocal.getText().trim();

            LocalDate data = LocalDate.parse(
                    txtData.getText().trim(),
                    formatoData
            );

            LocalTime hora = LocalTime.parse(
                    txtHora.getText().trim(),
                    formatoHora
            );

            int capacidade = Integer.parseInt(
                    txtCapacidade.getText().trim()
            );

            if (nome.isBlank() || local.isBlank() || capacidade <= 0) {
                aviso("Preencha nome, local e capacidade positiva.");
                return;
            }

            if (!LocalDateTime.of(data, hora)
                    .isAfter(LocalDateTime.now())) {
                aviso("Informe uma data e hora futuras.");
                return;
            }

            evento = new Evento(
                    edicao ? eventoSelecionado.getId() : 0,
                    nome,
                    txtDescricao.getText().trim(),
                    local,
                    data,
                    hora,
                    capacidade,
                    promotor,
                    chkActivo.isSelected()
            );

        } catch (DateTimeParseException e) {
            aviso("Use uma data válida dd/MM/aaaa e hora HH:mm.");
            return;

        } catch (NumberFormatException e) {
            aviso("A capacidade deve ser um número inteiro.");
            return;
        }

        executar(() -> {
            if (edicao) {
                Evento atual = eventoController.buscarPorId(evento.getId());

                if (!pertenceAoPromotor(atual)) {
                    throw new IllegalStateException(
                            "O evento não pertence ao promotor."
                    );
                }

                long vendidos = totalVendido(
                        evento.getId(),
                        compraController.listarVendasPorPromotor(
                                promotor.getId()
                        )
                );

                long stock = bilheteController
                        .listarPorEvento(evento.getId())
                        .stream()
                        .mapToLong(Bilhete::getQuantidade)
                        .sum();

                if (evento.getCapacidade() < vendidos + stock) {
                    throw new IllegalStateException(
                            "A capacidade não pode ser inferior à soma "
                                    + "dos bilhetes vendidos e do stock existente."
                    );
                }

                return eventoController.atualizar(evento);
            }

            return eventoController.criar(evento);
        }, sucesso -> {
            if (sucesso) {
                mensagem("Evento guardado com sucesso.");
                carregarEventos();
            } else {
                aviso("Não foi possível guardar o evento.");
            }
        });
    }

    private void removerEvento() {
        if (eventoSelecionado == null) {
            aviso("Seleccione um evento.");
            return;
        }

        int id = eventoSelecionado.getId();

        if (JOptionPane.showConfirmDialog(
                this,
                "Remover o evento seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) {
            return;
        }

        executar(() -> {
            Evento atual = eventoController.buscarPorId(id);

            if (!pertenceAoPromotor(atual)) {
                return false;
            }

            // Evita perder o histórico de vendas.
            boolean temCompras = compraController
                    .listarVendasPorPromotor(promotor.getId())
                    .stream()
                    .anyMatch(c ->
                            c.getBilhete().getEvento().getId() == id
                    );

            if (temCompras) {
                throw new IllegalStateException(
                        "O evento tem vendas. Desactive-o "
                                + "através da edição para preservar o histórico."
                );
            }

            if (!bilheteController.listarPorEvento(id).isEmpty()) {
                throw new IllegalStateException(
                        "O evento tem bilhetes associados. "
                                + "Pode desactivá-lo através da edição."
                );
            }

            return eventoController.eliminar(id);
        }, sucesso -> {
            if (sucesso) {
                mensagem("Evento removido.");
                carregarEventos();
            } else {
                aviso("Não foi possível remover o evento.");
            }
        });
    }

    private JPanel painelBilhetes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        cbEventosBilhetes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Evento) {
                    Evento evento = (Evento) value;

                    setText(
                            evento.getId() + " - " + evento.getNome()
                    );
                } else {
                    setText("Seleccione um evento...");
                }

                return this;
            }
        });

        cbEventosBilhetes.addActionListener(e -> {
            if (aCarregarEventosBilhetes) {
                return;
            }

            modeloBilhetes.setRowCount(0);
            txtPreco.setText("");
            txtQuantidade.setText("");

            Evento evento = eventoDosBilhetes();

            if (evento == null) {
                lblEventoBilhetes.setText("Seleccione um evento na lista.");
                return;
            }

            lblEventoBilhetes.setText(
                    "Evento: " + evento.getNome()
                            + " | Capacidade: " + evento.getCapacidade()
            );

            carregarBilhetes();
        });

        JPanel selector = new JPanel(new BorderLayout(10, 10));
        selector.add(new JLabel("Evento:"), BorderLayout.WEST);
        selector.add(cbEventosBilhetes, BorderLayout.CENTER);

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        topo.add(selector, BorderLayout.NORTH);
        topo.add(lblEventoBilhetes, BorderLayout.SOUTH);

        lblEventoBilhetes.setText("Seleccione um evento na lista.");

        JPanel campos = new JPanel(new GridLayout(0, 2, 10, 10));
        campos.add(new JLabel("Tipo:"));
        campos.add(cbTipo);
        campos.add(new JLabel("Preço (MT):"));
        campos.add(txtPreco);
        campos.add(new JLabel("Quantidade a disponibilizar:"));
        campos.add(txtQuantidade);

        JButton carregar = new JButton("Carregar bilhetes");
        JButton criar = new JButton("Criar bilhetes");
        JButton desativar = new JButton("Desactivar seleccionado");
        JButton reativar = new JButton("Reactivar seleccionado");

        carregar.addActionListener(e -> carregarBilhetes());
        criar.addActionListener(e -> criarBilhetes());
        desativar.addActionListener(e -> alterarEstadoBilhete(false));
        reativar.addActionListener(e -> alterarEstadoBilhete(true));

        JPanel botoes = new JPanel(new FlowLayout());
        botoes.add(carregar);
        botoes.add(criar);
        botoes.add(desativar);
        botoes.add(reativar);

        JPanel rodape = new JPanel(new BorderLayout(10, 10));
        rodape.add(campos, BorderLayout.CENTER);
        rodape.add(botoes, BorderLayout.SOUTH);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabelaBilhetes), BorderLayout.CENTER);
        panel.add(rodape, BorderLayout.SOUTH);

        return panel;
    }

    private void criarBilhetes() {
        Evento seleccionado = eventoDosBilhetes();

        if (seleccionado == null) {
            aviso("Seleccione um evento na lista.");
            return;
        }

        final double preco;
        final int quantidade;

        try {
            preco = Double.parseDouble(
                    txtPreco.getText().trim().replace(',', '.')
            );

            quantidade = Integer.parseInt(
                    txtQuantidade.getText().trim()
            );

            if (!Double.isFinite(preco) || preco < 0 || quantidade <= 0) {
                aviso("Informe um preço válido e quantidade positiva.");
                return;
            }
        } catch (NumberFormatException e) {
            aviso("Preço ou quantidade inválidos.");
            return;
        }

        int eventoId = seleccionado.getId();
        boolean vip = "VIP".equals(cbTipo.getSelectedItem());

        executar(() -> {
            Evento evento = eventoController.buscarPorId(eventoId);

            if (!pertenceAoPromotor(evento)
                    || !evento.isEstado()
                    || !LocalDateTime.of(
                    evento.getData(), evento.getHora()
            ).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException(
                        "O evento deve estar activo e ter data futura."
                );
            }

            long stock = bilheteController.listarPorEvento(eventoId)
                    .stream()
                    .mapToLong(Bilhete::getQuantidade)
                    .sum();

            long vendidos = totalVendido(
                    eventoId,
                    compraController.listarVendasPorPromotor(
                            promotor.getId()
                    )
            );

            if (vendidos + stock + quantidade > evento.getCapacidade()) {
                throw new IllegalStateException(
                        "A quantidade excede a capacidade disponível."
                );
            }

            return vip
                    ? bilheteController.criarVIP(preco, quantidade, evento)
                    : bilheteController.criarNormal(
                    preco, quantidade, evento
            );

        }, sucesso -> {
            if (sucesso) {
                txtPreco.setText("");
                txtQuantidade.setText("");
                mensagem("Bilhetes criados com sucesso.");
                carregarBilhetes();
            } else {
                aviso("Não foi possível criar os bilhetes.");
            }
        });
    }

    private void alterarEstadoBilhete(boolean estado) {
        Integer id = idSelecionado(tabelaBilhetes);
        Evento seleccionado = eventoDosBilhetes();

        if (id == null || seleccionado == null) {
            aviso("Seleccione um evento e um bilhete.");
            return;
        }

        int eventoId = seleccionado.getId();

        //int eventoId = eventoSelecionado.getId();

        executar(() -> {
            Bilhete bilhete = bilheteController.buscarPorId(id);

            if (bilhete == null
                    || bilhete.getEvento().getId() != eventoId
                    || !pertenceAoPromotor(
                    eventoController.buscarPorId(eventoId)
            )) {
                return false;
            }

            return bilheteController.alterarEstado(id, estado);

        }, sucesso -> {
            if (sucesso) {
                carregarBilhetes();
            } else {
                aviso("Não foi possível alterar o estado.");
            }
        });
    }

    private void carregarBilhetes() {
        Evento evento = eventoDosBilhetes();

        if (evento == null) {
            aviso("Seleccione um evento na lista.");
            return;
        }

        int id = evento.getId();

        executar(
                () -> bilheteController.listarPorEvento(id),
                bilhetes -> {
                    // Ignora resultados de uma selecção anterior.
                    Evento actual = eventoDosBilhetes();

                    if (actual == null || actual.getId() != id) {
                        return;
                    }

                    modeloBilhetes.setRowCount(0);

                    for (Bilhete bilhete : bilhetes) {
                        modeloBilhetes.addRow(new Object[]{
                                bilhete.getId(),
                                bilhete instanceof BilheteVIP
                                        ? "VIP" : "NORMAL",
                                bilhete.getPreco(),
                                bilhete.getQuantidade(),
                                bilhete.isEstado() ? "Activo" : "Inactivo"
                        });
                    }
                }
        );
    }

    private JPanel painelVendas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JButton atualizar = new JButton("Actualizar vendas");

        atualizar.addActionListener(e ->
                executar(
                        () -> compraController.listarVendasPorPromotor(
                                promotor.getId()
                        ),
                        compras -> {
                            modeloVendas.setRowCount(0);

                            long quantidade = 0;
                            double receita = 0;

                            for (Compra compra : compras) {
                                Bilhete bilhete = compra.getBilhete();

                                quantidade += compra.getQuantidade();
                                receita += compra.getValorTotal();

                                modeloVendas.addRow(new Object[]{
                                        compra.getId(),
                                        compra.getCliente().getNome(),
                                        bilhete.getEvento().getNome(),
                                        bilhete instanceof BilheteVIP
                                                ? "VIP" : "NORMAL",
                                        compra.getQuantidade(),
                                        compra.getValorTotal(),
                                        compra.getDataCompra()
                                });
                            }

                            lblResumo.setText(String.format(
                                    "Compras: %d | Bilhetes vendidos: %d | "
                                            + "Receita bruta: %.2f MT",
                                    compras.size(), quantidade, receita
                            ));
                        }
                )
        );

        panel.add(lblResumo, BorderLayout.NORTH);
        panel.add(
                new JScrollPane(tabela(modeloVendas)),
                BorderLayout.CENTER
        );
        panel.add(atualizar, BorderLayout.SOUTH);

        return panel;
    }

    private long totalVendido(int eventoId, List<Compra> compras) {
        return compras.stream()
                .filter(c ->
                        c.getBilhete().getEvento().getId() == eventoId
                )
                .mapToLong(Compra::getQuantidade)
                .sum();
    }

    private boolean pertenceAoPromotor(Evento evento) {
        return evento != null
                && evento.getPromotor() != null
                && evento.getPromotor().getId() == promotor.getId();
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
                this, texto, "Atenção",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mensagem(String texto) {
        JOptionPane.showMessageDialog(this, texto);
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
        JTable tabela = new JTable(modelo);

        estilizarTabela(tabela);

        return tabela;
    }





    /*
     * Percorre os componentes da tela e aplica uma aparência uniforme.
     * Não altera os ActionListeners nem as regras de negócio.
     */
    private void aplicarEstiloFormal(Component componente) {
        Color texto = new Color(30, 41, 59);
        Color fundo = new Color(248, 250, 252);

        componente.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        componente.setForeground(texto);

        if (componente instanceof JButton) {
            estilizarBotaoFormal((JButton) componente);

            // Não percorre os componentes internos do botão.
            return;
        }

        if (componente instanceof javax.swing.text.JTextComponent) {
            estilizarCampoFormal(
                    (javax.swing.text.JTextComponent) componente
            );
            return;
        }

        if (componente instanceof JComboBox<?>) {
            JComboBox<?> combo = (JComboBox<?>) componente;

            combo.setBackground(Color.WHITE);
            combo.setForeground(texto);

            // Mantém o renderer existente que apresenta os nomes dos eventos.
            combo.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(203, 213, 225)
                    )
            );

            Dimension tamanho = combo.getPreferredSize();

            combo.setPreferredSize(
                    new Dimension(tamanho.width, Math.max(36, tamanho.height))
            );

            return;
        }

        if (componente instanceof JTable) {
            estilizarTabela((JTable) componente);
            return;
        }

        if (componente instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) componente;

            scroll.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(220, 227, 236)
                    )
            );

            scroll.getViewport().setBackground(Color.WHITE);
            scroll.getVerticalScrollBar().setUnitIncrement(18);
        }

        if (componente instanceof JCheckBox) {
            JCheckBox check = (JCheckBox) componente;

            check.setOpaque(false);
            check.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );

            return;
        }

        if (componente instanceof JTabbedPane) {
            JTabbedPane abas = (JTabbedPane) componente;

            abas.setFont(new Font("SansSerif", Font.BOLD, 14));
            abas.setBackground(new Color(232, 238, 246));
            abas.setForeground(new Color(25, 50, 90));
        }

        if (componente instanceof JPanel) {
            JPanel painel = (JPanel) componente;
            painel.setBackground(fundo);

            // Uniformiza os títulos das secções, como "Dados do evento".
            if (painel.getBorder()
                    instanceof javax.swing.border.TitledBorder) {

                javax.swing.border.TitledBorder borda =
                        (javax.swing.border.TitledBorder) painel.getBorder();

                borda.setTitleFont(
                        new Font("SansSerif", Font.BOLD, 14)
                );
                borda.setTitleColor(new Color(25, 50, 90));
                borda.setBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 227, 236)
                        )
                );
            }
        }

        // Aplica também aos componentes contidos nos painéis e nas abas.
        if (componente instanceof Container) {
            for (Component filho :
                    ((Container) componente).getComponents()) {
                aplicarEstiloFormal(filho);
            }
        }
    }

    /*
     * Botões com cores associadas à finalidade da operação.
     * Mantém os eventos de acção já existentes.
     */
    private void estilizarBotaoFormal(JButton botao) {
        String nome = botao.getText()
                .toLowerCase(java.util.Locale.ROOT);

        final Color normal;
        final Color destaque;
        final Color corTexto;

        if (nome.contains("remover") || nome.equals("sair")) {
            // Acções destrutivas ou de saída.
            normal = new Color(170, 45, 60);
            destaque = new Color(140, 30, 45);
            corTexto = Color.WHITE;

        } else if (nome.contains("desactivar")) {
            // Operação que exige atenção.
            normal = new Color(140, 85, 25);
            destaque = new Color(115, 65, 15);
            corTexto = Color.WHITE;

        } else if (nome.contains("criar")
                || nome.contains("reactivar")) {
            // Operações principais.
            normal = new Color(30, 65, 115);
            destaque = new Color(20, 45, 85);
            corTexto = Color.WHITE;

        } else {
            // Operações secundárias: novo, carregar e actualizar.
            normal = new Color(232, 238, 246);
            destaque = new Color(213, 224, 240);
            corTexto = new Color(30, 55, 90);
        }

        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setForeground(corTexto);
        botao.setBackground(normal);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);

        // O foco será destacado pela borda personalizada.
        botao.setFocusPainted(false);

        botao.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        Runnable actualizar = () -> {
            boolean focado = botao.isFocusOwner();

            Color borda = focado
                    ? new Color(65, 115, 195)
                    : normal.darker();

            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borda, 2, true),
                    new EmptyBorder(8, 12, 8, 12)
            ));

            if (!botao.isEnabled()) {
                botao.setBackground(new Color(225, 229, 235));
                botao.setForeground(new Color(110, 120, 135));

            } else {
                botao.setForeground(corTexto);

                if (botao.getModel().isPressed()
                        && botao.getModel().isArmed()) {
                    botao.setBackground(destaque.darker());

                } else if (botao.getModel().isRollover()) {
                    botao.setBackground(destaque);

                } else {
                    botao.setBackground(normal);
                }
            }
        };

        botao.setRolloverEnabled(true);

        // EVENTO DO MODELO:
        // detecta hover, pressão e activação pelo teclado.
        botao.getModel().addChangeListener(e -> actualizar.run());

        // EVENTOS DE FOCO: destaque ao navegar com Tab.
        botao.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                actualizar.run();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                actualizar.run();
            }
        });

        actualizar.run();
    }

    /*
     * Aplica o mesmo estilo aos JTextFields e à descrição.
     */
    private void estilizarCampoFormal(
            javax.swing.text.JTextComponent campo
    ) {
        Color bordaNormal = new Color(203, 213, 225);
        Color bordaFoco = new Color(55, 100, 170);

        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(new Color(30, 41, 59));
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(new Color(30, 65, 115));

        campo.setSelectionColor(new Color(205, 223, 250));
        campo.setSelectedTextColor(new Color(20, 40, 75));

        aplicarBordaCampoFormal(campo, bordaNormal);

        // EVENTO DE FOCO: destaca o campo seleccionado.
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                aplicarBordaCampoFormal(campo, bordaFoco);
                campo.setBackground(new Color(247, 250, 255));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                aplicarBordaCampoFormal(campo, bordaNormal);
                campo.setBackground(Color.WHITE);
            }
        });
    }

    private void aplicarBordaCampoFormal(
            javax.swing.text.JTextComponent campo,
            Color cor
    ) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
    }

    private static void estilizarTabela(JTable tabela) {
        Color azul = new Color(30, 65, 115);
        Color texto = new Color(35, 48, 65);

        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setForeground(texto);
        tabela.setBackground(Color.WHITE);

        // Linhas mais altas para melhorar a leitura.
        tabela.setRowHeight(40);

        // Mantém apenas divisórias horizontais discretas.
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(233, 238, 245));
        tabela.setIntercellSpacing(new Dimension(0, 1));

        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabela.setSelectionBackground(new Color(218, 232, 252));
        tabela.setSelectionForeground(new Color(20, 45, 85));

        // Preenche também o espaço vazio abaixo dos registos.
        tabela.setFillsViewportHeight(true);

        // ==================== CABEÇALHO ====================

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setReorderingAllowed(false);
        cabecalho.setResizingAllowed(true);
        cabecalho.setPreferredSize(new Dimension(0, 44));

        /*
         * Renderer próprio para garantir o fundo do cabeçalho,
         * mesmo quando o tema do Swing usa outras cores.
         */
        cabecalho.setDefaultRenderer(new DefaultTableCellRenderer() {
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
                        table, value, isSelected, hasFocus, row, column
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
        });

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
                                table, value, isSelected, hasFocus, row, column
                        );

                        String coluna = table.getColumnName(column);
                        String valor = value == null ? "" : value.toString();

                        // Reinicia as propriedades: o renderer é reutilizado.
                        setText(valor);
                        setOpaque(true);
                        setFont(table.getFont());
                        setHorizontalAlignment(SwingConstants.LEFT);
                        setToolTipText(valor.isEmpty() ? null : valor);

                        setForeground(
                                isSelected
                                        ? table.getSelectionForeground()
                                        : texto
                        );

                        // Alterna o fundo entre branco e cinzento muito claro.
                        setBackground(
                                isSelected
                                        ? table.getSelectionBackground()
                                        : row % 2 == 0
                                          ? Color.WHITE
                                          : new Color(246, 248, 252)
                        );

                        // Mantém uma indicação de foco para navegação por teclado.
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(
                                        hasFocus
                                                ? new Color(90, 135, 200)
                                                : getBackground(),
                                        1
                                ),
                                new EmptyBorder(7, 11, 7, 11)
                        ));

                        // Formata visualmente os valores monetários.
                        // O valor original no modelo continua a ser um Number.
                        if (value instanceof Number
                                && coluna.contains("(MT)")) {
                            setText(String.format(
                                    "%.2f",
                                    ((Number) value).doubleValue()
                            ));

                            setHorizontalAlignment(SwingConstants.RIGHT);

                        } else if (value instanceof Number) {
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        }

                        // Destaca o estado sem alterar os dados do modelo.
                        if ("Estado".equalsIgnoreCase(coluna)) {
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setFont(table.getFont().deriveFont(Font.BOLD));

                            if (!isSelected) {
                                if ("Activo".equalsIgnoreCase(valor)) {
                                    setForeground(new Color(25, 115, 75));
                                    setBackground(new Color(231, 246, 237));

                                } else if ("Inactivo".equalsIgnoreCase(valor)) {
                                    setForeground(new Color(165, 50, 60));
                                    setBackground(new Color(252, 235, 237));
                                }
                            }
                        }

                        // Diferencia os tipos de bilhete.
                        if ("Tipo".equalsIgnoreCase(coluna)) {
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setFont(table.getFont().deriveFont(Font.BOLD));

                            if (!isSelected && "VIP".equalsIgnoreCase(valor)) {
                                setForeground(new Color(115, 75, 155));
                            }
                        }

                        return this;
                    }
                };

        /*
         * Aplica o renderer a cada coluna.
         * Funciona para Eventos, Bilhetes e Vendas.
         */
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);

            String nome = tabela.getColumnName(i);

            if ("ID".equalsIgnoreCase(nome)) {
                tabela.getColumnModel().getColumn(i)
                        .setPreferredWidth(55);

            } else if ("Nome".equalsIgnoreCase(nome)
                    || "Evento".equalsIgnoreCase(nome)
                    || "Cliente".equalsIgnoreCase(nome)
                    || "Local".equalsIgnoreCase(nome)) {
                tabela.getColumnModel().getColumn(i)
                        .setPreferredWidth(180);

            } else {
                tabela.getColumnModel().getColumn(i)
                        .setPreferredWidth(110);
            }
        }
    }
}