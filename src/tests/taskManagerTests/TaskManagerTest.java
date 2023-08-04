package tests.taskManagerTests;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    @Test
    protected void addNewTask() {
        Task task = new Task("Task", Status.NEW, "description");
        int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    protected void addNewEpic() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        final Task savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    protected void addNewSubtask() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);
        int subId = taskManager.addSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtaskById(subId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        final List<Integer> epicsSubs = epic.getSubtasksId();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, epicsSubs.size(), "Неверное число подзадач у эпика");
        assertEquals(2, epicsSubs.get(0), "Неверный идентификатор подзадачи эпика");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    protected void getTaskByIdTest() {
        Task task = new Task("Task", Status.NEW, "description");
        int taskId = taskManager.addTask(task);

        assertNull(taskManager.getTaskById(7));
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное число задач");
        assertEquals(task, taskManager.getTaskById(taskId), "Задачи не совпадают.");
    }

    @Test
    protected void getEpicByIdTest() {
        Epic epic = new Epic("Epic", "description");
        int taskId = taskManager.addEpic(epic);

        assertNull(taskManager.getEpicById(6));
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное число эпиков");
        assertEquals(epic, taskManager.getEpicById(taskId), "Эпики не совпадают.");
    }

    @Test
    protected void getSubtaskByIdTest() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);
        int subId = taskManager.addSubtask(subtask);

        assertNull(taskManager.getSubtaskById(8));
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное число подзадач");
        assertEquals(subtask, taskManager.getSubtaskById(subId), "Подзадачи не совпадают.");
    }

    @Test
    protected void updateTask() {
        Task task = new Task("Task", Status.NEW, "description");
        int taskId = taskManager.addTask(task);

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное число задач");
        assertEquals(task, taskManager.getTaskById(taskId), "Сохранена неверная задача");

        Task task2 = new Task(taskId, "Task", Status.IN_PROGRESS, "description");
        taskManager.updateTask(task2);

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное число задач");
        assertEquals(task2, taskManager.getTaskById(taskId), "Задача не обновилась");
    }

    @Test
    protected void updateEpic() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Неверное число эпиков");
        assertEquals(epic, taskManager.getEpicById(epicId), "Сохранен неверный эпик");

        Epic epic1 = new Epic(epicId, "Epic1", "description");
        taskManager.updateEpic(epic1);

        assertEquals(1, taskManager.getAllEpics().size(), "Неверное число эпиков");
        assertEquals(epic1, taskManager.getEpicById(epicId), "Эпик не обновился");
    }

    @Test
    protected void updateSubtask() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);
        int subId = taskManager.addSubtask(subtask);

        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное число подзадач");
        assertEquals(subtask, taskManager.getSubtaskById(subId), "Сохранена неверная подзадача");
        assertEquals(epic, taskManager.getEpicById(subtask.getEpicId()), "Неверный эпик у подзадачи");
        assertEquals(Status.NEW, epic.getStatus());

        Subtask subtask1 = new Subtask(subId, "Subtask", Status.IN_PROGRESS, "description", epicId);
        taskManager.updateSubtask(subtask1);

        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное число подзадач");
        assertEquals(subtask1, taskManager.getSubtaskById(subId), "Подзадача не обновилась");
        assertEquals(epicId, subtask.getEpicId(), "Неверный эпик у подзадачи");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus(), "Статус эпика не обновился");
    }

    @Test
    protected void removeTaskById() {
        Task task = new Task("Task", Status.NEW, "description");
        int taskId = taskManager.addTask(task);
        taskManager.removeTaskById(7);

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное число задач");
        assertEquals(task, taskManager.getTaskById(taskId), "Задачи не соответствуют");

        taskManager.removeTaskById(taskId);

        assertFalse(taskManager.getAllTasks().contains(task), "Задача не была удалена");
    }

    @Test
    protected void removeEpicById() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Неверное число эпиков");
        assertEquals(epic, taskManager.getEpicById(epicId), "Эпики не соответствуют");
        taskManager.removeEpicById(4);

        taskManager.removeEpicById(epicId);

        assertFalse(taskManager.getAllEpics().contains(epic), "Эпик не был удален");
    }

    @Test
    protected void removeSubtaskById() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        taskManager.removeSubtaskById(4);

        assertTrue(epic.getSubtasksId().contains(subtaskId), "Эпик не содержит подзадачу");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное число подзадач");
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId), "Подзадачи не соответствуют");

        taskManager.removeSubtaskById(subtaskId);

        assertFalse(epic.getSubtasksId().contains(subtaskId), "Подзадача не была удалена из эпика");
        assertFalse(taskManager.getAllSubtasks().contains(subtask), "Подзадача не был удален");
    }

    @Test
    protected void getAllTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty());
        Task task = new Task("Task", Status.NEW, "description");
        taskManager.addTask(task);
        Task task1 = new Task("Task1", Status.IN_PROGRESS, "description");
        taskManager.addTask(task1);

        assertEquals(List.of(task, task1), taskManager.getAllTasks(), "Списки задач не совпадают");
    }

    @Test
    protected void getAllEpics() {
        Epic epic = new Epic("Epic", "description");
        taskManager.addEpic(epic);
        Epic epic1 = new Epic("Epic1", "description");
        taskManager.addEpic(epic1);

        assertEquals(List.of(epic, epic1), taskManager.getAllEpics(), "Списки эпиков не совпадают");
    }

    @Test
    protected void getAllSubtasks() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);
        Subtask subtask1 = new Subtask("Subtask1", Status.IN_PROGRESS, "description", epicId);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);

        assertEquals(List.of(subtask, subtask1), taskManager.getAllSubtasks(), "Списки подзадач не совпадают");
    }

    @Test
    protected void clearTasks() {
        Task task = new Task("Task", Status.NEW, "description");
        taskManager.addTask(task);
        Task task1 = new Task("Task1", Status.IN_PROGRESS, "description");
        taskManager.addTask(task1);

        assertEquals(List.of(task, task1), taskManager.getAllTasks(), "Списки задач не совпадают");
        taskManager.clearTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пуст");
    }

    @Test
    protected void clearEpics() {
        Epic epic = new Epic("Epic", "description");
        taskManager.addEpic(epic);
        Epic epic1 = new Epic("Epic1", "description");
        taskManager.addEpic(epic1);

        assertEquals(List.of(epic, epic1), taskManager.getAllEpics(), "Списки эпиков не совпадают");
        taskManager.clearEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пуст");
    }

    @Test
    protected void clearSubtasks() {
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", Status.NEW, "description", epicId);
        Subtask subtask1 = new Subtask("Subtask1", Status.IN_PROGRESS, "description", epicId);

        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);
        assertEquals(List.of(subtask, subtask1), taskManager.getAllSubtasks(), "Списки подзадач не совпадают");
        taskManager.clearSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач не пуст");
    }

    @Test
    protected void checkTasksTime() {
        Task task = new Task("Task", Status.NEW, "description",
                LocalDateTime.of(2023, 5, 1, 0, 0), 20);
        int taskId = taskManager.addTask(task);
        Epic epic = new Epic("Epic", "description");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", Status.IN_PROGRESS, "description",
                LocalDateTime.of(2023, 1, 3, 11, 30), 120, epicId);
        Subtask subtask1 = new Subtask("Subtask1", Status.DONE, "description",
                LocalDateTime.of(2023, 2, 3, 11, 30), 120, epicId);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);
        Task task1 = new Task("Task1", Status.NEW, "description");
        taskManager.addTask(task1);

        assertEquals(task.getStartTime(), LocalDateTime.of(
                2023, 5, 1, 0, 0), "Неверное время начала задачи");
        assertEquals(task.getEndTime(), LocalDateTime.of(
                2023, 5, 1, 0, 0).plusMinutes(20), "Неверное время завершения задачи");
        assertEquals(epic.getStartTime(), subtask.getStartTime(), "Неверное время начала эпика");
        assertEquals(epic.getEndTime(), subtask1.getEndTime(), "Неверное время завершения эпика");
        assertEquals(epic.getDuration(), subtask.getDuration() + subtask1.getDuration(), "Неверная продолжительность эпика");
        assertEquals(List.of(subtask, subtask1, task, task1), taskManager.getPrioritizedTasks());

        taskManager.removeTaskById(taskId);

        assertEquals(List.of(subtask, subtask1, task1), taskManager.getPrioritizedTasks());
    }

    @Test
    protected void checkTimeConflicts() {
        Task task = new Task("Task", Status.NEW, "description",
                LocalDateTime.of(2023, 5, 1, 0, 0), 20);
        Task task1 = new Task("Task1", Status.DONE, "description",
                LocalDateTime.of(2023, 5, 1, 0, 10), 20);
        taskManager.addTask(task);
        taskManager.addTask(task1);

        assertEquals(1, taskManager.getAllTasks().size(), "Задачи конфликтуют по времени");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Задачи конфликтуют по времени");
        assertEquals(List.of(task), taskManager.getPrioritizedTasks(), "Сохранена неверная задача");
    }
}
