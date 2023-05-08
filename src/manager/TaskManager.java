package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void updateTask(Task task, Task oldTask);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask, Epic epic);

    void updateSubtask(Subtask subtask, Subtask oldSubtask);

    void updateEpic(Epic epic);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getAllEpicsSubtasks(Epic epic);

    Task getTaskById(int taskId);

    Epic getEpicById(int taskId);

    Subtask getSubtaskById(int taskId);

    void removeTaskById(int taskId);

    void removeEpicById(int taskId);

    void removeSubtaskById(int taskId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();



}
