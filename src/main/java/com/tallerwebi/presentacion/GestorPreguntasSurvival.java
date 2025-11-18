package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Preguntas;
import com.tallerwebi.dominio.ServicioSurvival;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestor que maneja las preguntas del modo Survival sin usar índices.
 * Evita duplicados y gestiona la obtención de nuevas preguntas cuando es necesario.
 */
public class GestorPreguntasSurvival implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int CANTIDAD_PREGUNTAS_POR_LOTE = 5;

    private Preguntas preguntaActual;
    private Queue<Preguntas> colaPreguntas;
    private Set<String> preguntasRespondidas; // Usa el enunciado como identificador único
    private String dificultadActual;

    public GestorPreguntasSurvival() {
        this.colaPreguntas = new LinkedList<>();
        this.preguntasRespondidas = new HashSet<>();
    }

    /**
     * Inicializa el gestor con las primeras preguntas
     */
    public void inicializar(List<Preguntas> preguntasIniciales, String dificultad) {
        this.dificultadActual = dificultad;
        this.colaPreguntas.clear();
        this.preguntasRespondidas.clear();
        
        if (preguntasIniciales != null && !preguntasIniciales.isEmpty()) {
            this.colaPreguntas.addAll(preguntasIniciales);
        }
    }

    /**
     * Obtiene la siguiente pregunta disponible, evitando duplicados
     * @param servicioSurvival El servicio para obtener más preguntas si es necesario
     * @return La siguiente pregunta o null si no hay más disponibles
     */
    public Preguntas obtenerSiguientePregunta(ServicioSurvival servicioSurvival) {
        // Si no hay pregunta actual o ya fue respondida, obtener la siguiente
        if (preguntaActual == null || estaRespondida(preguntaActual)) {
            preguntaActual = obtenerPreguntaNoRespondida();
            
            // Si no hay pregunta y podemos obtener más, intentar cargar
            if (preguntaActual == null && puedeObtenerMasPreguntas()) {
                if (cargarNuevasPreguntas(servicioSurvival)) {
                    preguntaActual = obtenerPreguntaNoRespondida();
                }
            }
        }
        return preguntaActual;
    }

    /**
     * Obtiene la pregunta actual sin avanzar
     * @param servicioSurvival El servicio para obtener más preguntas si es necesario
     */
    public Preguntas getPreguntaActual(ServicioSurvival servicioSurvival) {
        if (preguntaActual == null) {
            preguntaActual = obtenerPreguntaNoRespondida();
            
            // Si no hay pregunta y podemos obtener más, intentar cargar
            if (preguntaActual == null && puedeObtenerMasPreguntas() && servicioSurvival != null) {
                if (cargarNuevasPreguntas(servicioSurvival)) {
                    preguntaActual = obtenerPreguntaNoRespondida();
                }
            }
        }
        return preguntaActual;
    }

    /**
     * Marca la pregunta actual como respondida y avanza a la siguiente
     */
    public void marcarPreguntaComoRespondida() {
        if (preguntaActual != null) {
            preguntasRespondidas.add(obtenerIdentificadorPregunta(preguntaActual));
            preguntaActual = null; // Limpiar para obtener la siguiente
        }
    }

    /**
     * Verifica si una pregunta ya fue respondida
     */
    public boolean estaRespondida(Preguntas pregunta) {
        if (pregunta == null) {
            return false;
        }
        return preguntasRespondidas.contains(obtenerIdentificadorPregunta(pregunta));
    }

    /**
     * Verifica si hay más preguntas disponibles
     * @param servicioSurvival El servicio para verificar si se pueden obtener más
     */
    public boolean tieneMasPreguntas(ServicioSurvival servicioSurvival) {
        return !colaPreguntas.isEmpty() || (puedeObtenerMasPreguntas() && servicioSurvival != null);
    }

    /**
     * Obtiene una pregunta que no haya sido respondida
     */
    private Preguntas obtenerPreguntaNoRespondida() {
        // Primero, intentar obtener de la cola actual
        while (!colaPreguntas.isEmpty()) {
            Preguntas pregunta = colaPreguntas.poll();
            if (!estaRespondida(pregunta)) {
                return pregunta;
            }
        }

        return null; // No hay más preguntas en la cola
    }

    /**
     * Carga nuevas preguntas del servicio, filtrando las ya respondidas
     * Intenta múltiples veces si todas las preguntas obtenidas ya fueron respondidas
     * @param servicioSurvival El servicio para obtener preguntas
     * @return true si se cargaron preguntas, false si no hay más disponibles
     */
    public boolean cargarNuevasPreguntas(ServicioSurvival servicioSurvival) {
        if (servicioSurvival == null || dificultadActual == null) {
            return false;
        }

        int maxIntentos = 3; // Intentar hasta 3 veces para obtener preguntas no respondidas
        int intentos = 0;

        while (intentos < maxIntentos) {
            List<Preguntas> nuevasPreguntas = servicioSurvival.obtenerPreguntasSurvival(
                    dificultadActual, CANTIDAD_PREGUNTAS_POR_LOTE);

            if (nuevasPreguntas == null || nuevasPreguntas.isEmpty()) {
                return false; // No se pudieron obtener preguntas del servicio
            }

            // Filtrar preguntas ya respondidas
            List<Preguntas> preguntasFiltradas = nuevasPreguntas.stream()
                    .filter(p -> !estaRespondida(p))
                    .collect(Collectors.toList());

            if (!preguntasFiltradas.isEmpty()) {
                // Se encontraron preguntas no respondidas, agregarlas a la cola
                colaPreguntas.addAll(preguntasFiltradas);
                return true;
            }

            // Todas las preguntas obtenidas ya fueron respondidas, intentar de nuevo
            intentos++;
        }

        // Después de varios intentos, si aún no hay preguntas nuevas, 
        // agregar las que se obtuvieron (aunque ya fueron respondidas)
        // Esto evita que el juego termine prematuramente
        return false;
    }

    /**
     * Verifica si es posible obtener más preguntas
     */
    public boolean puedeObtenerMasPreguntas() {
        return dificultadActual != null;
    }

    /**
     * Actualiza la dificultad
     * Nota: Las nuevas preguntas se cargarán automáticamente cuando se necesiten
     */
    public void actualizarDificultad(String nuevaDificultad) {
        if (!nuevaDificultad.equals(dificultadActual)) {
            dificultadActual = nuevaDificultad;
        }
    }

    /**
     * Obtiene un identificador único para la pregunta (usa el enunciado)
     */
    private String obtenerIdentificadorPregunta(Preguntas pregunta) {
        if (pregunta == null) {
            return null;
        }
        // Usar el enunciado como identificador único
        // Si tiene ID, usarlo también para mayor precisión
        String identificador = pregunta.getEnunciado();
        if (pregunta.getId() != null) {
            identificador = pregunta.getId() + ":" + identificador;
        }
        return identificador;
    }

    /**
     * Obtiene la dificultad actual
     */
    public String getDificultadActual() {
        return dificultadActual;
    }

    /**
     * Obtiene la cantidad de preguntas respondidas
     */
    public int getCantidadPreguntasRespondidas() {
        return preguntasRespondidas.size();
    }

    /**
     * Verifica si la pregunta actual existe
     */
    public boolean tienePreguntaActual() {
        return preguntaActual != null;
    }

    /**
     * Limpia todas las preguntas del gestor pero mantiene la estructura
     * Útil para reiniciar el juego sin perder la referencia del gestor
     */
    public void limpiarPreguntas() {
        preguntaActual = null;
        colaPreguntas.clear();
        // NO limpiar preguntasRespondidas para evitar duplicados entre sesiones
        // preguntasRespondidas.clear();
        // NO limpiar dificultadActual para mantener el estado
        // dificultadActual = null;
    }

    /**
     * Limpia completamente el estado del gestor
     */
    public void limpiar() {
        preguntaActual = null;
        colaPreguntas.clear();
        preguntasRespondidas.clear();
        dificultadActual = null;
    }
}

