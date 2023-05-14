package com.jdsjara.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdsjara.dscatalog.dto.RoleDTO;
import com.jdsjara.dscatalog.dto.UserDTO;
import com.jdsjara.dscatalog.dto.UserInsertDTO;
import com.jdsjara.dscatalog.dto.UserUpdateDTO;
import com.jdsjara.dscatalog.entities.Role;
import com.jdsjara.dscatalog.entities.User;
import com.jdsjara.dscatalog.repositories.RoleRepository;
import com.jdsjara.dscatalog.repositories.UserRepository;
import com.jdsjara.dscatalog.services.exceptions.DatabaseException;
import com.jdsjara.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repository.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		/*
		Em versões mais recentes do Spring Boot, o nome da função mudou para: getReferenceById
		Esse comando não realiza consulta no BD, ele instancia um objeto com os dados que serão
		atualizados. A conexão com o BD será aberta apenas uma vez e com o método SAVE do repository
		*/
		try {
			User entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);	
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);	
		} 
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		} 
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de Integridade.");
		}
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {

		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		for (RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}
		
	}
	
}
