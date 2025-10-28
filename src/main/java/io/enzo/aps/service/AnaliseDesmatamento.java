package io.enzo.aps.service;

import java.util.*;
import io.enzo.aps.model.RegistroDesmatamento;

public class AnaliseDesmatamento {

    public static double calcularTotal(List<RegistroDesmatamento> lista) {
        return lista.stream().mapToDouble(RegistroDesmatamento::getAreaKm2).sum();
    }

    public static Map<String, Double> calcularPorMunicipio(List<RegistroDesmatamento> lista) {
        Map<String, Double> mapa = new HashMap<>();
        for (RegistroDesmatamento r : lista) {
            mapa.put(r.getMunicipio(),
                    mapa.getOrDefault(r.getMunicipio(), 0.0) + r.getAreaKm2());
        }
        return mapa;
    }

    public static void exibirRanking(Map<String, Double> mapa) {
        mapa.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue() + " km²"));
    }

    public static Map<String, Double> obterTop5(Map<String, Double> mapa) {
        Map<String, Double> top5 = new LinkedHashMap<>();
        mapa.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> top5.put(e.getKey(), e.getValue()));
        return top5;
    }
}
