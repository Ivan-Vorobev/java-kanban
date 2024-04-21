package services.history.memory;

import models.Epic;
import models.Subtask;
import models.Task;
import services.history.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> tasks;
    private final int HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        tasks = new ArrayList<>(HISTORY_SIZE);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (tasks.size() == HISTORY_SIZE) {
            tasks.remove(0);
        }

        if (task instanceof Epic) {
            tasks.add(clone( (Epic) task));
        } else if (task instanceof Subtask) {
            tasks.add(clone( (Subtask) task));
        } else {
            tasks.add(clone(task));
        }
    }

    private Epic clone(Epic epic) {
        Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getStatus());
        newEpic.setId(epic.getId());
        for (Integer sibtaskId : epic.getSubtaskIds()) {
            newEpic.getSubtaskIds().add(sibtaskId);
        }

        return newEpic;
    }

    private Task clone(Task task) {
        Task newTask = new Task(task.getTitle(), task.getDescription(), task.getStatus());
        newTask.setId(task.getId());

        return newTask;
    }
    private Subtask clone(Subtask subtask) {
        Subtask newSubtask = new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getStatus());
        newSubtask.setId(subtask.getId());
        newSubtask.setEpicId(subtask.getEpicId());

        return newSubtask;
    }

    @Override
    public List<Task> getHistory() {
        return tasks;
    }
}
