package com.ejemplo.wallapop.controlador;

import com.ejemplo.wallapop.modelo.Anuncio;
import com.ejemplo.wallapop.modelo.Imagen;
import com.ejemplo.wallapop.modelo.Usuario;
import com.ejemplo.wallapop.servicio.ServicioAnuncio;
import com.ejemplo.wallapop.servicio.ServicioImagen;
import com.ejemplo.wallapop.servicio.ServicioUsuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/anuncios")
public class ControladorAnuncio {

    @Autowired
    private ServicioAnuncio servicioAnuncio;

    @Autowired
    private ServicioUsuario servicioUsuario;

    @Autowired
    private ServicioImagen servicioImagen;

    @GetMapping
    public String listarAnuncios(@RequestParam(defaultValue = "0") int pagina, Model model) {
        Page<Anuncio> anuncios = servicioAnuncio.obtenerTodosLosAnuncios(PageRequest.of(pagina, 10));
        model.addAttribute("anuncios", anuncios);
        return "lista-anuncios";//comentario
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoAnuncio(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "formulario-anuncio";
    }

    @PostMapping("/nuevo")
    public String crearAnuncio(@Valid @ModelAttribute Anuncio anuncio, BindingResult result,
                               @RequestParam("imagenes") List<MultipartFile> imagenes,
                               Authentication authentication) throws IOException {
        if (result.hasErrors()) {
            return "formulario-anuncio";
        }

        UserDetails usuario = servicioUsuario.loadUserByUsername(authentication.getName());
        anuncio.setUsuario((Usuario) usuario);

        for (MultipartFile imagen : imagenes) {
            if (!imagen.isEmpty()) {
                Imagen nuevaImagen = servicioImagen.guardarImagen(imagen);
                anuncio.addImagen(nuevaImagen);
            }
        }

        servicioAnuncio.guardarAnuncio(anuncio);
        return "redirect:/anuncios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarAnuncio(@PathVariable Long id, Model model, Authentication authentication) {
        Anuncio anuncio = servicioAnuncio.obtenerAnuncioPorId(id);
        if (!anuncio.getUsuario().getEmail().equals(authentication.getName())) {
            return "redirect:/anuncios";
        }
        model.addAttribute("anuncio", anuncio);
        return "formulario-anuncio";
    }

    @PostMapping("/editar/{id}")
    public String editarAnuncio(@PathVariable Long id, @Valid @ModelAttribute Anuncio anuncio,
                                BindingResult result, @RequestParam("imagenes") List<MultipartFile> imagenes,
                                Authentication authentication) throws IOException {
        if (result.hasErrors()) {
            return "formulario-anuncio";
        }

        Anuncio anuncioExistente = servicioAnuncio.obtenerAnuncioPorId(id);
        if (!anuncioExistente.getUsuario().getEmail().equals(authentication.getName())) {
            return "redirect:/anuncios";
        }

        anuncioExistente.setTitulo(anuncio.getTitulo());
        anuncioExistente.setPrecio(anuncio.getPrecio());
        anuncioExistente.setDescripcion(anuncio.getDescripcion());
    try {
        for (MultipartFile imagen : imagenes) {
            if (!imagen.isEmpty()) {
                Imagen nuevaImagen = servicioImagen.guardarImagen(imagen);
                anuncioExistente.addImagen(nuevaImagen);
            }
        }
    }catch (IOException e) {
        // Manejo de errores al guardar imágenes
        result.reject("imagenes", "Error al procesar las imágenes.");
        return "formulario-anuncio";
    }
        servicioAnuncio.guardarAnuncio(anuncioExistente);
        return "redirect:/anuncios";
    }

    @PostMapping("/borrar/{id}")
    public String borrarAnuncio(@PathVariable Long id, Authentication authentication) throws IOException {
        Anuncio anuncio = servicioAnuncio.obtenerAnuncioPorId(id);
        if (!anuncio.getUsuario().getEmail().equals(authentication.getName())) {
            return "redirect:/anuncios";
        }

        for (Imagen imagen : anuncio.getImagenes()) {
            servicioImagen.eliminarImagen(imagen.getRuta());
        }

        servicioAnuncio.eliminarAnuncio(anuncio);
        return "redirect:/anuncios";
    }

    @GetMapping("/ver/{id}")
    public String verAnuncio(@PathVariable Long id, Model model) {
        Anuncio anuncio = servicioAnuncio.obtenerAnuncioPorId(id);
        model.addAttribute("anuncio", anuncio);
        return "ver-anuncio";
    }

    @GetMapping("/mis-anuncios")
    public String listarMisAnuncios(@RequestParam(defaultValue = "0") int pagina, Model model, Authentication authentication) {
        UserDetails usuario = servicioUsuario.loadUserByUsername(authentication.getName());
        Page<Anuncio> anuncios = servicioAnuncio.obtenerAnunciosDeUsuario((Usuario) usuario, PageRequest.of(pagina, 10));
        model.addAttribute("anuncios", anuncios);
        return "mis-anuncios";
    }
}