package com.tallerwebi.dominio.dto;

public class RankingCuestionarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private Long puntajeTotal;
    private Long preguntasCorrectas;
    private Long preguntasErradas;
    private Long numeroIntentos;

    public RankingCuestionarioDTO(Long idUsuario, String nombreUsuario, Long puntajeTotal,
                                  Long preguntasCorrectas, Long preguntasErradas, Long numeroIntentos) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.puntajeTotal = puntajeTotal;
        this.preguntasCorrectas = preguntasCorrectas;
        this.preguntasErradas = preguntasErradas;
        this.numeroIntentos = numeroIntentos;
    }

    public RankingCuestionarioDTO(Object[] data) {
        if (data != null && data.length >= 6) {
            this.idUsuario = data[0] != null ? ((Number) data[0]).longValue() : null;
            this.nombreUsuario = (String) data[1];
            this.puntajeTotal = data[2] != null ? ((Number) data[2]).longValue() : 0L;
            this.preguntasCorrectas = data[3] != null ? ((Number) data[3]).longValue() : 0L;
            this.preguntasErradas = data[4] != null ? ((Number) data[4]).longValue() : 0L;
            this.numeroIntentos = data[5] != null ? ((Number) data[5]).longValue() : 0L;
        }
    }

    public Long getIdUsuario() {return idUsuario;}
    public void setIdUsuario(Long idUsuario) {this.idUsuario = idUsuario;}

    public String getNombreUsuario() {return nombreUsuario;}
    public void setNombreUsuario(String nombreUsuario) {this.nombreUsuario = nombreUsuario;}

    public Long getPuntajeTotal() {return puntajeTotal;}
    public void setPuntajeTotal(Long puntajeTotal) {this.puntajeTotal = puntajeTotal;}

    public Long getPreguntasCorrectas() {return preguntasCorrectas;}
    public void setPreguntasCorrectas(Long preguntasCorrectas) {this.preguntasCorrectas = preguntasCorrectas;}

    public Long getPreguntasErradas() {return preguntasErradas;}
    public void setPreguntasErradas(Long preguntasErradas) {this.preguntasErradas = preguntasErradas;}

    public Long getNumeroIntentos() {return numeroIntentos;}
    public void setNumeroIntentos(Long numeroIntentos) {this.numeroIntentos = numeroIntentos;}

}
