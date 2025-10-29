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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GraficoDesmatamento {

    public static void exibirInterface(Map<String, Double> dadosGeral,
                                       List<RegistroDesmatamento> registros) {

        // --- Datasets ---
        DefaultCategoryDataset datasetBarras = criarDatasetBarras(dadosGeral);
        DefaultCategoryDataset datasetLinhas = criarDatasetLinhasPorMes(registros);

        // --- Gráficos ---
        JFreeChart graficoBarras = ChartFactory.createBarChart(
                "Top 5 Municípios com Maior Desmatamento (2023 + 2024)",
                "Município", "Área (km²)", datasetBarras,
                PlotOrientation.VERTICAL, true, true, false);

        JFreeChart graficoLinhas = ChartFactory.createLineChart(
                "Comparativo Mensal de Desmatamento (2023 × 2024)",
                "Mês", "Área (km²)", datasetLinhas,
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

    private static DefaultCategoryDataset criarDatasetLinhasPorMes(List<RegistroDesmatamento> registros) {
        Map<Integer, Double> dados2023 = agruparPorMes(registros, "2023");
        Map<Integer, Double> dados2024 = agruparPorMes(registros, "2024");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        for (int mes = 1; mes <= 12; mes++) {
            dataset.addValue(dados2023.getOrDefault(mes, 0.0), "2023", nomesMeses[mes - 1]);
            dataset.addValue(dados2024.getOrDefault(mes, 0.0), "2024", nomesMeses[mes - 1]);
        }

        return dataset;
    }

    private static Map<Integer, Double> agruparPorMes(List<RegistroDesmatamento> registros, String ano) {
        return registros.stream()
                .filter(r -> r.getData() != null && r.getData().startsWith(ano))
                .collect(Collectors.groupingBy(
                        r -> Integer.parseInt(r.getData().split("-")[1]),
                        Collectors.summingDouble(RegistroDesmatamento::getAreaKm2)
                ));
    }

    private static JPanel criarPainelDados(List<RegistroDesmatamento> registros) {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(new Color(245, 255, 245));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("📋 Dados de Desmatamento", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(34, 139, 34));
        painel.add(titulo, BorderLayout.NORTH);

        // ---------- TABELA ----------
        String[] colunas = {"Município", "Data", "Estado", "Bioma", "Área (km²)"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(22);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        atualizarTabela(modelo, registros);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 180)));

        // ---------- COMBOBOX DE MESES ----------
        String[] meses = {"Todos", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        JComboBox<String> comboMeses = new JComboBox<>(meses);
        comboMeses.setFont(new Font("SansSerif", Font.BOLD, 13));
        comboMeses.setPreferredSize(new Dimension(150, 30));

        // Botão Mostrar Tudo
        JButton btnMostrarTudo = criarBotao("🔄 Mostrar Tudo", new Color(105, 105, 105));
        btnMostrarTudo.setPreferredSize(new Dimension(150, 30));

        // Painel lateral
        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new GridLayout(2, 1, 5, 5));
        painelLateral.setBackground(new Color(230, 240, 230));
        painelLateral.add(comboMeses);
        painelLateral.add(btnMostrarTudo);
        painelLateral.setPreferredSize(new Dimension(170, 80));

        painel.add(scroll, BorderLayout.CENTER);
        painel.add(painelLateral, BorderLayout.WEST);

        // ---------- AÇÕES ----------
        comboMeses.addActionListener(e -> {
            int indice = comboMeses.getSelectedIndex();
            if (indice == 0) {
                atualizarTabela(modelo, registros);
            } else {
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
                atualizarTabela(modelo, filtrados);
            }
        });

        btnMostrarTudo.addActionListener(e -> comboMeses.setSelectedIndex(0));

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