package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Task;
import com.matedevs.taskinmind.model.User;
import com.matedevs.taskinmind.repository.TaskRepository;
import com.matedevs.taskinmind.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(Task task, String username) {
        Long userId = getUserIdByUsername(username);

        task.setUserId(userId);

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUserId(Long userId){
        return taskRepository.findTasksByUserId(userId);
    }

    public Long getUserIdByUsername(String username) {
        // Feltételezve, hogy a UserRepository-ben van egy findByUsername metódus
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user.getId();
    }

    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id);
    }

    public Task updateTask(Long id,Task taskDetails){
        Optional<Task> optionalTask = taskRepository.findById(id);
        if(optionalTask.isPresent()){
            Task task = optionalTask.get();
            task.setTitle(taskDetails.getTitle());
            task.setPriority(taskDetails.getPriority());
            task.setDescription(taskDetails.getDescription());
            task.setUserId(taskDetails.getUserId());
            task.setCompleted(taskDetails.isCompleted());
            task.setDueDate(taskDetails.getDueDate());

            return taskRepository.save(task);
        }
        else{
            return null;
        }
    }

    public void deleteTask(Long id){
        taskRepository.deleteById(id);
    }
}
