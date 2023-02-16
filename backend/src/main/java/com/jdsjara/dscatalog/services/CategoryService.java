package com.jdsjara.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdsjara.dscatalog.dto.CategoryDTO;
import com.jdsjara.dscatalog.entities.Category;
import com.jdsjara.dscatalog.repositories.CategoryRepository;
import com.jdsjara.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		
		return list
				.stream()
				.map(x -> new CategoryDTO(x))
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		// Em versões mais recentes do Spring Boot, o nome da função mudou para: getReferenceById
		// Esse comando não realiza consulta no BD, ele instancia um objeto com os dados que serão
		// atualizados. A conexão com o BD será aberta apenas uma vez e com o método SAVE do repository
		try {
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);	
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		}
	}
	
}
