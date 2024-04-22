package services.history.memory;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Проверяем работу InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }
    @Test
    @DisplayName("Проверяем, что история записывает состояние вызванных задач / подзадач / эпиков, то есть их копии данных")
    void shouldSaveCloneTaskIntoHistoryWhenAddTaskOrSubtaskOrEpic() {
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

    @Test
    @DisplayName("Проверяем, что история хранит последние 10 записей")
    void getHistory_returnListOt10Elements_add11Tasks() {
        Task task = new Task("Title", "Description", Status.NEW);
        for (int i = 0; i < 11; i++) {
            historyManager.add(task);
        }

        assertEquals(historyManager.getHistory().size(), 10, "Размер истории больше 10");
    }

    @Test
    @DisplayName("Проверяем, что задачи записывабтся в историю")
    void add_setTaskOrSubtaskOrEpic() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Subtask subtask = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        Task task = new Task("Title", "Description", Status.NEW);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        List<Task> tasks = historyManager.getHistory();

        assertEquals(tasks.size(), 3, "История не записывается");
        assertEquals(tasks.get(0), epic, "Отсутствует эпик в истории");
        assertEquals(tasks.get(1), task, "Отсутствует задача в истории");
        assertEquals(tasks.get(2), subtask, "Отсутствует подзадача в истории");
    }
}