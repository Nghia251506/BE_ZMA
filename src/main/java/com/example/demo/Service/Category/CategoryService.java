package com.example.demo.Service.Category;

import com.example.demo.Dto.Category.CategoryRequest;
import com.example.demo.Dto.Category.CategoryResponse;
import com.example.demo.Entity.Category;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        categoryMapper.updateEntity(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        // Kiểm tra xem có sản phẩm nào đang thuộc Category này không trước khi xóa
        categoryRepository.deleteById(id);
    }
}
