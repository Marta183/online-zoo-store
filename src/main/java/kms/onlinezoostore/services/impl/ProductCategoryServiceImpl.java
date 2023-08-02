package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductCategoryRepository;
import kms.onlinezoostore.services.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public ProductCategory findById(Long id) {
        ProductCategory category = categoryRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return category;
    }

    @Override
    public List<ProductCategory> findAll() {
        return categoryRep.findAll(Sort.by("name"));
    }

    @Override
    public List<ProductCategory> findAllByNameLike(String nameLike) {
        return categoryRep.findAllByNameLikeIgnoreCase(nameLike);
    }

    @Override
    public List<ProductCategory> findAllByParentId(String idParentCategory) throws NumberFormatException {
        return categoryRep.findAllByParent_Id(Long.parseLong(idParentCategory));
    }

    @Override
    @Transactional
    public ProductCategory create(ProductCategory category) {
        // log
        checkUniqueNameWithinParentCategory(category);
        // log
        ProductCategory savedCategory = categoryRep.save(category);
        // log
        return savedCategory;
    }

    @Override
    @Transactional
    public ProductCategory update(Long id, ProductCategory updatedCategory) {
        // log
        ProductCategory existingCategory = categoryRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            checkUniqueNameWithinParentCategory(updatedCategory);
        }
        updatedCategory.setId(id);
        return categoryRep.save(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        categoryRep.deleteById(id);
    }

    private void checkUniqueNameWithinParentCategory(ProductCategory category) {
        String name = category.getName();
        String parentName = null;
        Long parentId = null;
        if (category.getParent() != null) {
            ProductCategory parentCategory = categoryRep.findById(category.getParent().getId()).orElse(null);
            parentName = (parentCategory == null) ? null : parentCategory.getName();
            parentId = (parentCategory == null) ? null : parentCategory.getId();
        }

        if (categoryRep.countAllByParent_IdAndNameIgnoreCase(parentId, name) != 0) {
            String message = String.format("Name \'%s\' is already exist in the group \'%s\'", name, parentName);
            throw new EntityDuplicateException(ENTITY_CLASS_NAME, "name", message);
        }
    }
}
