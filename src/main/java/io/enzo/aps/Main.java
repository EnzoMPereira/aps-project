package io.enzo.aps;

import java.util.*;
import io.enzo.aps.model.RegistroDesmatamento;
import io.enzo.aps.service.*;

public class Main {
    public static void main(String[] args) {
        // Lê os dados dos dois anos
        List<RegistroDesmatamento> dados2023 = LeitorCSV.lerCSV("/home/enzo/Downloads/focos_br_ap_ref_2023.csv");
        List<RegistroDesmatamento> dados2024 = LeitorCSV.lerCSV("/home/enzo/Downloads/focos_br_ap_ref_2024.csv");

        // Junta todos os registros
        List<RegistroDesmatamento> todos = new ArrayList<>();
        todos.addAll(dados2023);
        todos.addAll(dados2024);

        // Calcula totais
        Map<String, Double> porMunicipio2023 = AnaliseDesmatamento.calcularPorMunicipio(dados2023);
        Map<String, Double> porMunicipio2024 = AnaliseDesmatamento.calcularPorMunicipio(dados2024);
        Map<String, Double> rankingGeral = AnaliseDesmatamento.obterTop5(
                AnaliseDesmatamento.calcularPorMunicipio(todos)
        );

        // Exibe a interface completa
        GraficoDesmatamento.exibirInterface(rankingGeral, porMunicipio2023, porMunicipio2024, todos);
    }
}