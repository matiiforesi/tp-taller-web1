package com.tallerwebi.dominio;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private String categoria;

    private Integer vidas;

    @ManyToOne
    @JoinColumn(name = "dificultad_id", nullable = false)
    private Dificultad dificultad;

    @OneToMany(mappedBy = "cuestionario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Preguntas> preguntas = new ArrayList<>();

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public String getCategoria() {return categoria;}
    public void setCategoria(String categoria) {this.categoria = categoria;}

    public Integer getVidas() {return vidas;}
    public void setVidas(Integer vidas) {this.vidas = vidas;}

    public Dificultad getDificultad() {return dificultad;}
    public void setDificultad(Dificultad dificultad) {this.dificultad = dificultad;}

    public List<Preguntas> getPreguntas() {return preguntas;}
    public void setPreguntas(List<Preguntas> preguntas) {
        this.preguntas = preguntas;
    }
}
