package taskManager;

import java.util.Collection;
import java.util.HashMap;

public class TaskManager {

    private int genId;
    private final HashMap<Integer, Task> tasks;

    public TaskManager() {
        genId = 0;
        tasks = new HashMap<>();
    }

    public int generateId() {
        return ++genId;
    }

    public Task createTask(String title, String description, Status status) {
        Task task = new Task(title, description, status);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask createSubtask(Epic epic, String title, String description, Status status) {
        Subtask subtask = new Subtask(title, description, status);
        subtask.setId(generateId());
        epic.addSubtask(subtask);
        tasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public Epic createEpic(String title, String description, Status status) {
        Epic epic = new Epic(title, description, status);
        epic.setId(generateId());
        tasks.put(epic.getId(), epic);
        return epic;
    }

    public Task get(int taskId) {
        return tasks.get(taskId);
    }

    public HashMap<Integer, Task> getAll() {
        return tasks;
    }

    public Task update(Task task) {
        Task updatedTask = tasks.get(task.getId());
        if (updatedTask != null) {
            updatedTask.setStatus(task.getStatus());
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
        }

        return updatedTask;
    }

    public void remove(int taskId) {
        Task task = tasks.get(taskId);

        if (task == null) {
            return;
        }

        if (task instanceof Epic) {
            removeEpic((Epic) task);
        } else if (task instanceof Subtask) {
            removeSubtask((Subtask) task);
        }

        tasks.remove(task.getId());
    }

    private void removeEpic(Epic epic) {
        Collection<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            tasks.remove(subtask.getId());
        }
        subtasks.clear();
    }

    private void removeSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        epic.removeSubtask(subtask.getId());
    }
}
