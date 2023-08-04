package tests;

import managers.inMemoryManagers.InMemoryTasksManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager;

    @BeforeEach
    protected void getManager() {
        manager = new InMemoryTasksManager();
    }

    @Test
    protected void shouldBeNewWithNoSubtasks() {
        Epic epic = new Epic("epic1", "description");
        manager.addEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    protected void shouldBeNewWithAllNewSubtasks() {
        Epic epic = new Epic("epic1", "description");
        Subtask subtask1 = new Subtask("Subtask1", Status.NEW, "description", 1);
        Subtask subtask2 = new Subtask("Subtask2", Status.NEW, "description", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    protected void shouldBeDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("epic1", "description");
        Subtask subtask1 = new Subtask("Subtask1", Status.DONE, "description", 1);
        Subtask subtask2 = new Subtask("Subtask2", Status.DONE, "description", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    protected void shouldBeInProgressWhenSubtasksNewAndDone() {
        Epic epic = new Epic("epic1", "description");
        Subtask subtask1 = new Subtask("Subtask1", Status.NEW, "description", 1);
        Subtask subtask2 = new Subtask("Subtask2", Status.DONE, "description", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    protected void shouldBeInProgressWhenSubtasksInProgress() {
        Epic epic = new Epic("epic1", "description");
        Subtask subtask1 = new Subtask("Subtask1", Status.IN_PROGRESS, "description", 1);
        Subtask subtask2 = new Subtask("Subtask2", Status.IN_PROGRESS, "description", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}