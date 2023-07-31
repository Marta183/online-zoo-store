package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Color;

import java.util.List;

public interface ColorService {

    Color findById(Long id);
    List<Color> findAll();

    Color create(Color color);
    Color update(Long id, Color color);
    void deleteById(Long id);
}
