package com.tallerwebi.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class HistorialDeCuestionarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Long puntaje;

    private Integer preguntasCorrectas;

    private Integer preguntasErradas;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public Long getPuntaje() {return puntaje;}
    public void setPuntaje(Long puntaje) {this.puntaje = puntaje;}
    public Integer getPreguntasCorrectas() {return preguntasCorrectas;}
    public void setPreguntasCorrectas(Integer preguntasCorrectas) {this.preguntasCorrectas = preguntasCorrectas;}
    public Integer getPreguntasErradas() {return preguntasErradas;}
    public void setPreguntasErradas(Integer preguntasErradas) {this.preguntasErradas = preguntasErradas;}
}
