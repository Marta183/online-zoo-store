package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.dto.mappers.ColorMapper;
import kms.onlinezoostore.entities.Color;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ColorRepository;
import kms.onlinezoostore.services.ColorService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "COLOR";

    @Autowired
    public ColorServiceImpl(ColorRepository colorRepository, UniqueFieldService uniqueFieldService) {
        this.colorRepository = colorRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public ColorDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        ColorDto colorDto = colorRepository.findById(id)
                .map(ColorMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return colorDto;
    }

    @Override
    public List<ColorDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return colorRepository.findAll()
                .stream().map(ColorMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ColorDto create(ColorDto colorDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, colorDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(colorRepository, "name", colorDto.getName());

        Color color = ColorMapper.INSTANCE.mapToEntity(colorDto);

        Color savedColor = colorRepository.save(color);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedColor.getId());

        return ColorMapper.INSTANCE.mapToDto(savedColor);
    }

    @Override
    @Transactional
    public void update(Long id, ColorDto updatedColorDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Color existingColor = colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingColor.getName().equals(updatedColorDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(colorRepository, "name", updatedColorDto.getName());
        }
        
        Color updatedColor = ColorMapper.INSTANCE.mapToEntity(updatedColorDto);
        updatedColor.setId(id);
        colorRepository.save(updatedColor);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        colorRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
