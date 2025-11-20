package com.tallerwebi.dominio;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);

    void guardar(Usuario usuario);

    Usuario buscar(String email);

    void modificar(Usuario usuario);

    Usuario buscarPorId(Long idUsuario);

    Integer contarUsuarios();

    void actualizarMonedas(Long idUsuario, Long nuevasMonedas);
}
