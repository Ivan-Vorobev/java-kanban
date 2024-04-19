package services;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Subtask createSubtask(Epic epic, Subtask subtask);

    Epic createEpic(Epic epic);

    Task getTask(int taskId);

    Subtask getSubtask(int subtaskId);

    Epic getEpic(int epicId);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    void removeTask(int taskId);

    void removeEpic(int epicId);

    void removeSubtask(int subtaskId);

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();
}
