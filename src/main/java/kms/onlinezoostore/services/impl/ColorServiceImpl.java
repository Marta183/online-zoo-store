package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.dto.mappers.ColorMapper;
import kms.onlinezoostore.entities.Color;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ColorRepository;
import kms.onlinezoostore.services.ColorService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Color";

    @Autowired
    public ColorServiceImpl(ColorRepository colorRepository, UniqueFieldService uniqueFieldService) {
        this.colorRepository = colorRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public ColorDto findById(Long id) {
        ColorDto colorDto = colorRepository.findById(id)
                .map(ColorMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return colorDto;
    }

    @Override
    public List<ColorDto> findAll() {
        return colorRepository.findAll()
                .stream().map(ColorMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ColorDto create(ColorDto colorDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(colorRepository, "name", colorDto.getName());
        // log
        Color color = ColorMapper.INSTANCE.mapToEntity(colorDto);
        // log
        Color savedColor = colorRepository.save(color);
        // log
        ColorDto savedColorDto = ColorMapper.INSTANCE.mapToDto(savedColor);
        // log
        return savedColorDto;
    }

    @Override
    @Transactional
    public void update(Long id, ColorDto updatedColorDto) {
        // log
        Color existingColor = colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingColor.getName().equals(updatedColorDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(colorRepository, "name", updatedColorDto.getName());
        }
        Color updatedColor = ColorMapper.INSTANCE.mapToEntity(updatedColorDto);
        // log
        updatedColor.setId(id);
        colorRepository.save(updatedColor);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Color color = colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        colorRepository.deleteById(id);
    }
}
