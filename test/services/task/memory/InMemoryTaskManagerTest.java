package services.task.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import services.history.memory.InMemoryHistoryManager;
import services.task.TaskManagerTest;

@DisplayName("Проверяем работу InMemoryTaskManagerTest")
public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}