package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Color;
import kms.onlinezoostore.repositories.ColorRepository;
import kms.onlinezoostore.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    @Autowired
    public ColorServiceImpl(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Override
    public Color findById(Long id) {
        return colorRepository.findById(id).orElse(null);
    }

    @Override
    public List<Color> findAll() {
        return colorRepository.findAll();
    }

    @Override
    @Transactional
    public Color create(Color color) {
        return colorRepository.save(color);
    }

    @Override
    @Transactional
    public Color update(Long id, Color updatedColor) {
        updatedColor.setId(id);
        return colorRepository.save(updatedColor);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        colorRepository.deleteById(id);
    }
}
