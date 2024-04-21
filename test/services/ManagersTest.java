package services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.history.HistoryManager;
import services.task.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверка утилитарного менеджера")
class ManagersTest {

    @Test
    @DisplayName("Проверяем что создается дефолтный HistoryManager")
    void shouldCreateHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не создался");
    }

    @Test
    @DisplayName("Проверяем что создается дефолтный TaskManager")
    void shouldCreateTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не создался");
    }
}