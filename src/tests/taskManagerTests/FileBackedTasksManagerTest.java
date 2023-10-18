package tests.taskManagerTests;

import managers.fileBackedTasksManager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    protected void beforeEach() {
        taskManager = new FileBackedTasksManager(Path.of("resources/testFiles/testRecoveryFile"));
    }

    @Test
    protected void loadFromFile() {
        Task task = new Task("Task", Status.NEW, "description");
        int taskId = taskManager.addTask(task);

        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", Status.NEW, "description", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        taskManager = FileBackedTasksManager.loadFromFile(new File("resources/testFiles/testRecoveryFile"));

        assertEquals(task.toString(), taskManager.getTaskById(taskId).toString(), "Задачи отличаются");
        assertEquals(epic.toString(), taskManager.getEpicById(epicId).toString(), "Эпики отличаются");
        assertEquals(subtask.toString(), taskManager.getSubtaskById(subtaskId).toString(), "Подзадачи отличаются");
        assertEquals(task.toString(), taskManager.historyManager.getHistory().get(0).toString(), "Ошибка истории");
        assertEquals(epic.toString(), taskManager.historyManager.getHistory().get(1).toString(), "Ошибка истории");
        assertEquals(subtask.toString(), taskManager.historyManager.getHistory().get(2).toString(), "Ошибка истории");
        assertEquals(List.of(subtaskId), epic.getSubtasksId(), "Неверный список подзадач у эпика");
    }

    @Test
    protected void loadWithEmptyTaskList() {
        Task task = new Task("Task", Status.NEW, "description");
        int taskId = taskManager.addTask(task);
        taskManager.removeTaskById(taskId);

        taskManager = FileBackedTasksManager.loadFromFile(new File("resources/testFiles/testRecoveryFile"));

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(taskManager.historyManager.getHistory().isEmpty());
    }

    @Test
    protected void loadWithEmptyHistoryList() {
        Task task = new Task("Task", Status.NEW, "description");
        Epic epic = new Epic("Epic", "description");

        int taskId = taskManager.addTask(task);
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);

        taskManager.addSubtask(subtask);

        taskManager = FileBackedTasksManager.loadFromFile(new File(
                "resources/testFiles/testRecoveryFile"));

        assertTrue(taskManager.historyManager.getHistory().isEmpty(), "История не пуста");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное число задач");
        assertEquals(task.toString(), taskManager.getTaskById(taskId).toString(), "Задачи не совпадают");
        assertEquals(1, taskManager.getAllEpics().size(), "Эпики не совпадают");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадачи не совпадают");
    }

    @Test
    protected void loadEpicWithNoSubtasks() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        taskManager = FileBackedTasksManager.loadFromFile(new File("resources/testFiles/testRecoveryFile"));

        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(epic.toString(), taskManager.getEpicById(epicId).toString());
        assertTrue(epic.getSubtasksId().isEmpty());
    }

    @Test
    protected void loadWithTimedTasks() {
        Task task = new Task("Task", Status.NEW, "description", LocalDateTime.of(
                2023, 1, 3, 11, 30), 180);
        Epic epic = new Epic("Epic", "description");

        int taskId = taskManager.addTask(task);

        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", LocalDateTime.of(
                2023, 1, 1, 11, 30), 180, epicId);

        int subtaskId = taskManager.addSubtask(subtask);

        taskManager = FileBackedTasksManager.loadFromFile(new File("resources/testFiles/testRecoveryFile"));

        assertEquals(List.of(task), taskManager.getAllTasks(), "Ошибка загрузки задач");
        assertEquals(List.of(epic), taskManager.getAllEpics(), "Ошибка загрузки эпиков");
        assertEquals(List.of(subtask), taskManager.getAllSubtasks(), "Ошибка загрузки подзадач");
        assertEquals(List.of(subtask, task), taskManager.getPrioritizedTasks(), "Ошибка приоритета задач");
        assertEquals(task.getStartTime(), taskManager.getTaskById(taskId).getStartTime(), "Ошибка загрузки задач");
        assertEquals(task.getDuration(), taskManager.getTaskById(taskId).getDuration(), "Ошибка загрузки задач");
        assertEquals(subtask.getStartTime(), taskManager.getEpicById(epicId).getStartTime(), "Ошибка загрузки подзадач");
        assertEquals(subtask.getStartTime(), taskManager.getSubtaskById(subtaskId).getStartTime(), "Ошибка загрузки подзадач");

    }
}