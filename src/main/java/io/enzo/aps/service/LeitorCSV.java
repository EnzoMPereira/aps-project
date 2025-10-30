package io.enzo.aps.service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import io.enzo.aps.model.RegistroDesmatamento;

public class LeitorCSV {

    public static List<RegistroDesmatamento> lerCSV(String caminho) {
        List<RegistroDesmatamento> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha = br.readLine(); // pular cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                String estado = dados[6];
                String municipio = dados[7];
                double area = Double.parseDouble(dados[2]);
                String bioma = dados[8];
                String data = dados[4];

                lista.add(new RegistroDesmatamento(estado, municipio, area, bioma, data));
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler CSV: " + e.getMessage());
        }

        return lista;
    }
}
