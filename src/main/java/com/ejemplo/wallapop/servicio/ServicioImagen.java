package com.ejemplo.wallapop.servicio;

import com.ejemplo.wallapop.modelo.Imagen;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ServicioImagen {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public Imagen guardarImagen(MultipartFile archivo) throws IOException {
        String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path rutaCompleta = Paths.get(uploadDir, nombreArchivo);

        BufferedImage imagenOriginal = ImageIO.read(archivo.getInputStream());
        BufferedImage imagenRedimensionada = redimensionarImagen(imagenOriginal, 1000);

        File outputfile = rutaCompleta.toFile();
        ImageIO.write(imagenRedimensionada, obtenerExtension(nombreArchivo), outputfile);

        Imagen imagen = new Imagen();
        imagen.setRuta(nombreArchivo);
        return imagen;
    }

    public void eliminarImagen(String nombreArchivo) throws IOException {
        Path rutaCompleta = Paths.get(uploadDir, nombreArchivo);
        Files.deleteIfExists(rutaCompleta);
    }

    private BufferedImage redimensionarImagen(BufferedImage imagenOriginal, int anchoObjetivo) {
        int anchoOriginal = imagenOriginal.getWidth();
        int altoOriginal = imagenOriginal.getHeight();
        int altoObjetivo = (int) ((double) altoOriginal / anchoOriginal * anchoObjetivo);

        BufferedImage imagenRedimensionada = new BufferedImage(anchoObjetivo, altoObjetivo, imagenOriginal.getType());
        Graphics2D g = imagenRedimensionada.createGraphics();
        g.drawImage(imagenOriginal, 0, 0, anchoObjetivo, altoObjetivo, null);
        g.dispose();

        return imagenRedimensionada;
    }

    private String obtenerExtension(String nombreArchivo) {
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
    }
}