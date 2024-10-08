package com.java.taskmanager.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.taskmanager.dtos.TaskDTO;
import com.java.taskmanager.entities.Category;
import com.java.taskmanager.entities.Priority;
import com.java.taskmanager.entities.Task;
import com.java.taskmanager.repositories.CategoryRepository;
import com.java.taskmanager.repositories.PriorityRepository;
import com.java.taskmanager.repositories.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired 
	private PriorityRepository priorityRepository;

	public TaskDTO update(Task task) {
		Task entity = taskRepository.save(task);
		return new TaskDTO(entity);
	}
	
	public TaskDTO insert(TaskDTO dto) {
        
        Task task = new Task();
        
        task.setTitle(dto.getTitle());
        task.setCompleted(dto.isCompleted());
        
        if (dto.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(dto.getDate(), formatter);
            task.setDate(date); 
        }
       
        Optional<Category> categoryOpt = categoryRepository.findById(dto.getCategoryId());
        if (categoryOpt.isPresent()) {
            task.setCategory(categoryOpt.get());
        } else {
            throw new RuntimeException("Category not found");
        }
        
        Optional<Priority> priorityOpt = priorityRepository.findById(dto.getPriorityId());
        if(priorityOpt.isPresent()) {
        	task.setPriority(priorityOpt.get());
        } else {
            throw new RuntimeException("Category not found");
        }

        task = taskRepository.save(task);
        
        return new TaskDTO(task);
    }



	@Transactional(readOnly = true)
	public List<TaskDTO> findAll() {
		List<Task> tasks = taskRepository.findAll(Sort.by(Sort.Order.asc("priority"))); // Ordenar por prioridade
		  return tasks.stream().map(TaskDTO::new).collect(Collectors.toList());
		
	}

	@Transactional(readOnly = true)
	public Optional<Task> findById(Long id) {
		return Optional.ofNullable(taskRepository.findById(id).get());

	}

	@Transactional
	public void deleteById(Long id) {
		taskRepository.deleteById(id);
	}

}
