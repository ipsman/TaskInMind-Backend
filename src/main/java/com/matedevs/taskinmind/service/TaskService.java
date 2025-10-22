package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Task;
import com.matedevs.taskinmind.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
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
