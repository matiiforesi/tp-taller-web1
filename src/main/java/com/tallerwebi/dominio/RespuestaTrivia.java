package com.tallerwebi.dominio;

import java.util.List;

public class RespuestaTrivia {
    private int response_code;
    private List<PreguntaTrivia> results;

    public int getResponse_code() { return response_code; }
    public void setResponse_code(int response_code) { this.response_code = response_code; }
    public List<PreguntaTrivia> getResults() { return results; }
    public void setResults(List<PreguntaTrivia> results) { this.results = results; }
}
