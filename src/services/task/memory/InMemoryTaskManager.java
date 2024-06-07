package services.task.memory;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import services.history.HistoryManager;
import services.task.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;
    protected int genId;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        genId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    private int generateId() {
        return ++genId;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        subtask.setEpicId(epic.getId());
        epic.getSubtaskIds().add(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.getSubtaskIds().clear();
        epics.put(epic.getId(), epic);
        epic.setStatus(calculateEpicStatus(epic));
        return epic;
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = tasks.get(task.getId());
        if (updatedTask != null) {
            updatedTask.setStatus(task.getStatus());
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
        }

        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = subtasks.get(subtask.getId());
        if (updatedSubtask != null) {
            updatedSubtask.setStatus(subtask.getStatus());
            updatedSubtask.setTitle(subtask.getTitle());
            updatedSubtask.setDescription(subtask.getDescription());

            Epic epic = epics.get(updatedSubtask.getEpicId());
            epic.setStatus(calculateEpicStatus(epic));
        }

        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = epics.get(epic.getId());
        if (updatedEpic != null) {
            updatedEpic.setTitle(epic.getTitle());
            updatedEpic.setDescription(epic.getDescription());
        }

        return updatedEpic;
    }

    @Override
    public void removeTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);

        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            subtaskIds.clear();
            historyManager.remove(epic.getId());
        }
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);

        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove(subtask.getId());
            epic.setStatus(calculateEpicStatus(epic));
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(calculateEpicStatus(epic));
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                historyManager.remove(subtaskId);
            }
            subtaskIds.clear();
        }

        subtasks.clear();
        epics.clear();
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(subtaskIds.size());
        for (Integer subtaskId : subtaskIds) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }

        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Status calculateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        Status status = Status.NEW;

        if (!subtaskIds.isEmpty()) {
            status = null;
            for (Integer subtaskId : subtaskIds) {
                Status subtaskStatus = subtasks.get(subtaskId).getStatus();
                if (status == null) {
                    status = subtaskStatus;
                } else if (status != subtaskStatus) {
                    status = Status.IN_PROGRESS;
                    break;
                }
            }

        }

        return status;
    }
}
