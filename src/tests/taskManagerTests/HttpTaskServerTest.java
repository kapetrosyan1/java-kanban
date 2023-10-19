package tests.taskManagerTests;

import Server.HttpTaskServer;
import Server.KV.KVServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.HttpTaskManager.HttpTaskManager;
import managers.utilityClasses.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;
    private HttpTaskServer httpServer;
    private static HttpClient client;
    private static Gson gson;
    private Task task1;
    private Task task2;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeAll
    static void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = new HttpTaskManager("http://localhost:8078/", false);

        httpServer = new HttpTaskServer(taskManager);
        httpServer.start();

        task1 = new Task("test task", Status.NEW, "test task 1");
        task2 = new Task("test task", Status.IN_PROGRESS, "test task 2",
                LocalDateTime.of(2023, 10, 16, 10, 10), 120);
        epic = new Epic("test epic", "test epic with 2 subs");
        subtask1 = new Subtask("test Subtask", Status.NEW, "test subtask 1", 1);
        subtask2 = new Subtask("test Subtask", Status.DONE, "test subtask 2",
                LocalDateTime.of(2023, 10, 18, 14, 40), 60, 1);
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        httpServer.stop();
    }

    @Test
    void addOrUpdateTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        String jsonTask = gson.toJson(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksList = List.of(new Task(1, "test task", Status.NEW, "test task 1"));
        assertEquals(tasksList, taskManager.getAllTasks(), "Списки не совпадают");
        assertEquals(1, taskManager.getAllTasks().size(), "Количество задач неверное");
        assertEquals(201, response.statusCode(), "неверный код ответа");

        String jsonTask1 = gson.toJson(new Task(1, "test task", Status.DONE, "test task 1"));
        HttpRequest newRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        HttpResponse<String> response1 = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        List<Task> updatedList = List.of(new Task(1, "test task", Status.DONE, "test task 1"));

        assertEquals(201, response1.statusCode(), "неверный код ответа");
        assertEquals(updatedList, taskManager.getAllTasks(), "Списки должны быть одинаковыми");
    }

    @Test
    void addOrUpdateEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        String jsonEpic = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksList = List.of(new Epic(1, "test epic", "test epic with 2 subs"));

        assertEquals(tasksList, taskManager.getAllEpics(), "Списки не совпадают");
        assertEquals(201, response.statusCode(), "неверный код ответа");
        assertEquals("Эпик успешно добавлен", response.body());

        String jsonEpic1 = gson.toJson(new Epic(1, "test epic", "test epic with still 0 subs"));
        HttpRequest newRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();

        HttpResponse<String> response1 = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        List<Epic> updatedList = List.of(new Epic(1, "test epic", "test epic with still 0 subs"));

        assertEquals(updatedList, taskManager.getAllEpics(), "Списки должны быть одинаковыми");
        assertEquals("Эпик успешно обновлен", response1.body());
        assertEquals(201, response1.statusCode(), "неверный код ответа");
    }

    @Test
    void addOrUpdateSubtask() throws IOException, InterruptedException {
        taskManager.addEpic(epic);

        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        String jsonSubtask = gson.toJson(subtask1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksList = List.of(new Subtask(2, "test Subtask", Status.NEW, "test subtask 1", 1));

        assertEquals(tasksList, taskManager.getAllSubtasks(), "Списки не совпадают");
        assertEquals(201, response.statusCode(), "неверный код ответа");
        assertEquals("Подзадача успешно добавлена", response.body());

        String jsonSubtask1 = gson.toJson(new Subtask(2, "test Subtask", Status.DONE, "test subtask 1", 1));
        HttpRequest newRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask1))
                .build();

        HttpResponse<String> response1 = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        List<Subtask> updatedList = List.of(new Subtask(2, "test Subtask", Status.DONE, "test subtask 1", 1));

        assertEquals(updatedList, taskManager.getAllSubtasks(), "Списки должны быть одинаковыми");
        assertEquals("Подзадача успешно обновлена", response1.body());
        assertEquals(201, response1.statusCode(), "неверный код ответа");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask2);
        taskManager.addTask(task1);

        URI uri = URI.create("http://localhost:8080/tasks/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> prioritizedTaskList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Task> sampleList = List.of(new Task(1, "test task", Status.IN_PROGRESS, "test task 2",
                        LocalDateTime.of(2023, 10, 16, 10, 10), 120),
                new Task(3, "test Subtask", Status.DONE, "test subtask 2",
                        LocalDateTime.of(2023, 10, 18, 14, 40), 60),
                new Task(4, "test task", Status.NEW, "test task 1"));

        assertEquals(sampleList, prioritizedTaskList, "Списки не одинаковые");
        assertEquals(200, response.statusCode(), "Неверный код ответа");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        taskManager.addTask(task1);

        URI uri = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        List<Task> sampleList = List.of(new Task(1, "test task", Status.NEW, "test task 1"));

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(sampleList, tasksList, "Списки не одинаковые");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        taskManager.addEpic(epic);

        URI uri = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksList = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());

        List<Epic> sampleList = List.of(new Epic(1, "test epic", "test epic with 2 subs"));

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(sampleList, tasksList, "Списки не одинаковые");
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);

        URI uri = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksList = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());

        List<Subtask> sampleList = List.of(new Subtask(2, "test Subtask", Status.NEW, "test subtask 1", 1));

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(sampleList, tasksList, "Списки не одинаковые");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        taskManager.addTask(task1);

        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);

        Task sampleTask = new Task(1, "test task", Status.NEW, "test task 1");

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(sampleTask, task, "Задачи не одинаковые");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);

        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic1 = gson.fromJson(response.body(), Epic.class);

        Epic sampleEpic = new Epic(1, "test epic", "test epic with 2 subs");

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(sampleEpic, epic1, "Эпики не одинаковые");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);

        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=2");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        Subtask sampleSubtask = new Subtask(2, "test Subtask", Status.NEW, "test subtask 1", 1);

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(sampleSubtask, subtask, "Подзадачи не одинаковые");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);

        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        URI uri = URI.create("http://localhost:8080/tasks/history");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> historyList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(3, historyList.get(0).getId(), "Элементы не совпадают");
        assertEquals(1, historyList.get(1).getId(), "Элементы не совпадают");
        assertEquals(2, historyList.get(2).getId(), "Элементы не совпадают");
        assertEquals(200, response.statusCode(), "Неверный код ответа");
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(List.of(new Task(1, "test task", Status.NEW, "test task 1"),
                        new Task(2, "test task", Status.IN_PROGRESS, "test task 2",
                                LocalDateTime.of(2023, 10, 16, 10, 10), 120)),
                taskManager.getAllTasks(), "Задача не была добавлена");

        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals("Задача с id 1 успешно удалена", response.body(), "Неверный ответ сервера");
        List<Task> sampleTask = List.of(new Task(2, "test task", Status.IN_PROGRESS, "test task 2",
                LocalDateTime.of(2023, 10, 16, 10, 10), 120));
        assertEquals(sampleTask, taskManager.getAllTasks(), "Списки не совпадают");
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addEpic(new Epic("delete epic", "delete epic"));

        assertEquals(2, taskManager.getAllEpics().size(), "Неверное число эпиков");

        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> sampleList = List.of(new Epic(2, "delete epic", "delete epic"));
        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals("Эпик с id 1 успешно удален", response.body(), "Неверный ответ сервера");
        assertEquals(sampleList, taskManager.getAllEpics(), "Списки не совпадают");
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> sampleList = List.of(new Subtask(2, "test Subtask", Status.NEW, "test subtask 1", 1));
        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals("Подзадача с id 3 успешно удалена", response.body(), "Неверный ответ сервера");
        assertEquals(sampleList, taskManager.getAllSubtasks(), "Списки не совпадают");
    }

    @Test
    void clearAllTasks() throws IOException, InterruptedException {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        URI uri = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals("Задачи успешно удалены", response.body(), "Неверный ответ сервера");
        assertEquals(0, taskManager.getAllTasks().size(), "Список не пуст");
    }

    @Test
    void clearAllEpics() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addEpic(new Epic("delete Epic", "delete epic descr"));

        URI uri = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals("Эпики успешно удалены", response.body(), "Неверный ответ сервера");
        assertEquals(0, taskManager.getAllEpics().size(), "Список не пуст");
    }

    @Test
    void clearAllSubtasks() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        URI uri = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals("Подзадачи успешно удалены", response.body(), "Неверный ответ сервера");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список не пуст");
    }

    @Test
    void getsubtaskEpiclist() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> respList = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        List<Subtask> sampleList = List.of(taskManager.getSubtaskById(2), taskManager.getSubtaskById(3));

        assertEquals(200, response.statusCode(), "Неверный код ответа");
        assertEquals(2, taskManager.getEpicSubtasks(1).size(), "Список неправильный");
        assertEquals(sampleList, respList, "Списки должны быть одинаковыми");
    }
}
