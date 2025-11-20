package com.tallerwebi.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dificultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private static Integer multiplicadorDificultad;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public Integer getMultiplicadorDificultad() {return multiplicadorDificultad;}
    public void setMultiplicadorDificultad(Integer multiplicadorDificultad) {this.multiplicadorDificultad = multiplicadorDificultad;}
}
