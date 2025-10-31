package io.enzo.aps.service;

import io.enzo.aps.model.RegistroDesmatamento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraficoBuilder {

    public static ChartPanel criarGraficoBarras(Map<String, Double> dados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int count = 0;
        for (Map.Entry<String, Double> entry : dados.entrySet()) {
            dataset.addValue(entry.getValue(), "Área Desmatada (km²)", entry.getKey());
            if (++count >= 5) break;
        }

        JFreeChart grafico = ChartFactory.createBarChart(
                "Top 5 Municípios com Maior Desmatamento (2023 + 2024)",
                "Município", "Área (km²)", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        return new ChartPanel(grafico);
    }

    public static ChartPanel criarGraficoLinhas(List<RegistroDesmatamento> registros) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<Integer, Double> dados2023 = agruparPorMes(registros, "2023");
        Map<Integer, Double> dados2024 = agruparPorMes(registros, "2024");

        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        for (int i = 1; i <= 12; i++) {
            dataset.addValue(dados2023.getOrDefault(i, 0.0), "2023", meses[i - 1]);
            dataset.addValue(dados2024.getOrDefault(i, 0.0), "2024", meses[i - 1]);
        }

        JFreeChart grafico = ChartFactory.createLineChart(
                "Comparativo Mensal de Desmatamento (2023 × 2024)",
                "Mês", "Área (km²)", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        return new ChartPanel(grafico);
    }

    private static Map<Integer, Double> agruparPorMes(List<RegistroDesmatamento> registros, String ano) {
        return registros.stream()
                .filter(r -> r.getData() != null && r.getData().startsWith(ano))
                .collect(Collectors.groupingBy(
                        r -> Integer.parseInt(r.getData().split("-")[1]),
                        Collectors.summingDouble(RegistroDesmatamento::getAreaKm2)
                ));
    }
}
