package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Color;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ColorRepository;
import kms.onlinezoostore.services.ColorService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Color findById(Long id) {
        Color color = colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return color;
    }

    @Override
    public List<Color> findAll() {
        return colorRepository.findAll();
    }

    @Override
    @Transactional
    public Color create(Color color) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(colorRepository, "name", color.getName());
        // log
        Color savedColor = colorRepository.save(color);
        // log
        return savedColor;
    }

    @Override
    @Transactional
    public Color update(Long id, Color updatedColor) {
        // log
        Color existingColor = colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingColor.getName().equals(updatedColor.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(colorRepository, "name", updatedColor.getName());
        }
        // log
        updatedColor.setId(id);
        return colorRepository.save(updatedColor);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        colorRepository.deleteById(id);
    }
}
