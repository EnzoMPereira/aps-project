package io.enzo.aps.model;

public class RegistroDesmatamento {
    private String estado;
    private String municipio;
    private double areaKm2;
    private String bioma;
    private String data;

    public RegistroDesmatamento(String estado, String municipio, double areaKm2, String bioma, String data) {
        this.estado = estado;
        this.municipio = municipio;
        this.areaKm2 = areaKm2;
        this.bioma = bioma;
        this.data = data;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public double getAreaKm2() {
        return areaKm2;
    }

    public void setAreaKm2(double areaKm2) {
        this.areaKm2 = areaKm2;
    }

    public String getBioma() {
        return bioma;
    }

    public void setBioma(String bioma) {
        this.bioma = bioma;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
