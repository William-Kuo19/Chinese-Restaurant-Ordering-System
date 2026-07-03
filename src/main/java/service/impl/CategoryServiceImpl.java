package service.impl;

import dao.CategoryDao;
import dao.impl.CategoryDaoImpl;
import model.Category;
import service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao = new CategoryDaoImpl();
    @Override
    public List<Category> getAllCategories() { return categoryDao.findAll(); }
}
