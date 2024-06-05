package services.task.memory;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Assertions;
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
    @DisplayName("Проверяем создание Task")
    void createTask_returnNewTask() {
        String title = "Title";
        String description = "Description";
        Status status = Status.NEW;
        Task task = new Task(title, description, status);

        Assertions.assertNull(task.getId(), "id задачи не null");

        Task createdTask = taskManager.createTask(task);
        assertNotNull(createdTask, "Созданная задача null");
        assertEquals(createdTask.getTitle(), title, "Заголовок отличается после создания задачи");
        assertEquals(createdTask.getDescription(), description, "Описание отличается после создания задачи");
        assertEquals(createdTask.getStatus(), status, "Статус отличается после создания задачи");
        assertNotNull(createdTask.getId(), "id после создания задачи null");
    }

    @Test
    @DisplayName("Поиск существующий Task")
    void getTask_returnExistsTask() {
        Task task = new Task("Title", "Description", Status.NEW);
        Task createdTask = taskManager.createTask(task);

        Task findTask = taskManager.getTask(createdTask.getId());

        assertEquals(createdTask, findTask, "Созданная и найденная задача не равны");
    }

    @Test
    @DisplayName("Поиск несуществующий Task")
    void getTask_returnNull_taskNotExist() {
        Task findTask = taskManager.getTask(0);

        assertNull(findTask, "Найдена несуществующая задача");
    }

    @Test
    @DisplayName("Обновление существующий Task")
    void updateTask_returnUpdatedTask_taskExist() {
        Task task = new Task("Title", "Description", Status.NEW);
        Task createdTask = taskManager.createTask(task);
        Task updateTask = new Task("Updated title", "Updated description", Status.DONE);
        updateTask.setId(createdTask.getId());

        Task updatedTask = taskManager.updateTask(updateTask);

        assertNotNull(updatedTask, "Не удалось обновить задачу");
        assertEquals(createdTask.getId(), updatedTask.getId(), "id обновленной задачи не равен входным параметрам");
        assertEquals(createdTask.getTitle(), updatedTask.getTitle(), "title обновленной задачи не равен входным параметрам");
        assertEquals(createdTask.getDescription(), updatedTask.getDescription(), "description обновленной задачи не равен входным параметрам");
        assertEquals(createdTask.getStatus(), updatedTask.getStatus(), "status обновленной задачи не равен входным параметрам");
    }

    @Test
    @DisplayName("Обновление несуществующий Task")
    void updateTask_returnNull_taskNotExist() {
        Task task = new Task("Title", "Description", Status.NEW);
        task.setId(1);
        Task updatedTask = taskManager.updateTask(task);

        assertNull(updatedTask, "Обновилась неизвестная задача");
    }

    @Test
    @DisplayName("Удаление существующей задачи Task")
    void removeTask_removeExistTask() {
        Task cretaedTask = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.createTask(new Task("Title", "Description", Status.NEW));

        taskManager.removeTask(cretaedTask.getId());

        assertEquals(taskManager.getAllTasks().size(), 2, "Количество задач не соответствует ожидаемому после удаления");
        assertNull(taskManager.getTask(cretaedTask.getId()), "Задача не удалилась");
    }

    @Test
    @DisplayName("Удаление всех существующих задач Task")
    void removeAllTasks_removeExistTask() {
        taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.createTask(new Task("Title", "Description", Status.NEW));

        taskManager.removeAllTasks();

        assertEquals(taskManager.getAllTasks().size(), 0, "Задачи не удалились");
    }

    @Test
    @DisplayName("Получение всех задач Task")
    void getAllTasks_returnListTasks() {
        taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.createTask(new Task("Title", "Description", Status.NEW));

        assertEquals(taskManager.getAllTasks().size(), 3, "Количество задач не соответствует созданному количеству");
    }

    // ---
    @Test
    @DisplayName("Проверяем создание Subtask")
    void createSubtask_returnNewSubtask() {
        String title = "Title";
        String description = "Description";
        Status status = Status.NEW;
        Subtask subtask = new Subtask(title, description, status);
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));

        Subtask createdSubtask = taskManager.createSubtask(epic, subtask);

        assertNotNull(createdSubtask, "Созданная подзадача null");
        assertEquals(createdSubtask.getTitle(), title, "Заголовок отличается после создания подзадачи");
        assertEquals(createdSubtask.getDescription(), description, "Описание отличается после создания подзадачи");
        assertEquals(createdSubtask.getStatus(), status, "Статус отличается после создания подзадачи");
        assertNotNull(createdSubtask.getId(), "id после создания подзадачи null");
        assertEquals(epic.getId(), subtask.getEpicId(), "связь id эпика в подзадачи не соответствует эпику");
    }

    @Test
    @DisplayName("Поиск существующий Subtask")
    void getSubtask_returnExistsSubtask() {
        Subtask task = new Subtask("Title", "Description", Status.NEW);
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Subtask createdSubtask = taskManager.createSubtask(epic, task);

        Subtask findSubtask = taskManager.getSubtask(createdSubtask.getId());

        assertEquals(createdSubtask, findSubtask, "Созданная и найденная подзадача не равны");
    }

    @Test
    @DisplayName("Поиск несуществующий Subtask")
    void getSubtask_returnNull_SubtaskNotExist() {
        Subtask findSubtask = taskManager.getSubtask(0);

        assertNull(findSubtask, "Найдена несуществующая подзадача");
    }

    @Test
    @DisplayName("Обновление существующий Subtask")
    void updateSubtask_returnUpdatedSubtask_subtaskExist() {
        Subtask task = new Subtask("Title", "Description", Status.NEW);
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Subtask createdSubtask = taskManager.createSubtask(epic, task);
        Subtask updateSubtask = new Subtask("Updated title", "Updated description", Status.DONE);
        updateSubtask.setId(createdSubtask.getId());

        Subtask updatedSubtask = taskManager.updateSubtask(updateSubtask);

        assertNotNull(updatedSubtask, "Не удалось обновить подзадачу");
        assertEquals(createdSubtask.getId(), updatedSubtask.getId(), "id обновленной подзадачи не равен входным параметрам");
        assertEquals(createdSubtask.getTitle(), updatedSubtask.getTitle(), "title обновленной подзадачи не равен входным параметрам");
        assertEquals(createdSubtask.getDescription(), updatedSubtask.getDescription(), "description обновленной подзадачи не равен входным параметрам");
        assertEquals(createdSubtask.getStatus(), updatedSubtask.getStatus(), "status обновленной подзадачи не равен входным параметрам");
    }

    @Test
    @DisplayName("Обновление несуществующий Subtask")
    void updateSubtask_returnNull_taskNotExist() {
        Subtask task = new Subtask("Title", "Description", Status.NEW);
        task.setId(1);

        Subtask updatedSubtask = taskManager.updateSubtask(task);

        assertNull(updatedSubtask, "Обновилась неизвестная подзадача");
    }

    @Test
    @DisplayName("Удаление существующей подзадачи Subtask")
    void removeSubtask_removeExistSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Subtask cretaedSubtask = taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));

        taskManager.removeSubtask(cretaedSubtask.getId());

        assertEquals(taskManager.getAllSubtasks().size(), 2, "Количество подзадач не соответствует ожидаемому после удаления");
        assertNull(taskManager.getSubtask(cretaedSubtask.getId()), "Подзадача не удалилась");
    }

    @Test
    @DisplayName("Удаление всех существующих подзадач Subtask")
    void removeAllSubtasks_removeExistSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(
                taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW)),
                new Subtask("Title", "Description", Status.NEW)
        );

        taskManager.removeAllSubtasks();

        assertEquals(taskManager.getAllSubtasks().size(), 0, "Подзадачи не удалились");
    }

    @Test
    @DisplayName("Получение всех задач Subtask")
    void getAllSubtasks_returnListSubtasks() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(
                taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW)),
                new Subtask("Title", "Description", Status.NEW)
        );

        assertEquals(taskManager.getAllSubtasks().size(), 3, "Количество подзадач не соответствует созданному количеству");
    }

    // ---
    @Test
    @DisplayName("Проверяем создание Epic")
    void createEpic_returnNewEpic() {
        String title = "Title";
        String description = "Description";
        Status status = Status.DONE;

        Epic createdEpic = taskManager.createEpic(new Epic(title, description, status));

        assertNotNull(createdEpic, "Созданный эпик null");
        assertEquals(createdEpic.getTitle(), title, "Заголовок отличается после создания эпика");
        assertEquals(createdEpic.getDescription(), description, "Описание отличается после создания эпика");
        assertEquals(createdEpic.getStatus(), Status.NEW, "Статус созданного эпика не NEW");
        assertNotNull(createdEpic.getId(), "id после создания эпика null");
        assertEquals(createdEpic.getSubtaskIds().size(), 0, "Количество подзадач больше 0");
    }

    @Test
    @DisplayName("Поиск существующего Epic")
    void getEpic_returnExistsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Epic createdEpic = taskManager.createEpic(epic);

        Epic findEpic = taskManager.getEpic(createdEpic.getId());

        assertEquals(createdEpic, findEpic, "Созданная и найденная подзадача не равны");
    }

    @Test
    @DisplayName("Поиск несуществующего Epic")
    void getEpic_returnNull_epicNotExist() {
        Epic findEpic = taskManager.getEpic(0);

        assertNull(findEpic, "Найдена несуществующая подзадача");
    }

    @Test
    @DisplayName("Обновление существующего Epic")
    void updateEpic_returnUpdatedEpic_epicExist() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Epic createdEpic = taskManager.createEpic(epic);
        Epic updateEpic = new Epic("Updated title", "Updated description", Status.DONE);
        updateEpic.setId(createdEpic.getId());

        Epic updatedEpic = taskManager.updateEpic(updateEpic);

        assertNotNull(updatedEpic, "Не удалось обновить эпик");
        assertEquals(createdEpic.getId(), updatedEpic.getId(), "id обновленного эпика не равен входным параметрам");
        assertEquals(createdEpic.getTitle(), updatedEpic.getTitle(), "title обновленной эпика не равен входным параметрам");
        assertEquals(createdEpic.getDescription(), updatedEpic.getDescription(), "description обновленного эпика не равен входным параметрам");
        assertEquals(createdEpic.getStatus(), updatedEpic.getStatus(), "status обновленного эпика не равен входным параметрам");
    }

    @Test
    @DisplayName("Обновление несуществующий Epic")
    void updateEpic_returnNull_taskNotExist() {
        Epic task = new Epic("Title", "Description", Status.NEW);
        task.setId(1);

        Epic updatedEpic = taskManager.updateEpic(task);

        assertNull(updatedEpic, "Обновилась неизвестная подзадача");
    }

    @Test
    @DisplayName("Удаление существующего Epic")
    void removeEpic_removeExistEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));

        taskManager.removeEpic(epic.getId());

        assertEquals(taskManager.getAllEpics().size(), 2, "Количество эпиков' не соответствует ожидаемому после удаления");
        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удалился");
        assertEquals(taskManager.getEpicSubtasks(epic).size(), 0, "Количество подзадач у эпика не соответствует ожидаемому после удаления");
    }

    @Test
    @DisplayName("Удаление всех существующих Epic")
    void removeAllEpics_removeExistEpic() {
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(
                taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW)),
                new Subtask("Title", "Description", Status.NEW)
        );

        taskManager.removeAllEpics();

        assertEquals(taskManager.getAllEpics().size(), 0, "Эпики не удалились");
        assertEquals(taskManager.getAllSubtasks().size(), 0, "Подзадачи не удалились");
    }

    @Test
    @DisplayName("Получение всех Epic")
    void getAllEpics_returnListEpics() {
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));

        assertEquals(taskManager.getAllEpics().size(), 3, "Количество эпиков' не соответствует созданному количеству");
    }

    @Test
    @DisplayName("Получение всех подзадач Epic")
    void getEpicSubtasks_returnSubtaskList() {
        Epic epic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));
        taskManager.createSubtask(epic, new Subtask("Title", "Description", Status.NEW));

        assertEquals(taskManager.getEpicSubtasks(epic).size(), 2, "Количество эпиков' не соответствует созданному количеству");
    }

    @Test
    @DisplayName("Проверяем изменение NEW статусов у Epic")
    void checkEpicStatus_returnNew_setSubtaskWithNewStatus() {
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
    void checkEpicStatus_returnInProgress_setAllSubtaskStatus() {
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
    void checkEpicStatus_returnDone_setSubtaskWithDoneStatus() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtaskDone = new Subtask("Done Subtask title", "Done Subtask description", Status.DONE);

        taskManager.createSubtask(createdEpic, subtaskDone);
        assertEquals(createdEpic.getSubtaskIds().size(), 1, "Сабтаска не добавилась в эпик");
        assertEquals(createdEpic.getStatus(), Status.DONE, "Созданный эпик без подзадач со статусом не DONE");
    }

    @Test
    @DisplayName("Проверяем что задача с заданным внешним id не влияют на созданный id")
    void checkCreateTaskId_setNewId_setExternalIdHasNoEffectForNewTask() {
        Task task = new Task("Title", "Description", Status.NEW);
        task.setId(102);

        Task createdTask = taskManager.createTask(task);

        assertNotEquals(createdTask.getId(), 102, "id созданной задачи и произвольной совпали");
    }

    @Test
    @DisplayName("Проверяем что эпик с заданным внешним id не влияют на созданный id")
    void checkCreateEpicId_setNewId_setExternalIdHasNoEffectForNewEpic() {
        Epic epic = new Epic("Epic title", "Epic description", Status.DONE);
        epic.setId(100);

        Epic createdEpic = taskManager.createEpic(epic);

        assertNotEquals(createdEpic.getId(), 100, "id созданного эпика и произвольного совпали");
    }

    @Test
    @DisplayName("Проверяем что подзадача с заданным внешним id не влияют на созданный id")
    void checkCreateSubtaskId_setNewId_setExternalIdHasNoEffectForNewSubtask() {
        Epic createdEpic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtaskNew = new Subtask("New Subtask title", "New Subtask description", Status.NEW);
        subtaskNew.setId(101);

        Subtask createdSubtask = taskManager.createSubtask(createdEpic, subtaskNew);

        assertNotEquals(createdSubtask.getId(), 101, "id созданной подзадачи и произвольной совпали");
    }

    @Test
    @DisplayName("Проверяем что история возвращает просмотренные задачи / подзадачи / эпики")
    void getHistory_returnShownTaskList_afterGetTaskSubtaskEpics() {
        Epic createdEpic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtask = taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Task task = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(createdEpic.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 3, "История просмотров не равна количеству просмотров");
        assertEquals(histories.get(0), task, "История не хронологична");
        assertEquals(histories.get(1), subtask, "История не хронологична");
        assertEquals(histories.get(2), createdEpic, "История не хронологична");
    }

    @Test
    @DisplayName("Проверяем что история удаляет просмотренные задачи")
    void getHistory_returnShownTaskList_afterDeleteTask() {
        Task task1 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task3 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 3, "История просмотров не равна количеству просмотров");

        taskManager.removeTask(task2.getId());

        histories = taskManager.getHistory();
        assertEquals(histories.size(), 2, "История просмотров не равна количеству просмотров после удаления");

        assertEquals(histories, List.of(task1, task3), "Task2 не удалился");
    }

    @Test
    @DisplayName("Проверяем что история удаляет просмотренные подзадачи")
    void getHistory_returnShownTaskList_afterDeleteSubtask() {
        Epic createdEpic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtask1 = taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask2 = taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask3 = taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.getEpic(createdEpic.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 4, "История просмотров не равна количеству просмотров");

        taskManager.removeTask(subtask2.getId());

        histories = taskManager.getHistory();
        assertEquals(histories.size(), 3, "История просмотров не равна количеству просмотров после удаления");

        assertEquals(histories, List.of(createdEpic, subtask1, subtask3), "Subtask2 не удалился");
    }

    @Test
    @DisplayName("Проверяем что история удаляет просмотренные эпики")
    void getHistory_returnShownTaskList_afterDeleteEpic() {
        Epic createdEpic1 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic2 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic3 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic4 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtask1 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask2 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.getEpic(createdEpic1.getId());
        taskManager.getEpic(createdEpic2.getId());
        taskManager.getEpic(createdEpic3.getId());
        taskManager.getEpic(createdEpic4.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 6, "История просмотров не равна количеству просмотров");

        taskManager.removeEpic(createdEpic3.getId());

        histories = taskManager.getHistory();
        assertEquals(histories.size(), 5, "История просмотров не равна количеству просмотров после удаления");
        assertEquals(histories, List.of(createdEpic1, createdEpic2, createdEpic4, subtask1, subtask2), "Epic3 не удалился");

        taskManager.removeEpic(createdEpic2.getId());
        histories = taskManager.getHistory();

        assertEquals(histories.size(), 2, "История просмотров не равна количеству просмотров после удаления");
        assertEquals(histories, List.of(createdEpic1, createdEpic4), "Epic2 не удалился");
    }

    @Test
    @DisplayName("Проверяем что история удаляет все просмотренные задачи")
    void getHistory_returnShownTaskList_afterDeleteAllTasks() {
        Task task1 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task3 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Epic createdEpic1 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic2 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic3 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic4 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtask1 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask2 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask3 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getEpic(createdEpic1.getId());
        taskManager.getEpic(createdEpic2.getId());
        taskManager.getEpic(createdEpic3.getId());
        taskManager.getEpic(createdEpic4.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 10, "История просмотров не равна количеству просмотров");

        taskManager.removeAllTasks();

        histories = taskManager.getHistory();
        assertEquals(histories.size(), 7, "История просмотров не равна количеству просмотров после удаления");
        assertEquals(histories, List.of(createdEpic1, createdEpic2, createdEpic3, createdEpic4, subtask1, subtask2, subtask3), "Не удалены все задачи");
    }

    @Test
    @DisplayName("Проверяем что история удаляет все просмотренные подзадачи")
    void getHistory_returnShownTaskList_afterDeleteAllSubtasks() {
        Task task1 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task3 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Epic createdEpic1 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic2 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic3 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic4 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtask1 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask2 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask3 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getEpic(createdEpic1.getId());
        taskManager.getEpic(createdEpic2.getId());
        taskManager.getEpic(createdEpic3.getId());
        taskManager.getEpic(createdEpic4.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 10, "История просмотров не равна количеству просмотров");

        taskManager.removeAllSubtasks();

        histories = taskManager.getHistory();
        assertEquals(histories.size(), 7, "История просмотров не равна количеству просмотров после удаления");
        assertEquals(histories, List.of(task1, task2, task3, createdEpic1, createdEpic2, createdEpic3, createdEpic4), "Не удалены все подзадачи");
    }

    @Test
    @DisplayName("Проверяем что история удаляет все просмотренные эпики и подзадачи эпиков")
    void getHistory_returnShownTaskList_afterDeleteAllEpicsWithSubtasks() {
        Task task1 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Task task3 = taskManager.createTask(new Task("Title", "Description", Status.NEW));
        Epic createdEpic1 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic2 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic3 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Epic createdEpic4 = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));
        Subtask subtask1 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask2 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask3 = taskManager.createSubtask(createdEpic2, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getEpic(createdEpic1.getId());
        taskManager.getEpic(createdEpic2.getId());
        taskManager.getEpic(createdEpic3.getId());
        taskManager.getEpic(createdEpic4.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        List<Task> histories = taskManager.getHistory();

        assertEquals(histories.size(), 10, "История просмотров не равна количеству просмотров");

        taskManager.removeAllEpics();

        histories = taskManager.getHistory();
        assertEquals(histories.size(), 3, "История просмотров не равна количеству просмотров после удаления");
        assertEquals(histories, List.of(task1, task2, task3), "Не удалены все эпики + подзадачи");
    }
}