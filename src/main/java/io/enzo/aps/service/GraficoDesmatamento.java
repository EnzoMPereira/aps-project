package io.enzo.aps.service;

import io.enzo.aps.model.RegistroDesmatamento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraficoDesmatamento {

    public static void exibirInterface(Map<String, Double> dadosGeral,
                                       Map<String, Double> dados2023,
                                       Map<String, Double> dados2024,
                                       List<RegistroDesmatamento> registros) {

        // --- Criação dos datasets ---
        DefaultCategoryDataset datasetBarras = criarDatasetBarras(dadosGeral);
        DefaultCategoryDataset datasetLinhas = criarDatasetLinhas(dados2023, dados2024);

        // --- Criação dos gráficos ---
        JFreeChart graficoBarras = ChartFactory.createBarChart(
                "Top 5 Municípios com Maior Desmatamento (2023+2024)",
                "Município", "Área (km²)", datasetBarras,
                PlotOrientation.VERTICAL, true, true, false);

        JFreeChart graficoLinhas = ChartFactory.createLineChart(
                "Comparativo de Desmatamento 2023 × 2024",
                "Município", "Área (km²)", datasetLinhas,
                PlotOrientation.VERTICAL, true, true, false);

        // --- Painéis dos gráficos ---
        ChartPanel painelBarras = new ChartPanel(graficoBarras);
        ChartPanel painelLinhas = new ChartPanel(graficoLinhas);

        painelBarras.setBackground(new Color(240, 248, 245));
        painelLinhas.setBackground(new Color(240, 248, 245));

        // --- Painel de dados ---
        JPanel painelDados = criarPainelDados(registros);

        // --- Layout principal com CardLayout ---
        JPanel painelCentral = new JPanel(new CardLayout());
        painelCentral.add(painelBarras, "barras");
        painelCentral.add(painelLinhas, "linhas");
        painelCentral.add(painelDados, "dados");

        // --- Botões de navegação ---
        JButton btnBarras = criarBotao("📊 Barras", new Color(34, 139, 34));
        JButton btnLinhas = criarBotao("📈 Linhas", new Color(30, 144, 255));
        JButton btnDados = criarBotao("📋 Dados", new Color(255, 165, 0));

        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(new Color(230, 240, 230));
        painelBotoes.setBorder(new EmptyBorder(10, 0, 10, 0));
        painelBotoes.add(btnBarras);
        painelBotoes.add(btnLinhas);
        painelBotoes.add(btnDados);

        // --- Frame principal ---
        JFrame frame = new JFrame("Painel de Análise de Desmatamento 🌎");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(new Color(245, 255, 245));

        // Cabeçalho com título
        JLabel titulo = new JLabel("📊 Painel de Monitoramento do Desmatamento", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setBorder(new EmptyBorder(15, 0, 10, 0));
        frame.add(titulo, BorderLayout.NORTH);

        frame.add(painelCentral, BorderLayout.CENTER);
        frame.add(painelBotoes, BorderLayout.SOUTH);
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);

        // --- Ações dos botões ---
        CardLayout cl = (CardLayout) (painelCentral.getLayout());
        btnBarras.addActionListener(e -> cl.show(painelCentral, "barras"));
        btnLinhas.addActionListener(e -> cl.show(painelCentral, "linhas"));
        btnDados.addActionListener(e -> cl.show(painelCentral, "dados"));

        frame.setVisible(true);
    }

    // ---------- Métodos auxiliares ----------

    private static JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(140, 40));
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Efeito hover
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

    private static DefaultCategoryDataset criarDatasetBarras(Map<String, Double> dados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int count = 0;
        for (Map.Entry<String, Double> entry : dados.entrySet()) {
            dataset.addValue(entry.getValue(), "Área Desmatada (km²)", entry.getKey());
            if (++count >= 5) break;
        }
        return dataset;
    }

    private static DefaultCategoryDataset criarDatasetLinhas(Map<String, Double> dados2023, Map<String, Double> dados2024) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : dados2023.entrySet()) {
            dataset.addValue(entry.getValue(), "2023", entry.getKey());
        }
        for (Map.Entry<String, Double> entry : dados2024.entrySet()) {
            dataset.addValue(entry.getValue(), "2024", entry.getKey());
        }
        return dataset;
    }

    private static JPanel criarPainelDados(List<RegistroDesmatamento> registros) {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(new Color(245, 255, 245));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("📋 Dados de Desmatamento", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(34, 139, 34));
        painel.add(titulo, BorderLayout.NORTH);

        // Tabela e modelo
        String[] colunas = {"Município", "Data", "Estado", "Bioma", "Área (km²)"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(22);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        atualizarTabela(modelo, registros);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 180)));

        // Campo de filtro
        JTextField campoFiltro = new JTextField(20);
        campoFiltro.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JButton btnFiltrar = criarBotao("🔍 Filtrar", new Color(46, 139, 87));
        JButton btnMostrarTudo = criarBotao("🔄 Mostrar Tudo", new Color(105, 105, 105));

        JPanel painelFiltro = new JPanel();
        painelFiltro.setBackground(new Color(230, 240, 230));
        painelFiltro.add(new JLabel("Pesquisar: "));
        painelFiltro.add(campoFiltro);
        painelFiltro.add(btnFiltrar);
        painelFiltro.add(btnMostrarTudo);

        painel.add(painelFiltro, BorderLayout.SOUTH);
        painel.add(scroll, BorderLayout.CENTER);

        // Ações
        btnFiltrar.addActionListener(e -> {
            String termo = campoFiltro.getText().trim().toLowerCase();
            List<RegistroDesmatamento> filtrados = registros.stream()
                    .filter(r ->
                            r.getMunicipio().toLowerCase().contains(termo) ||
                                    r.getBioma().toLowerCase().contains(termo) ||
                                    r.getEstado().toLowerCase().contains(termo))
                    .collect(Collectors.toList());
            atualizarTabela(modelo, filtrados);
        });

        btnMostrarTudo.addActionListener(e -> atualizarTabela(modelo, registros));

        return painel;
    }

    private static void atualizarTabela(DefaultTableModel modelo, List<RegistroDesmatamento> registros) {
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

