package managers.utilityClasses;

import managers.HttpTaskManager.HttpTaskManager;
import managers.fileBackedTasksManager.FileBackedTasksManager;
import managers.inMemoryManagers.InMemoryHistoryManager;
import managers.inMemoryManagers.InMemoryTasksManager;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;

import java.net.URL;
import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(String KVServerURL) {
        return new HttpTaskManager(KVServerURL);
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTasksManager();
    }

    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManager(Path filePath) {
        return new FileBackedTasksManager(filePath);
    }
}