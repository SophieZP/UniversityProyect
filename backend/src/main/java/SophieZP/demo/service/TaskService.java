package SophieZP.demo.service;

import SophieZP.demo.entity.Subject;
import SophieZP.demo.entity.Task;
import SophieZP.demo.repository.SubjectRepository;
import SophieZP.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // Create a Task inside a especific Subject
    public Task createTask(Long subjectId, Task task) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject != null) {
            task.setSubject(subject);
            return taskRepository.save(task);
        }
        return null;
    }

    // Get all the task of the user for the list or callendar
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findBySubject_UserIdOrderByDueDateAsc(userId);
    }

    // Mark task as complete
    public Task toggleTaskCompletion(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setIsCompleted(!task.getIsCompleted());
            return taskRepository.save(task);
        }
        return null;
    }

    // En TaskService.java
    public List<Task> getTasksBySubjectId(Long subjectId) {
        return taskRepository.findBySubjectId(subjectId);
    }

    // Delete a task
    public boolean deleteTask(Long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }
}