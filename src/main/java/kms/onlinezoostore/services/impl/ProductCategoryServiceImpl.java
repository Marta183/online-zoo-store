package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.mappers.ProductCategoryMapper;
import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductCategoryRepository;
import kms.onlinezoostore.services.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRep;
    private static final String ENTITY_CLASS_NAME = "ProductCategory";

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository categoryRep) {
        this.categoryRep = categoryRep;
    }

    @Override
    public ProductCategoryDto findById(Long id) {
        ProductCategoryDto categoryDto = categoryRep.findById(id)
                .map(ProductCategoryMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return categoryDto;
    }

    @Override
    public List<ProductCategoryDto> findAll() {
        return categoryRep.findAll()
                .stream().map(ProductCategoryMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> findAllByNameLike(String nameLike) {
        return categoryRep.findAllByNameContainsIgnoreCase(nameLike)
                .stream().map(ProductCategoryMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> findAllByParentId(Long idParentCategory) {
        return categoryRep.findAllByParentId(idParentCategory)
                .stream().map(ProductCategoryMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductCategoryDto create(ProductCategoryDto categoryDto) {
        // log
        checkUniqueNameWithinParentCategory(categoryDto);
        // log
        ProductCategory productCategory = ProductCategoryMapper.INSTANCE.mapToEntity(categoryDto);
        // log
        ProductCategory savedCategory = categoryRep.save(productCategory);
        // log
        ProductCategoryDto savedCategoryDto = ProductCategoryMapper.INSTANCE.mapToDto(savedCategory);
        // log
        return savedCategoryDto;
    }

    @Override
    @Transactional
    public void update(Long id, ProductCategoryDto updatedCategoryDto) {
        // log
        ProductCategory existingCategory = categoryRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingCategory.getName().equals(updatedCategoryDto.getName())) {
            checkUniqueNameWithinParentCategory(updatedCategoryDto);
        }

        ProductCategory updatedCategory = ProductCategoryMapper.INSTANCE.mapToEntity(updatedCategoryDto);
        // log
        updatedCategory.setId(id);
        categoryRep.save(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ProductCategory category = categoryRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));// log
        categoryRep.deleteById(id);
    }

    private void checkUniqueNameWithinParentCategory(ProductCategoryDto categoryDto) {
        String name = categoryDto.getName();
        String parentName = null;
        Long parentId = null;
        if (categoryDto.getParent() != null) {
            ProductCategory parentCategory = categoryRep.findById(categoryDto.getParent().getId()).orElse(null);
            parentName = (parentCategory == null) ? null : parentCategory.getName();
            parentId = (parentCategory == null) ? null : parentCategory.getId();
        }

        if (categoryRep.countAllByParentIdAndNameIgnoreCase(parentId, name) != 0) {
            String message = String.format("Name \'%s\' is already exist in the group \'%s\'", name, parentName);
            throw new EntityDuplicateException(ENTITY_CLASS_NAME, "name", message);
        }
    }
}
