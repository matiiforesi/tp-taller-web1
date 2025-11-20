package com.tallerwebi.dominio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RespuestaCategorias {

    @JsonProperty("trivia_categories")
    private List<TriviaCategory> triviaCategories;

    public List<TriviaCategory> getTriviaCategories() {
        return triviaCategories;
    }

    public void setTriviaCategories(List<TriviaCategory> triviaCategories) {
        this.triviaCategories = triviaCategories;
    }
}

