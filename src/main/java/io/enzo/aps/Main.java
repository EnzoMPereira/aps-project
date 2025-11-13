package io.enzo.aps;

import java.util.*;
import io.enzo.aps.model.RegistroDesmatamento;
import io.enzo.aps.service.*;

import javax.swing.*;

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
        Map<String, Double> rankingGeral = AnaliseDesmatamento.obterTop5(
                AnaliseDesmatamento.calcularPorMunicipio(todos)
        );

        SwingUtilities.invokeLater(() -> {
            new PainelDesmatamentoUI(rankingGeral, todos).exibir();
        });
    }
}