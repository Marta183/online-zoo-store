package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.ProductCategory;
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

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository categoryRep) {
        this.categoryRep = categoryRep;
    }

    @Override
    public ProductCategory findById(Long id) {
        return categoryRep.findById(id).orElse(null);
    }

    @Override
    public List<ProductCategory> findAll() {
        return categoryRep.findAll(Sort.by("name"));
    }

    @Override
    public List<ProductCategory> findAllByNameStartingWith(String nameStartingWith) {
        return categoryRep.findAllByNameStartingWith(nameStartingWith);
    }

    @Override
    public List<ProductCategory> findAllByParentId(String idParentCategory) throws NumberFormatException {
        return categoryRep.findAllByParent_Id(Long.parseLong(idParentCategory));
    }

    @Override
    @Transactional
    public ProductCategory create(ProductCategory category) {
        return categoryRep.save(category);
    }

    @Override
    @Transactional
    public ProductCategory update(Long id, ProductCategory updatedCategory) {
        updatedCategory.setId(id);
        return categoryRep.save(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        categoryRep.deleteById(id);
    }
}
