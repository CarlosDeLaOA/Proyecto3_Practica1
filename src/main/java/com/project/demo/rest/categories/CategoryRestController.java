package com.project.demo.rest.categories;

import com.project.demo.logic.entity.categories.Categories;
import com.project.demo.logic.entity.categories.CategoriesRepository;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    @Autowired
    private CategoriesRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1));
        Page<Categories> categoriesPage = categoryRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Categories retrieved successfully",
                categoriesPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> addCategory(@RequestBody Categories category,
                                         HttpServletRequest request) {
        if (category == null) {
            return new GlobalResponseHandler().handleResponse(
                    "Error creating Category",
                    HttpStatus.BAD_REQUEST,
                    request
            );
        }

        Categories savedCategory = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse(
                "Category created successfully",
                savedCategory,
                HttpStatus.CREATED,
                request
        );
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
                                            @RequestBody Categories category,
                                            HttpServletRequest request) {
        Optional<Categories> foundCategory = categoryRepository.findById(categoryId);

        if (foundCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Categories toUpdate = foundCategory.get();
        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());

        Categories saved = categoryRepository.save(toUpdate);

        return new GlobalResponseHandler().handleResponse(
                "Category updated successfully",
                saved,
                HttpStatus.OK,
                request
        );
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId,
                                            HttpServletRequest request) {
        Optional<Categories> foundCategory = categoryRepository.findById(categoryId);

        if (foundCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        categoryRepository.deleteById(categoryId);

        return new GlobalResponseHandler().handleResponse(
                "Category deleted successfully",
                foundCategory.get(),
                HttpStatus.OK,
                request
        );
    }
}
