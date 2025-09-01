package com.javaboy.situs.dto;

import com.javaboy.situs.model.Situs;
import java.math.BigDecimal;

public class SitusFormDto {
    private Integer situsId;
    private String namaSitus;

    private String tanggalDitemukan;

    private String kondisi;
    private String deskripsi;
    private String fotoUrl;
    private BigDecimal lat;
    private BigDecimal lon;
    private Integer provinsiId;
    private Integer kabupatenId;
    private Integer kecamatanId;
    private String mapsUrl;

    public static SitusFormDto fromEntity(Situs s) {
        SitusFormDto d = new SitusFormDto();
        d.situsId = s.getSitusId();
        d.namaSitus = s.getNamaSitus();
        d.tanggalDitemukan = normalizeToIsoDate(s.getTahunDitemukan()); // normalisasi
        d.kondisi = s.getKondisi();
        d.deskripsi = s.getDeskripsi();
        d.fotoUrl = s.getFotoUrl();
        d.lat = s.getLat();
        d.lon = s.getLon();
        d.mapsUrl = s.getMapsUrl();
        d.provinsiId = s.getProvinsi() != null ? s.getProvinsi().getProvinsiId() : null;
        d.kabupatenId = s.getKabupaten() != null ? s.getKabupaten().getKabupatenId() : null;
        d.kecamatanId = s.getKecamatan() != null ? s.getKecamatan().getKecamatanId() : null;
        return d;
    }

    private static String normalizeToIsoDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String t = raw.trim();
        if (t.matches("^\\d{4}$")) return t + "-01-01";                 // hanya tahun
        if (t.matches("^\\d{4}-\\d{2}-\\d{2}$")) return t;              // sudah ISO
        if (t.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {                  // dd/MM/yyyy
            String[] p = t.split("/");
            String dd = p[0].length()==1 ? "0"+p[0] : p[0];
            String mm = p[1].length()==1 ? "0"+p[1] : p[1];
            return p[2] + "-" + mm + "-" + dd;
        }
        return t;
    }

    // getters & setters
    public Integer getSitusId() { return situsId; }
    public void setSitusId(Integer situsId) { this.situsId = situsId; }
    public String getNamaSitus() { return namaSitus; }
    public void setNamaSitus(String namaSitus) { this.namaSitus = namaSitus; }
    public String getTanggalDitemukan() { return tanggalDitemukan; }
    public void setTanggalDitemukan(String tanggalDitemukan) { this.tanggalDitemukan = tanggalDitemukan; }
    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public BigDecimal getLat() { return lat; }
    public void setLat(BigDecimal lat) { this.lat = lat; }
    public BigDecimal getLon() { return lon; }
    public void setLon(BigDecimal lon) { this.lon = lon; }
    public Integer getProvinsiId() { return provinsiId; }
    public void setProvinsiId(Integer provinsiId) { this.provinsiId = provinsiId; }
    public Integer getKabupatenId() { return kabupatenId; }
    public void setKabupatenId(Integer kabupatenId) { this.kabupatenId = kabupatenId; }
    public Integer getKecamatanId() { return kecamatanId; }
    public void setKecamatanId(Integer kecamatanId) { this.kecamatanId = kecamatanId; }
    public String getMapsUrl() { return mapsUrl; }
    public void setMapsUrl(String mapsUrl) { this.mapsUrl = mapsUrl; }
}
