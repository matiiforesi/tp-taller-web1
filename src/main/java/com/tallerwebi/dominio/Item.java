package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private TIPO_ITEMS tipoItem;

    private Long precio;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public TIPO_ITEMS getTipoItem() {return tipoItem;}
    public void setTipoItem(TIPO_ITEMS tipoItem) {this.tipoItem = tipoItem;}

    public Long getPrecio() {return precio;}
    public void setPrecio(Long precio) {this.precio = precio;}
}
