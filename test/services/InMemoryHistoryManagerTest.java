package services;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверяем работу InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager;
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }
    @Test
    @DisplayName("Проверяем сохранение неизменяемой истории")
    void shouldSaveImmutableHistory() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        epic.setId(1);
        historyManager.add(epic);
        epic.setTitle("Epic update title");
        epic.setDescription("Epic update description");
        epic.setStatus(Status.NEW);
        epic.getSubtaskIds().add(1);
        historyManager.add(epic);

        List<Task> history = historyManager.getHistory();
        assertNotEquals(history.get(0).getTitle(), history.get(1).getTitle(), "Title изменился везде");
        assertNotEquals(history.get(0).getDescription(), history.get(1).getDescription(), "Description изменился везде");
        assertNotEquals(history.get(0).getStatus(), history.get(1).getStatus(), "Status изменился везде");
        assertNotEquals(
                ((Epic) history.get(0)).getSubtaskIds().toString(),
                ((Epic) history.get(1)).getSubtaskIds().toString(),
                "Связь к подзадачам изменилась везде"
        );

        Subtask subtaskNew = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        subtaskNew.setId(2);
        subtaskNew.setEpicId(1);
        historyManager.add(subtaskNew);
        subtaskNew.setTitle("In progress Subtask title");
        subtaskNew.setDescription("In progress Subtask description");
        subtaskNew.setStatus(Status.IN_PROGRESS);
        subtaskNew.setEpicId(3);
        historyManager.add(subtaskNew);

        assertNotEquals(history.get(2).getTitle(), history.get(3).getTitle(), "Title изменился везде");
        assertNotEquals(history.get(2).getDescription(), history.get(3).getDescription(), "Description изменился везде");
        assertNotEquals(history.get(2).getStatus(), history.get(3).getStatus(), "Status изменился везде");
        assertNotEquals(
                ((Subtask) history.get(2)).getEpicId(),
                ((Subtask) history.get(3)).getEpicId(),
                "Связь к эпику изменилась везде"
        );

        Task task = new Task("Title", "Description", Status.NEW);
        task.setId(4);
        historyManager.add(task);
        task.setTitle("Title update title");
        task.setDescription("Title update description");
        task.setStatus(Status.DONE);
        historyManager.add(task);
        assertNotEquals(history.get(4).getTitle(), history.get(5).getTitle(), "Title изменился везде");
        assertNotEquals(history.get(4).getDescription(), history.get(5).getDescription(), "Description изменился везде");
        assertNotEquals(history.get(4).getStatus(), history.get(5).getStatus(), "Status изменился везде");
    }
}