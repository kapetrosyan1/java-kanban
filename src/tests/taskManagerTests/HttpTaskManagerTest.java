package tests.taskManagerTests;

import Server.HttpTaskServer;
import Server.KV.KVServer;
import managers.HttpTaskManager.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;
    private HttpTaskServer httpServer;
    private Task task1;
    private Epic epic;
    private Subtask subtask1;

    @BeforeEach
    void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = new HttpTaskManager("http://localhost:8078/", false);

        httpServer = new HttpTaskServer(taskManager);
        httpServer.start();

        task1 = new Task("test task", Status.NEW, "test task 1");
        epic = new Epic("test epic", "test epic with 2 subs");
        subtask1 = new Subtask("test Subtask", Status.NEW, "test subtask 1", epic.getId());
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        httpServer.stop();
    }

    @Test
    public void testLoadFromHttpServer() {
        taskManager.addTask(task1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);

        assertEquals(1, taskManager.getAllTasks().size(), "Неправильное количество задач");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество подзадач");

        HttpTaskManager taskManager1 = new HttpTaskManager("http://localhost:8078/", false);

        assertEquals(0, taskManager1.getAllTasks().size(), "Список задач должен быть пуст");
        assertEquals(0, taskManager1.getAllEpics().size(), "Список эпиков должен быть пуст");
        assertEquals(0, taskManager1.getAllSubtasks().size(), "Список подзадач должен быть пуст");

        taskManager1.load();

        assertEquals(1, taskManager1.getAllTasks().size(), "Неправильное количество задач");
        assertEquals(new Task(1, "test task", Status.NEW, "test task 1"),
                taskManager.getTaskById(1), "Задачи не совпадают");
        assertEquals(1, taskManager1.getAllEpics().size(), "Неверное количество эпиков");
        assertEquals(1, taskManager1.getAllSubtasks().size(), "Неверное количество подзадач");
        assertEquals(new Subtask(3, "test Subtask", Status.NEW, "test subtask 1", 2),
                taskManager1.getSubtaskById(3), "Подзадачи не совпадают");
    }
}
