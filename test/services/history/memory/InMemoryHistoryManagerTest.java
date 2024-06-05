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

@DisplayName("Проверяем работу InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }
    @Test
    @DisplayName("Проверяем, что история записывает последние состояние вызванных задач / подзадач / эпиков")
    void shouldSaveCloneTaskIntoHistoryWhenAddTaskOrSubtaskOrEpic() {
        Epic epic1 = new Epic("Epic title", "Epic description", Status.DONE);
        epic1.setId(1);
        historyManager.add(epic1);
        Epic epic2 = new Epic("Epic update title", "Epic update description", Status.DONE);
        epic2.setId(1);
        epic2.setStatus(Status.NEW);
        epic2.getSubtaskIds().add(1);
        historyManager.add(epic2);

        List<Task> history = historyManager.getHistory();
        assertEquals(history.size(), 1, "Количество историй не равно ожидаемым");
        assertEquals(history.get(0).getTitle(), epic2.getTitle(), "Title НЕ изменился везде");
        assertEquals(history.get(0).getDescription(), epic2.getDescription(), "Description НЕ изменился везде");
        assertEquals(history.get(0).getStatus(), epic2.getStatus(), "Status НЕ изменился везде");
        assertEquals(
                ((Epic) history.get(0)).getSubtaskIds().toString(),
                epic2.getSubtaskIds().toString(),
                "Связь к подзадачам НЕ изменилась"
        );

        Subtask subtaskNew1= new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        subtaskNew1.setId(2);
        subtaskNew1.setEpicId(1);
        historyManager.add(subtaskNew1);
        Subtask subtaskNew2= new Subtask("In progress Subtask title", "In progress Subtask description", Status.IN_PROGRESS);
        subtaskNew2.setId(2);
        subtaskNew2.setEpicId(3);
        historyManager.add(subtaskNew2);

        history = historyManager.getHistory();
        assertEquals(history.size(), 2, "Количество историй не равно ожидаемым");
        assertEquals(history.get(1).getTitle(), subtaskNew2.getTitle(), "Title НЕ изменился везде");
        assertEquals(history.get(1).getDescription(), subtaskNew2.getDescription(), "Description НЕ изменился везде");
        assertEquals(history.get(1).getStatus(), subtaskNew2.getStatus(), "Status НЕ изменился везде");
        assertEquals(
                ((Subtask) history.get(1)).getEpicId(),
                subtaskNew2.getEpicId(),
                "Связь к эпику НЕ изменилась"
        );

        Task task1 = new Task(3, "Title", "Description", Status.NEW);
        historyManager.add(task1);
        Task task2 = new Task(3, "Title update title", "Title update description", Status.DONE);
        historyManager.add(task2);

        history = historyManager.getHistory();
        assertEquals(history.size(), 3, "Количество историй не равно ожидаемым");
        assertEquals(history.get(2).getTitle(), task2.getTitle(), "Title НЕ изменился везде");
        assertEquals(history.get(2).getDescription(), task2.getDescription(), "Description НЕ изменился везде");
        assertEquals(history.get(2).getStatus(), task2.getStatus(), "Status НЕ изменился везде");
    }

    @Test
    @DisplayName("Проверяем, что история хранит последние 10 записей")
    void getHistory_returnListOf10Elements_add11Tasks() {
        for (int i = 0; i < 15; i++) {
            historyManager.add(new Task(i + 1, "Title", "Description", Status.NEW));
        }

        assertEquals(historyManager.getHistory().size(), 15, "Размер истории не ограничен");
    }

    @Test
    @DisplayName("Проверяем, что задачи записываются в историю")
    void add_setTaskOrSubtaskOrEpic() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        epic.setId(1);
        Subtask subtask = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        subtask.setId(2);
        Task task = new Task(3, "Title", "Description", Status.NEW);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        List<Task> tasks = historyManager.getHistory();

        assertEquals(tasks.size(), 3, "История не записывается");
        assertEquals(tasks.get(0), epic, "Отсутствует эпик в истории");
        assertEquals(tasks.get(1), task, "Отсутствует задача в истории");
        assertEquals(tasks.get(2), subtask, "Отсутствует подзадача в истории");
    }

    @Test
    @DisplayName("Проверяем, что задачи удаляются из истории")
    void remove_checkRemoveTasks() {
        Task task1 = new Task(1, "Title", "Description", Status.NEW);
        Task task2 = new Task(2, "Title", "Description", Status.NEW);
        Task task3 = new Task(3, "Title", "Description", Status.NEW);
        Task task4 = new Task(4, "Title", "Description", Status.NEW);
        Task task5 = new Task(5, "Title", "Description", Status.NEW);
        Task task6 = new Task(6, "Title", "Description", Status.NEW);
        Task task7 = new Task(7, "Title", "Description", Status.NEW);
        Task task8 = new Task(8, "Title", "Description", Status.NEW);
        Task task9 = new Task(9, "Title", "Description", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);

        historyManager.remove(5);
        assertEquals(historyManager.getHistory(), List.of(task1, task2, task3, task4, task6, task7, task8, task9));

        historyManager.remove(1);
        assertEquals(historyManager.getHistory(), List.of(task2, task3, task4, task6, task7, task8, task9));

        historyManager.remove(9);
        assertEquals(historyManager.getHistory(), List.of(task2, task3, task4, task6, task7, task8));
    }

    @Test
    @DisplayName("Проверяем, что добавление и удаление единственной таски верно")
    void remove_checkRemoveOneFirstTask() {
        Task task1 = new Task(1, "Title", "Description", Status.NEW);
        historyManager.add(task1);

        historyManager.remove(1);
        assertEquals(historyManager.getHistory().size(), 0, "История не удалилась");
    }

    @Test
    @DisplayName("Проверяем, что добавление и удаление двух тасок верно")
    void remove_checkRemoveDoubleFirstTask() {
        Task task1 = new Task(1, "Title", "Description", Status.NEW);
        Task task2 = new Task(2, "Title", "Description", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);
        assertEquals(historyManager.getHistory().size(), 1, "Некорректное удаление связей при пограничных значениях");
    }
}