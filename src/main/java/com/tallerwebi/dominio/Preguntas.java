package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class Preguntas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String enunciado;

    private String categoria;

    @ManyToOne
    @JoinColumn(name = "dificultad_id", nullable = false)
    private Dificultad dificultad;

    @Column(name = "respuesta_correcta")
    private String respuestaCorrecta;

    @Column(name = "respuesta_incorrecta_1")
    private String respuestaIncorrecta1;

    @Column(name = "respuesta_incorrecta_2")
    private String respuestaIncorrecta2;

    @Column(name = "respuesta_incorrecta_3")
    private String respuestaIncorrecta3;

    @ManyToOne
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    public Cuestionario getCuestionario() {return cuestionario;}
    public void setCuestionario(Cuestionario cuestionario) {this.cuestionario = cuestionario;}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getEnunciado() {return enunciado;}
    public void setEnunciado(String enunciado) {this.enunciado = enunciado;}

    public String getCategoria() {return categoria;}
    public void setCategoria(String categoria) {this.categoria = categoria;}

    public Dificultad getDificultad() {return dificultad;}
    public void setDificultad(Dificultad dificultad) {this.dificultad = dificultad;}

    public String getRespuestaCorrecta() {return respuestaCorrecta;}
    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getRespuestaIncorrecta1() {return respuestaIncorrecta1;}
    public void setRespuestaIncorrecta1(String respuestaIncorrecta1) {
        this.respuestaIncorrecta1 = respuestaIncorrecta1;
    }

    public String getRespuestaIncorrecta2() {return respuestaIncorrecta2;}
    public void setRespuestaIncorrecta2(String respuestaIncorrecta2) {
        this.respuestaIncorrecta2 = respuestaIncorrecta2;
    }

    public String getRespuestaIncorrecta3() {return respuestaIncorrecta3;}
    public void setRespuestaIncorrecta3(String respuestaIncorrecta3) {
        this.respuestaIncorrecta3 = respuestaIncorrecta3;
    }
}
