package managers.utilityClasses;

import managers.fileBackedTasksManager.FileBackedTasksManager;
import managers.inMemoryManagers.InMemoryHistoryManager;
import managers.inMemoryManagers.InMemoryTasksManager;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;

import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedManager(Path filePath) {
        return new FileBackedTasksManager(filePath);
    }

}
