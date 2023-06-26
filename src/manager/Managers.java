package manager;

import java.io.File;
import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedManager(Path filePath) {
        return new FileBackedTasksManager(filePath);
    }

}
