package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class HistorialCuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCuestionario;

    private Long idCuestionario;

    private String nombreUsuario;

    private Long puntaje;

    private Integer preguntasCorrectas;

    private Integer preguntasErradas;

    @ManyToOne
    private Usuario jugador;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNombreCuestionario() {return nombreCuestionario;}
    public void setNombreCuestionario(String nombre) {this.nombreCuestionario = nombre;}

    public Long getIdCuestionario() {return idCuestionario;}
    public void setIdCuestionario(Long idCuestionario) {this.idCuestionario = idCuestionario;}

    public String getNombreUsuario() {return nombreUsuario;}
    public void setNombreUsuario(String nombre) {this.nombreUsuario = nombre;}

    public Long getPuntaje() {return puntaje;}
    public void setPuntaje(Long puntaje) {this.puntaje = puntaje;}

    public Integer getPreguntasCorrectas() {return preguntasCorrectas;}
    public void setPreguntasCorrectas(Integer preguntasCorrectas) {this.preguntasCorrectas = preguntasCorrectas;}

    public Integer getPreguntasErradas() {return preguntasErradas;}
    public void setPreguntasErradas(Integer preguntasErradas) {this.preguntasErradas = preguntasErradas;}

    public Usuario getJugador() {return jugador;}
    public void setJugador(Usuario jugador) {this.jugador = jugador;}
}
