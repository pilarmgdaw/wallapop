package com.ejemplo.wallapop.servicio;

import com.ejemplo.wallapop.modelo.Anuncio;
import com.ejemplo.wallapop.modelo.Usuario;
import com.ejemplo.wallapop.repositorio.RepositorioAnuncio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServicioAnuncio {

    private static final Logger logger = LoggerFactory.getLogger(ServicioAnuncio.class);

    @Autowired
    private RepositorioAnuncio repositorioAnuncio;

    public Page<Anuncio> obtenerTodosLosAnuncios(Pageable pageable) {
        return repositorioAnuncio.findAllByOrderByFechaCreacionDesc(pageable);
    }

    public Page<Anuncio> obtenerAnunciosDeUsuario(Usuario usuario, Pageable pageable) {
        return repositorioAnuncio.findByUsuarioOrderByFechaCreacionDesc(usuario, pageable);
    }

    public Anuncio guardarAnuncio(Anuncio anuncio) {
        return repositorioAnuncio.save(anuncio);
    }

    public Anuncio obtenerAnuncioPorId(Long id) {
        return repositorioAnuncio.findById(id)
                .orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));
    }

    public void eliminarAnuncio(Anuncio anuncio) {
        repositorioAnuncio.delete(anuncio);
    }
}