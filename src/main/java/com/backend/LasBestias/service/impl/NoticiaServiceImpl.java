package com.backend.LasBestias.service.impl;


// import com.backend.LasBestias.exception.custom.BadRequestException;
// import com.backend.LasBestias.exception.error.Error;
import com.backend.LasBestias.model.Noticia;
import com.backend.LasBestias.repository.NoticiaRepository;
import com.backend.LasBestias.service.ImageService;
import com.backend.LasBestias.service.NoticiaService;
import com.backend.LasBestias.service.dto.request.NoticiaDTOin;
import com.backend.LasBestias.service.dto.response.NoticiaDTO;
import com.backend.LasBestias.service.mapper.NoticiaMapper;
import com.backend.LasBestias.model.ImageType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NoticiaServiceImpl implements NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final ImageService imageService;

    public NoticiaServiceImpl(NoticiaRepository noticiaRepository, ImageService imageService) {
        this.noticiaRepository = noticiaRepository;
        this.imageService = imageService;
    }

    @Override
    public NoticiaDTO create(NoticiaDTOin dto) {
        Noticia noticia = NoticiaMapper.MAPPER.toEntity(dto);
        noticia.setFechaPublicacion(LocalDateTime.now());

        // Primero guardamos la noticia (sin imagen todavía)
        noticia = noticiaRepository.save(noticia);

        // Subimos la imagen y la asignamos
        if (dto.getImage() != null) {
            Long imageId = imageService.uploadImage(dto.getImage(), ImageType.NOTICIA, noticia.getId());
            noticia.setImageId(imageId);
            noticia = noticiaRepository.save(noticia);  // Volvemos a guardar la noticia con la imagen
        }

        return NoticiaMapper.MAPPER.toDto(noticia);
    }


    @Override
    public NoticiaDTO getById(Long id) {
        Noticia noticia = getNoticia(id);
        NoticiaDTO dto = NoticiaMapper.MAPPER.toDto(noticia);

        // Agregar URL de la imagen desde S3
        String url = imageService.getS3url(noticia.getId(), ImageType.NOTICIA);
        dto.setImagenUrl(url);

        return dto;
    }

    @Override
    public Page<NoticiaDTO> getAll(Pageable pageable) {
        Page<Noticia> page = noticiaRepository.findAll(pageable);
        return page.map(noticia -> {
            NoticiaDTO dto = NoticiaMapper.MAPPER.toDto(noticia);
            String url = imageService.getS3url(noticia.getId(), ImageType.NOTICIA);
            dto.setImagenUrl(url);
            return dto;
        });
    }


    @Override
    public NoticiaDTO update(Long id, NoticiaDTOin dto) {
        Noticia noticia = getNoticia(id);
        Noticia noticiaUpdated = NoticiaMapper.MAPPER.toEntity(dto);
        NoticiaMapper.MAPPER.update(noticia, noticiaUpdated);

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            // 🧹 Eliminar imagen anterior
            imageService.deleteImage(noticia.getId(), ImageType.NOTICIA);

            // 📤 Subir nueva imagen
            Long imageId = imageService.uploadImage(dto.getImage(), ImageType.NOTICIA, noticia.getId());
            noticia.setImageId(imageId);
        }

        noticia = noticiaRepository.save(noticia);
        return NoticiaMapper.MAPPER.toDto(noticia);
    }



    @Override
    @Transactional
    public void delete(Long id) {
        Noticia noticia = getNoticia(id);
        noticiaRepository.delete(noticia);
    }

    private Noticia getNoticia(Long id) {
        Optional<Noticia> noticiaOptional = noticiaRepository.findById(id);
       // if (noticiaOptional.isEmpty()) {
       //     throw new BadRequestException(Error.NOTICIA_NOT_FOUND);
       // }
        return noticiaOptional.get();
    }
}

