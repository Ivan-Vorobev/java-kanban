package services.task.memory;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.history.memory.InMemoryHistoryManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверяем работу InMemoryTaskManagerTest")
class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    @DisplayName("Проверяем работу с Task")
    void shouldCreateTask() {
        Task task = new Task("Title", "Description", Status.NEW);
        Task createdTask = taskManager.createTask(task);
        Task findTask = taskManager.getTask(createdTask.getId());
        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(createdTask, "Созданная задача null");
        assertNotNull(findTask, "Найденная задача null");
        assertNotNull(tasks, "Список задач null");
        assertEquals(createdTask, findTask, "Созданная и найденная задача не равны");
        assertEquals(createdTask, tasks.get(0), "Первая созданная и первая найденная из списка задача не равны");
        assertEquals(tasks.size(), 1, "Добавлено больше 1 задачи");

        taskManager.removeTask(createdTask.getId());
        assertNull(taskManager.getTask(createdTask.getId()), "Задача не удалилась");

        taskManager.createTask(task);
        taskManager.createTask(task);
        taskManager.createTask(task);
        assertEquals(taskManager.getAllTasks().size(), 3, "Добавлено больше 3-х задачи");

        taskManager.removeAllTasks();
        assertEquals(taskManager.getAllTasks().size(), 0, "Все задачи не удалились");
    }


    @Test
    @DisplayName("Проверяем работу с Subtask")
    void shouldCreateSubtask() {
        Subtask subtask = new Subtask("Subtask title", "Subtask description", Status.NEW);
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Subtask createdSubtask = taskManager.createSubtask(epic, subtask);
        Subtask findSubtask = taskManager.getSubtask(createdSubtask.getId());
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(createdSubtask, "Созданная подзадача null");
        assertNotNull(findSubtask, "Найденная подзадача null");
        assertNotNull(subtasks, "Список подзадач null");
        assertEquals(createdSubtask, findSubtask, "Созданная и найденная подзадача не равны");
        assertEquals(createdSubtask, subtasks.get(0), "Первая созданная и первая найденная из списка подзадача не равны");
        assertEquals(subtasks.size(), 1, "Добавлено больше 1 подзадачи");

        taskManager.removeSubtask(createdSubtask.getId());
        assertNull(taskManager.getTask(createdSubtask.getId()), "Подзадача не удалилась");

        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask);
        assertEquals(taskManager.getAllSubtasks().size(), 3, "Добавлено больше 3-х подзадач");

        taskManager.removeAllSubtasks();
        assertEquals(taskManager.getAllSubtasks().size(), 0, "Все подзадачи не удалились");
    }

    @Test
    @DisplayName("Проверяем работу с Epic")
    void shouldCreateEpic() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Epic createdEpic = taskManager.createEpic(epic);
        Epic findEpic = taskManager.getEpic(createdEpic.getId());
        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getEpicSubtasks(createdEpic);

        assertNotNull(createdEpic, "Созданный эпик null");
        assertNotNull(findEpic, "Найденный эпик null");
        assertNotNull(epics, "Список эпиков null");
        assertNotNull(subtasks, "Список подзадач эпика null");
        assertEquals(createdEpic, findEpic, "Созданный и найденный эпик не равны");
        assertEquals(createdEpic, epics.get(0), "Первый созданный и первый найденный из списка эпик не равны");
        assertEquals(epics.size(), 1, "Добавлено больше 1 эпика");

        Subtask subtaskNew = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        taskManager.createSubtask(createdEpic, subtaskNew);
        assertEquals(taskManager.getAllSubtasks().size(), 1, "Добавлено больше 1 подзадачи");

        taskManager.removeEpic(createdEpic.getId());
        assertNull(taskManager.getEpic(createdEpic.getId()), "Эпик не удалился");
        assertEquals(taskManager.getAllSubtasks().size(), 0, "Подзадачи эпика не удалились");

        createdEpic = taskManager.createEpic(epic);
        taskManager.createSubtask(createdEpic, subtaskNew);
        taskManager.createSubtask(createdEpic, subtaskNew);
        taskManager.createSubtask(createdEpic, subtaskNew);
        taskManager.createSubtask(taskManager.createEpic(epic), subtaskNew);
        taskManager.createSubtask(taskManager.createEpic(epic), subtaskNew);
        assertEquals(taskManager.getAllEpics().size(), 3, "Добавлено больше 3-х эпиков");
        assertEquals(taskManager.getAllSubtasks().size(), 5, "Добавлено не 5 подзадач");

        taskManager.removeAllEpics();
        assertEquals(taskManager.getAllEpics().size(), 0, "Все эпики не удалились");
        assertEquals(taskManager.getAllSubtasks().size(), 0, "Все подзадачи не удалились");
    }

    @Test
    @DisplayName("Проверяем изменение NEW статусов у Epic")
    void shouldChangeEpicStatusToNew() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtaskNew = new Subtask("New Subtask title", "New Subtask description", Status.NEW);

        assertEquals(createdEpic.getStatus(), Status.NEW, "Созданный эпик без подзадач со статусом не NEW");

        taskManager.createSubtask(createdEpic, subtaskNew);
        assertEquals(createdEpic.getSubtaskIds().size(), 1, "Сабтаска не добавилась в эпик");
        assertEquals(createdEpic.getStatus(), Status.NEW, "Созданный эпик без подзадач со статусом не NEW");
    }

    @Test
    @DisplayName("Проверяем изменение IN_PROGRESS статусов у Epic")
    void shouldChangeEpicStatusToInProgress() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtaskNew = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        Subtask subtaskInProgress = new Subtask("In progress Subtask title", "In progress Subtask description", Status.IN_PROGRESS);
        Subtask subtaskDone = new Subtask("Done Subtask title", "Done Subtask description", Status.DONE);

        taskManager.createSubtask(createdEpic, subtaskInProgress);
        assertEquals(createdEpic.getSubtaskIds().size(), 1, "Сабтаска не добавилась в эпик");
        assertEquals(createdEpic.getStatus(), Status.IN_PROGRESS, "Созданный эпик без подзадач со статусом не IN_PROGRESS");

        taskManager.createSubtask(createdEpic, subtaskNew);
        assertEquals(createdEpic.getSubtaskIds().size(), 2, "Сабтаска не добавилась в эпик");
        assertEquals(createdEpic.getStatus(), Status.IN_PROGRESS, "Созданный эпик без подзадач со статусом не IN_PROGRESS");

        taskManager.createSubtask(createdEpic, subtaskDone);
        assertEquals(createdEpic.getSubtaskIds().size(), 3, "Сабтаска не добавилась в эпик");
        assertEquals(createdEpic.getStatus(), Status.IN_PROGRESS, "Созданный эпик без подзадач со статусом не IN_PROGRESS");
    }

    @Test
    @DisplayName("Проверяем изменение DONE статусов у Epic")
    void shouldChangeEpicStatusToDone() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtaskDone = new Subtask("Done Subtask title", "Done Subtask description", Status.DONE);

        taskManager.createSubtask(createdEpic, subtaskDone);
        assertEquals(createdEpic.getSubtaskIds().size(), 1, "Сабтаска не добавилась в эпик");
        assertEquals(createdEpic.getStatus(), Status.DONE, "Созданный эпик без подзадач со статусом не DONE");
    }

    @Test
    @DisplayName("Проверяем что задачи с заданным внешним id не влияют на созданные")
    void shouldCheckGenerateId() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        epic.setId(100);
        Epic createdEpic = taskManager.createEpic(epic);
        assertNotEquals(createdEpic.getId(), 100, "id созданного эпика и произвольного совпали");

        Subtask subtaskNew = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        subtaskNew.setId(101);
        Subtask createdSubtask = taskManager.createSubtask(createdEpic, subtaskNew);
        assertNotEquals(createdSubtask.getId(), 101, "id созданной подзадачи и произвольной совпали");

        Task task = new Task("Title", "Description", Status.NEW);
        task.setId(102);
        Task createdTask = taskManager.createTask(task);
        assertNotEquals(createdTask.getId(), 102, "id созданной задачи и произвольной совпали");
    }

    @Test
    @DisplayName("Проверяем неизменяемость полей при создании Task")
    void shouldNotChangeTaskFields() {
        String title = "Title";
        String description = "Description";
        Status status = Status.NEW;

        Task task = new Task(title, description, status);
        Task createdTask = taskManager.createTask(task);
        assertEquals(createdTask.getTitle(), title, "Title отличается");
        assertEquals(createdTask.getDescription(), description, "Description отличается");
        assertEquals(createdTask.getStatus(), status, "Status отличается");
    }

    @Test
    @DisplayName("Проверяем неизменяемость полей при создании Subtask")
    void shouldNotChangeSubtaskFields() {
        String title = "Title";
        String description = "Description";
        Status status = Status.NEW;

        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Subtask subtask = new Subtask(title, description, status);
        Subtask createdSubtask = taskManager.createSubtask(epic, subtask);
        assertEquals(createdSubtask.getTitle(), title, "Title отличается");
        assertEquals(createdSubtask.getDescription(), description, "Description отличается");
        assertEquals(createdSubtask.getStatus(), status, "Status отличается");
    }

    @Test
    @DisplayName("Проверяем неизменяемость полей при создании Epic")
    void shouldNotChangeEpicFields() {
        String title = "Title";
        String description = "Description";

        Epic epic = new Epic(title, description, Status.DONE);
        Epic createdEpic = taskManager.createEpic(epic);
        assertEquals(createdEpic.getTitle(), title, "Title отличается");
        assertEquals(createdEpic.getDescription(), description, "Description отличается");
    }
}