
package org.example.bookmarket.category.service;

import com.bukgeokbukgeok.category.dto.CategoryResponse;
import com.bukgeokbukgeok.category.entity.Category;
import com.bukgeokbukgeok.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
    }
}
