package io.enzo.aps.service;

import java.util.*;
import io.enzo.aps.model.RegistroDesmatamento;

public class AnaliseDesmatamento {

    public static Map<String, Double> calcularPorMunicipio(List<RegistroDesmatamento> lista) {
        Map<String, Double> mapa = new HashMap<>();
        for (RegistroDesmatamento r : lista) {
            mapa.put(r.getMunicipio(),
                    mapa.getOrDefault(r.getMunicipio(), 0.0) + r.getAreaKm2());
        }
        return mapa;
    }
    public static int getSizeList(List<RegistroDesmatamento> lista) {
        return lista.size();
    }

    public static Map<String, Double> obterTop5(Map<String, Double> mapa) {
        Map<String, Double> top5 = new LinkedHashMap<>();
        mapa.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> top5.put(e.getKey(), e.getValue()));
        return top5;
    }

    public static List<Map<String, Double>> contarRegistrosPorMunicipio(List<RegistroDesmatamento> registros) {

        // Mapa auxiliar para acumular a contagem
        Map<String, Integer> contador = new HashMap<>();

        for (RegistroDesmatamento r : registros) {
            String municipio = r.getMunicipio();

            // soma +1 para cada município
            contador.put(municipio, contador.getOrDefault(municipio, 0) + 1);
        }

        // Agora transformar em List<Map<String, Double>>
        List<Map<String, Double>> listaResultado = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : contador.entrySet()) {
            Map<String, Double> item = new HashMap<>();
            item.put(entry.getKey(), entry.getValue().doubleValue());
            listaResultado.add(item);
        }

        return listaResultado;
    }

}
