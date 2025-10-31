package io.enzo.aps.service;

import io.enzo.aps.model.RegistroDesmatamento;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DesmatamentoService {

    public Map<Integer, List<RegistroDesmatamento>> agruparPorMes(List<RegistroDesmatamento> registros) {
        return registros.stream()
                .filter(r -> r.getData() != null && r.getData().contains("-"))
                .collect(Collectors.groupingBy(r -> Integer.parseInt(r.getData().split("-")[1])));
    }

    public Map<Integer, Double> somarAreaPorMes(List<RegistroDesmatamento> registros, String ano) {
        return registros.stream()
                .filter(r -> r.getData() != null && r.getData().startsWith(ano))
                .collect(Collectors.groupingBy(
                        r -> Integer.parseInt(r.getData().split("-")[1]),
                        Collectors.summingDouble(RegistroDesmatamento::getAreaKm2)
                ));
    }

    public Map<String, Double> somarAreaPorMunicipio(List<RegistroDesmatamento> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(
                        RegistroDesmatamento::getMunicipio,
                        Collectors.summingDouble(RegistroDesmatamento::getAreaKm2)
                ));
    }
}
