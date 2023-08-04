package tests.taskManagerTests;

import managers.inMemoryManagers.InMemoryTasksManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTasksManager> {
    @BeforeEach
    protected void BeforeEach() {
        taskManager = new InMemoryTasksManager();
    }
}