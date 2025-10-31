package io.enzo.aps.service;

import io.enzo.aps.model.RegistroDesmatamento;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PainelDesmatamentoUI {

    private final Map<Integer, List<RegistroDesmatamento>> registrosFiltrados;
    private final List<RegistroDesmatamento> registros;
    private final Map<String, Double> dadosGeral;

    public PainelDesmatamentoUI(Map<String, Double> dadosGeral, List<RegistroDesmatamento> registros) {
        this.registros = registros;
        this.dadosGeral = dadosGeral;
        this.registrosFiltrados = preProcessarRegistros(registros);
    }

    private Map<Integer, List<RegistroDesmatamento>> preProcessarRegistros(List<RegistroDesmatamento> registros) {
        return registros.stream()
                .filter(r -> r.getData() != null && r.getData().contains("-"))
                .collect(Collectors.groupingBy(r -> Integer.parseInt(r.getData().split("-")[1])));
    }

    public void exibir() {
        JFrame frame = new JFrame("Painel de Análise de Desmatamento 🌎");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(new Color(245, 255, 245));

        JLabel titulo = new JLabel("📊 Painel de Monitoramento do Desmatamento", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setBorder(new EmptyBorder(15, 0, 10, 0));

        // Painéis com gráficos e dados
        JPanel painelCentral = new JPanel(new CardLayout());
        painelCentral.add(criarPainelBarras(), "barras");
        painelCentral.add(criarPainelLinhas(), "linhas");
        painelCentral.add(criarPainelDados(), "dados");

        // Botões de navegação
        JButton btnBarras = criarBotao("📊 Barras", new Color(34, 139, 34));
        JButton btnLinhas = criarBotao("📈 Linhas", new Color(30, 144, 255));
        JButton btnDados = criarBotao("📋 Dados", new Color(255, 165, 0));

        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(new Color(230, 240, 230));
        painelBotoes.setBorder(new EmptyBorder(10, 0, 10, 0));
        painelBotoes.add(btnBarras);
        painelBotoes.add(btnLinhas);
        painelBotoes.add(btnDados);

        CardLayout cl = (CardLayout) painelCentral.getLayout();
        btnBarras.addActionListener(e -> cl.show(painelCentral, "barras"));
        btnLinhas.addActionListener(e -> cl.show(painelCentral, "linhas"));
        btnDados.addActionListener(e -> cl.show(painelCentral, "dados"));

        // Montagem final
        frame.add(titulo, BorderLayout.NORTH);
        frame.add(painelCentral, BorderLayout.CENTER);
        frame.add(painelBotoes, BorderLayout.SOUTH);

        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ---------- PAINÉIS DE CONTEÚDO ----------

    private JPanel criarPainelBarras() {
        ChartPanel graficoBarras = GraficoBuilder.criarGraficoBarras(dadosGeral);
        graficoBarras.setBackground(new Color(240, 248, 245));
        return graficoBarras;
    }

    private JPanel criarPainelLinhas() {
        ChartPanel graficoLinhas = GraficoBuilder.criarGraficoLinhas(registros);
        graficoLinhas.setBackground(new Color(240, 248, 245));
        return graficoLinhas;
    }

    private JPanel criarPainelDados() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(new Color(245, 255, 245));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("📋 Dados de Desmatamento", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(34, 139, 34));
        painel.add(titulo, BorderLayout.NORTH);

        String[] colunas = {"Município", "Data", "Estado", "Bioma", "Área (km²)"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(22);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        atualizarTabela(modelo, registros);
        JScrollPane scroll = new JScrollPane(tabela);

        // ---------- COMBOBOX ----------
        String[] meses = {"Todos", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        JComboBox<String> comboMeses = new JComboBox<>(meses);
        comboMeses.setFont(new Font("SansSerif", Font.BOLD, 13));
        comboMeses.setPreferredSize(new Dimension(160, 30));

        JLabel lblFiltro = new JLabel("Filtrar por mês:");
        lblFiltro.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel painelLateral = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 20));
        painelLateral.setBackground(new Color(230, 240, 230));
        painelLateral.add(lblFiltro);
        painelLateral.add(comboMeses);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelLateral, scroll);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        painel.add(splitPane, BorderLayout.CENTER);

        // ---------- AÇÃO DO FILTRO ----------
        comboMeses.addActionListener(e -> {
            int indice = comboMeses.getSelectedIndex();
            if (indice == 0) {
                atualizarTabela(modelo, registros);
            } else {
                long startTime = System.nanoTime();
                List<RegistroDesmatamento> filtrados = registros.stream()
                        .filter(r -> {
                            try {
                                String[] partes = r.getData().split("-");
                                if (partes.length < 2) return false;
                                int mesRegistro = Integer.parseInt(partes[1]);
                                return mesRegistro == indice;
                            } catch (Exception ex) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
                long endTime = System.nanoTime();
                System.out.println("Mes: " + indice + ", qtd registros filtrados: " + filtrados.size() + ", Tempo de execução do O(n): " + (endTime - startTime) + " nanosegundos");

                startTime = System.nanoTime();
                filtrados = registrosFiltrados.getOrDefault(indice, List.of());
                endTime = System.nanoTime();
                System.out.println("Mes: " + indice + ", qtd registros filtrados: " + filtrados.size() + ", Tempo de execução do O(1): " + (endTime - startTime) + " nanosegundos");



//                List<RegistroDesmatamento> filtrados = registrosFiltrados.getOrDefault(indice, List.of());
                atualizarTabela(modelo, filtrados);
            }
        });

        return painel;
    }

    // ---------- UTILITÁRIOS ----------

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(140, 40));
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor);
            }
        });
        return botao;
    }

    private void atualizarTabela(DefaultTableModel modelo, List<RegistroDesmatamento> registros) {
        modelo.setRowCount(0);
        for (RegistroDesmatamento r : registros) {
            modelo.addRow(new Object[]{
                    r.getMunicipio(),
                    r.getData(),
                    r.getEstado(),
                    r.getBioma(),
                    String.format("%.2f", r.getAreaKm2())
            });
        }
    }
}
