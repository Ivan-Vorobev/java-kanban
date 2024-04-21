package services;

import services.history.HistoryManager;
import services.history.memory.InMemoryHistoryManager;
import services.task.TaskManager;
import services.task.memory.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
