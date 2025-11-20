package com.tallerwebi.dominio;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TriviaCategory {

    private Integer id;

    private String name;

    @JsonProperty("id")
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    @JsonProperty("name")
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}

