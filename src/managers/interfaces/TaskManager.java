package managers.interfaces;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    Task getTaskById(int taskId);

    Epic getEpicById(int taskId);

    Subtask getSubtaskById(int taskId);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void removeTaskById(int taskId);

    void removeEpicById(int taskId);

    void removeSubtaskById(int taskId);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    List<Task> getPrioritizedTasks();
}
