import taskManager.*;

import java.util.Collection;
import java.util.HashMap;

public class Main {

    private static TaskManager taskManager;

    public static void main(String[] args) {
        System.out.println("Поехали!");

        /*
         * Воспользовался областью видимости default, поэтому модели и менеджер в куче.
         * Требовалось не дать возможность удалять подзадачи из эпиков в обход хранения данных менеджера,
         * потому что ответственность эпика знать про свои подзадачи
         * Если сделать понятную архитектуру как и показывали на QA вебинаре model/service, то тогда пришлось бы писать
         * менеджер событий, для отлова события удаления подзадач из эпика, чтобы это тригернуло удаление в менеджере
         **/

        taskManager = new TaskManager();
        assertCreateTask();
        assertCreateSubtask();
        assertCreateEpic();
        assertEpicSubtasks();
    }

    private static void assertCreateTask() {
        Task task = taskManager.createTask("Task-1", "Сделать таску 1", Status.NEW);
        System.out.println("Создаем Task: " + task);
        Task newTask = createNewTask(task.getId());
        taskManager.update(newTask);
        System.out.println("Изменяем параметры на: " + newTask);
        System.out.println("Task изменился: " + task);
        System.out.println();
        assert task.getTitle().equals(newTask.getTitle()) : "Поле title не изменилось";
        assert task.getDescription().equals(newTask.getDescription()) : "Поле description не изменилось";
        assert task.getStatus().equals(newTask.getStatus()) : "Поле status не изменилось";
    }

    private static void assertCreateSubtask() {
        Subtask task = taskManager.createSubtask(
                taskManager.createEpic("Epic-1", "Epic description", Status.NEW),
                "Subtask-1",
                "Сделать подзадачу 1",
                Status.NEW
        );
        Task newTask = createNewTask(task.getId());
        taskManager.update(newTask);
        System.out.println("Изменяем параметры на: " + newTask);
        System.out.println("Subtask изменился: " + task);
        System.out.println();
        assert task.getTitle().equals(newTask.getTitle()) : "Поле title не изменилось";
        assert task.getDescription().equals(newTask.getDescription()) : "Поле description не изменилось";
        assert task.getStatus().equals(newTask.getStatus()) : "Поле status не изменилось";
    }

    private static void assertCreateEpic() {
        Epic task = taskManager.createEpic("Epic-1", "Сделать эпик 1", Status.IN_PROGRESS);
        System.out.println("Создаем Epic: " + task);
        Task newTask = createNewTask(task.getId());
        taskManager.update(newTask);
        System.out.println("Изменяем параметры на: " + newTask);
        System.out.println("Epic изменился: " + task);
        System.out.println();
        assert task.getTitle().equals(newTask.getTitle()) : "Поле title не изменилось";
        assert task.getDescription().equals(newTask.getDescription()) : "Поле description не изменилось";
        assert task.getStatus().equals(Status.NEW) : "Поле status не правильно вычислился";
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

    private static void assertEpicSubtasks() {
        Epic epic = taskManager.createEpic("Epic-1", "Epic description", Status.NEW);
        Subtask subtask1 = taskManager.createSubtask(epic, "Subtask-1", "Sabtask-1 description", Status.NEW);
        Subtask subtask2 = taskManager.createSubtask(epic, "Subtask-2", "Sabtask-2 description", Status.NEW);
        Subtask subtask3 = taskManager.createSubtask(epic, "Subtask-3", "Sabtask-3 description", Status.NEW);

        Collection<Subtask> subtasks = epic.getSubtasks();
        HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>(3);
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
        assert epic.getStatus() == Status.IN_PROGRESS : "У эпика вычислился не правильный статус";
        subtask1.setStatus(Status.DONE);
        assert epic.getStatus() == Status.IN_PROGRESS : "У эпика вычислился не правильный статус";
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        assert epic.getStatus() == Status.DONE : "У эпика вычислился не правильный статус";

        taskManager.remove(subtask3.getId());
        System.out.println("Удалили Subtask-3 из Epic-1: " + epic);
        assert epic.getSubtasks().size() == 2 : "Количество подзадач не совпадает после удаления";
        assert taskManager.get(subtask3.getId()) == null : "Subtask-3 не удалилась";
    }
}
