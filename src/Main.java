import Server.HttpTaskServer;
import Server.KV.KVServer;
import managers.interfaces.TaskManager;
import managers.utilityClasses.Managers;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager manager = Managers.getDefault("http://localhost:8078/");
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();

        Task task1 = new Task("test task", Status.NEW, "test task 1");
        Task task2 = new Task("test task", Status.IN_PROGRESS, "test task 2",
                LocalDateTime.of(2023, 10, 16, 10, 10), 120);
        Epic epic = new Epic("test epic", "test epic with 2 subs");
        Subtask subtask1 = new Subtask("test Subtask", Status.NEW, "test subtask 1", 1);
        Subtask subtask2 = new Subtask("test Subtask", Status.DONE, "test subtask 2",
                LocalDateTime.of(2023, 10, 18, 14, 40), 60, 1);
        System.out.println(HttpTaskServer.GSON.toJson(subtask1));
        System.out.println(HttpTaskServer.GSON.toJson(subtask2));

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addTask(task1);
        manager.addTask(task2);

        System.out.println(epic.getSubtasksId());
        manager.removeSubtaskById(2);
        System.out.println(epic.getSubtasksId());
        manager.removeTaskById(4);

    }
}
