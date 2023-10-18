import managers.inMemoryManagers.InMemoryTasksManager;
import managers.interfaces.TaskManager;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;

import java.time.LocalDateTime;

public class MainInMemory {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTasksManager();
        Epic epic = new Epic("test epic", "test epic with 2 subs");
        manager.addEpic(epic);
        System.out.println(epic.toString());
        Subtask subtask = new Subtask("test Subtask", Status.NEW, "test subtask 1",
                LocalDateTime.of(2023, 10, 18, 10, 20), 120, epic.getId());
        manager.addSubtask(subtask);
        System.out.println(epic.getStartTime());
        System.out.println(manager.getEpicSubtasks(1));

    }
}
