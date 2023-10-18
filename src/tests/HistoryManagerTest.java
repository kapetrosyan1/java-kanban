package tests;

import managers.inMemoryManagers.InMemoryHistoryManager;
import managers.interfaces.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    protected HistoryManager historyManager;

    @BeforeEach
    protected void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    protected void add() {
        Task task = new Task(1, "Task", Status.NEW, "description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

        Epic epic = new Epic(2, "Epic", Status.DONE, "description");
        historyManager.add(epic);
        assertEquals(2, historyManager.getHistory().size(), "История не полная.");
        assertEquals(List.of(task, epic), historyManager.getHistory(), "Неправильный порядок истории");

        historyManager.add(task);
        assertEquals(2, historyManager.getHistory().size(), "История не полная.");
        assertEquals(List.of(epic, task), historyManager.getHistory(), "Неправильный порядок истории");
    }

    @Test
    protected void remove() {
        Task task = new Task(1, "Task", Status.NEW, "description");
        historyManager.add(task);
        Epic epic = new Epic(2, "Epic", Status.DONE, "description");
        historyManager.add(epic);
        Subtask subtask = new Subtask(3, "Subtask", Status.DONE, "description", 2);
        historyManager.add(subtask);

        assertNotNull(historyManager.getHistory(), "История не null.");
        assertEquals(List.of(task, epic, subtask), historyManager.getHistory(), "Неправильная история");

        historyManager.remove(3);
        assertEquals(List.of(task, epic), historyManager.getHistory(), "Неправильная история");

        historyManager.add(subtask);
        historyManager.remove(2);
        assertEquals(List.of(task, subtask), historyManager.getHistory(), "Неправильная история");

        historyManager.add(epic);
        historyManager.remove(1);
        assertEquals(List.of(subtask, epic), historyManager.getHistory(), "Неправильная история");
    }

    @Test
    protected void getHistory() {
        Task task = new Task(1, "Task", Status.NEW, "description");
        historyManager.add(task);
        Epic epic = new Epic(2, "Epic", Status.DONE, "description");
        historyManager.add(epic);
        Subtask subtask = new Subtask(3, "Subtask", Status.DONE, "description", 2);
        historyManager.add(subtask);

        assertEquals(List.of(task, epic, subtask), historyManager.getHistory(), "Неправильная история");

        historyManager.add(task);
        assertEquals(List.of(epic, subtask, task), historyManager.getHistory(), "Неправильная история");

        historyManager.remove(2);
        assertEquals(List.of(subtask, task), historyManager.getHistory(), "Неправильная история");
    }
}