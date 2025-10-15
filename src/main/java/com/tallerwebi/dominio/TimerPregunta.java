package com.tallerwebi.dominio;

public class TimerPregunta {

    private Long inicio;
    private Integer duracionSegundos;

    public TimerPregunta(Integer duracionSegundos){
        this.duracionSegundos = duracionSegundos;
        this.inicio = System.currentTimeMillis();
    }

    public Boolean tiempoAgotado(){
        Long ahora= System.currentTimeMillis();
        Long transcurrido= (ahora - this.inicio)/1000;
        return transcurrido>this.duracionSegundos;
    }
    public Long segundosRestantes(){
        Long ahora= System.currentTimeMillis();
        Long transcurrido= (ahora - this.inicio)/1000;
        return Math.max(0, this.duracionSegundos-transcurrido );
    }
    public void reiniciar(){
        this.inicio = System.currentTimeMillis();
    }
}
