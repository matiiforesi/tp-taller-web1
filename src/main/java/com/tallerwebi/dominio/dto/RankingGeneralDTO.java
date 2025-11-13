package com.tallerwebi.dominio.dto;

public class RankingGeneralDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private Long puntajeTotal;

    public RankingGeneralDTO(Long idUsuario, String nombreUsuario, Long puntajeTotal) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.puntajeTotal = puntajeTotal;
    }

    public RankingGeneralDTO(Object[] data) {
        if (data != null && data.length >= 3) {
            this.idUsuario = data[0] != null ? ((Number) data[0]).longValue() : null;
            this.nombreUsuario = (String) data[1];
            this.puntajeTotal = data[2] != null ? ((Number) data[2]).longValue() : 0L;
        }
    }

    public Long getIdUsuario() {return idUsuario;}
    public void setIdUsuario(Long idUsuario) {this.idUsuario = idUsuario;}

    public String getNombreUsuario() {return nombreUsuario;}
    public void setNombreUsuario(String nombreUsuario) {this.nombreUsuario = nombreUsuario;}

    public Long getPuntajeTotal() {return puntajeTotal;}
    public void setPuntajeTotal(Long puntajeTotal) {this.puntajeTotal = puntajeTotal;}
}
