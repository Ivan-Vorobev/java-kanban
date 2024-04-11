import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import services.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static TaskManager taskManager;

    public static void main(String[] args) {
        System.out.println("Поехали!");

        taskManager = new TaskManager();
        taskManager.createTask(createNewTask(1));
        taskManager.createEpic(createNewEpic(2));
        taskManager.createSubtask(createNewEpic(3), createNewSubtask(4));
        assertCreateTask();
        assertCreateSubtask();
        assertCreateEpic();
        assertEpicSubtasks();
    }

    private static void assertCreateTask() {
        assertClearManager();
        Task task = taskManager.createTask(new Task("Task-1", "Сделать таску 1", Status.NEW));
        System.out.println("Создаем Task: " + task);
        Task newTask = createNewTask(task.getId());
        taskManager.updateTask(newTask);
        System.out.println("Изменяем параметры на: " + newTask);
        System.out.println("Task изменился: " + task);
        System.out.println();
        assert task.getTitle().equals(newTask.getTitle()) : "Поле title не изменилось";
        assert task.getDescription().equals(newTask.getDescription()) : "Поле description не изменилось";
        assert task.getStatus().equals(newTask.getStatus()) : "Поле status не изменилось";

        Task settedTask = taskManager.getTask(task.getId());
        System.out.println("Проверяем что Task достается из Manager: " + task);
        assert settedTask != null : "taskManager.getTask не вернул созданную таску";
        assert settedTask.equals(task) : "taskManager.getTask вернул не ту задачу что в нем создали";

        ArrayList<Task> tasks = taskManager.getAllTasks();
        System.out.println("Проверяем что TaskManager возвращает правильные коллекции:");
        assert tasks.size() == 1 : "Количество элементов не совпадает";
        assert tasks.contains(task) : "TaskManager не содержит насеченной задачи в его коллекции";

        taskManager.removeTask(task.getId());
        tasks = taskManager.getAllTasks();
        System.out.println("Проверяем что TaskManager удаляет элементы:");
        assert tasks.isEmpty() : "Количество элементов не пусто";
    }

    private static void assertClearManager() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        assert taskManager.getAllTasks().isEmpty() : "TaskList не пуст";
        assert taskManager.getAllSubtasks().isEmpty() : "SubtaskList не пуст";
        assert taskManager.getAllEpics().isEmpty() : "EpicList не пуст";
    }

    private static void assertCreateSubtask() {
        assertClearManager();
        Epic epic = taskManager.createEpic(new Epic("Epic-1", "Epic description", Status.NEW));
        Subtask task = taskManager.createSubtask(
                epic,
                new Subtask("Subtask-1", "Сделать подзадачу 1", Status.NEW)
        );
        Subtask newTask = createNewSubtask(task.getId());
        taskManager.updateSubtask(newTask);
        System.out.println("Изменяем параметры на: " + newTask);
        System.out.println("Subtask изменился: " + task);
        System.out.println();
        assert task.getTitle().equals(newTask.getTitle()) : "Поле title не изменилось";
        assert task.getDescription().equals(newTask.getDescription()) : "Поле description не изменилось";
        assert task.getStatus().equals(newTask.getStatus()) : "Поле status не изменилось";

        Subtask settedSubtask = taskManager.getSubtask(task.getId());
        System.out.println("Проверяем что Subtask достается из Manager: " + task);
        assert settedSubtask != null : "taskManager.getSubtask не вернул созданную подзадачу";
        assert settedSubtask.equals(task) : "taskManager.getSubtask вернул не ту подзадачу что в нем создали";

        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        ArrayList<Epic> epics = taskManager.getAllEpics();
        System.out.println("Проверяем что TaskManager возвращает правильные коллекции:");
        assert subtasks.size() == 1 : "Количество элементов Subtask не совпадает";
        assert subtasks.contains(task) : "TaskManager не содержит насеченной подзадачи в его коллекции";
        assert epics.size() == 1 : "Количество элементов Epic не совпадает";
        assert epics.contains(epic) : "TaskManager не содержит насеченного эпика в его коллекции";

        taskManager.removeSubtask(task.getId());
        subtasks = taskManager.getAllSubtasks();
        System.out.println("Проверяем что TaskManager удаляет элементы:");
        assert subtasks.isEmpty() : "Количество элементов не пусто";
        assert taskManager.getEpicSubtasks(epic).isEmpty() : "Количество элементов не пусто";
        assert task.getEpicId() == null : "Связь подзадачи с эпиком не очистилась";
    }

    private static void assertCreateEpic() {
        assertClearManager();
        Epic task = taskManager.createEpic(new Epic("Epic-1", "Сделать эпик 1", Status.IN_PROGRESS));
        System.out.println("Создаем Epic: " + task);
        Epic newTask = createNewEpic(task.getId());
        taskManager.updateEpic(newTask);
        System.out.println("Изменяем параметры на: " + newTask);
        System.out.println("Epic изменился: " + task);
        System.out.println();
        assert task.getTitle().equals(newTask.getTitle()) : "Поле title не изменилось";
        assert task.getDescription().equals(newTask.getDescription()) : "Поле description не изменилось";
        assert task.getStatus().equals(Status.NEW) : "Поле status не правильно вычислился";

        Epic settedEpic = taskManager.getEpic(task.getId());
        System.out.println("Проверяем что Epic достается из Manager: " + task);
        assert settedEpic != null : "taskManager.getEpic не вернул созданный эпик";
        assert settedEpic.equals(task) : "taskManager.getEpic вернул не тот эпик что в нем создали";

        ArrayList<Epic> epics = taskManager.getAllEpics();
        System.out.println("Проверяем что TaskManager возвращает правильные коллекции:");
        assert epics.size() == 1 : "Количество элементов Epic не совпадает";
        assert epics.contains(task) : "TaskManager не содержит насеченного эпика в его коллекции";

        taskManager.removeEpic(task.getId());
        epics = taskManager.getAllEpics();
        System.out.println("Проверяем что TaskManager удаляет элементы:");
        assert epics.isEmpty() : "Количество элементов не пусто";
        assert taskManager.getEpicSubtasks(task).isEmpty() : "Количество элементов не пусто";
    }

    private static Task createNewTask(int taskId) {
        String title = "Task modify";
        String description = "Description modify";
        Status status = Status.IN_PROGRESS;
        Task task = new Task(taskId, title, description, status);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);

        assert task.getId() == taskId : "Поле id не насетилось при созданни класса";
        assert task.getTitle().equals(title) : "Поле title не насетилось при созданни класса";
        assert task.getDescription().equals(description) : "Поле description не насетилось при созданни класса";
        assert task.getStatus().equals(status) : "Поле status не насетилось при созданни класса";

        return task;
    }

    private static Subtask createNewSubtask(int taskId) {
        String title = "Task modify";
        String description = "Description modify";
        Status status = Status.IN_PROGRESS;
        Subtask task = new Subtask(title, description, status);
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);

        assert task.getId() == taskId : "Поле id не насетилось при созданни класса";
        assert task.getTitle().equals(title) : "Поле title не насетилось при созданни класса";
        assert task.getDescription().equals(description) : "Поле description не насетилось при созданни класса";
        assert task.getStatus().equals(status) : "Поле status не насетилось при созданни класса";

        return task;
    }

    private static Epic createNewEpic(int taskId) {
        String title = "Task modify";
        String description = "Description modify";
        Status status = Status.IN_PROGRESS;
        Epic task = new Epic(title, description, status);
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);

        assert task.getId() == taskId : "Поле id не насетилось при созданни класса";
        assert task.getTitle().equals(title) : "Поле title не насетилось при созданни класса";
        assert task.getDescription().equals(description) : "Поле description не насетилось при созданни класса";
        assert task.getStatus().equals(status) : "Поле status не насетилось при созданни класса";

        return task;
    }

    private static void assertEpicSubtasks() {
        Epic epic = taskManager.createEpic(new Epic("Epic-1", "Epic description", Status.NEW));
        Subtask subtask1 = taskManager.createSubtask(
                epic,
                new Subtask("Subtask-1", "Sabtask-1 description", Status.NEW)
        );
        Subtask subtask2 = taskManager.createSubtask(
                epic,
                new Subtask("Subtask-2", "Sabtask-2 description", Status.NEW)
        );
        Subtask subtask3 = taskManager.createSubtask(
                epic,
                new Subtask("Subtask-3", "Sabtask-3 description", Status.NEW)
        );

        ArrayList<Subtask> subtasks = taskManager.getEpicSubtasks(epic);
        HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>(subtasks.size());
        for (Subtask subtask : subtasks) {
            subtaskHashMap.put(subtask.getId(), subtask);
        }

        System.out.println("Создали Epic-1: " + epic);

        assert subtasks.size() == 3 : "Количество подзадач не совпадает с созданными";
        assert subtaskHashMap.get(subtask1.getId()) != null && subtask1.equals(subtaskHashMap.get(subtask1.getId())) :
                "Подзадача Subtask-1 не привязана к эпику или не соответствует созданной Subtask-1";
        assert subtaskHashMap.get(subtask2.getId()) != null && subtask2.equals(subtaskHashMap.get(subtask2.getId())) :
                "Подзадача Subtask-2 не привязана к эпику или не соответствует созданной Subtask-2";
        assert subtaskHashMap.get(subtask3.getId()) != null && subtask3.equals(subtaskHashMap.get(subtask3.getId())) :
                "Подзадача Subtask-3 не привязана к эпику или не соответствует созданной Subtask-3";


        assert epic.getStatus() == Status.NEW : "У эпика вычислился не правильный статус";
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        assert epic.getStatus() == Status.IN_PROGRESS : "У эпика вычислился не правильный статус";
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        assert epic.getStatus() == Status.IN_PROGRESS : "У эпика вычислился не правильный статус";
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        assert epic.getStatus() == Status.DONE : "У эпика вычислился не правильный статус";

        taskManager.removeSubtask(subtask3.getId());
        System.out.println("Удалили Subtask-3 из Epic-1: " + epic);
        assert epic.getSubtaskIds().size() == 2 : "Количество подзадач не совпадает после удаления";
        assert taskManager.getSubtask(subtask3.getId()) == null : "Subtask-3 не удалилась";
    }
}
