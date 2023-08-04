import exceptions.ManagerSaveException;
import managers.fileBackedTasksManager.FileBackedTasksManager;
import managers.inMemoryManagers.InMemoryTasksManager;
import managers.interfaces.TaskManager;
import managers.utilityClasses.Managers;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getFileBackedManager(Path.of("resources/recovery.csv"));
        Task task = new Task("Task", Status.NEW, "description", LocalDateTime.of(
                2023, 1, 1, 11, 30), 180);
        taskManager.addTask(task);
        System.out.println(task);
    }
}
