package services;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int genId;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        genId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    private int generateId() {
        return ++genId;
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        subtask.setEpicId(epic.getId());
        epic.getSubtaskIds().add(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        return subtask;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.setStatus(calculateEpicStatus(epic));
        return epic;
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubtask(int subtaskId) {
        return subtasks.get(subtaskId);
    }
    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Task updateTask(Task task) {
        Task updatedTask = tasks.get(task.getId());
        if (updatedTask != null) {
            updatedTask.setStatus(task.getStatus());
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
        }

        return updatedTask;
    }

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

    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = epics.get(epic.getId());
        if (updatedEpic != null) {
            updatedEpic.setTitle(epic.getTitle());
            updatedEpic.setDescription(epic.getDescription());
        }

        return updatedEpic;
    }

    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);

        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);

        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove(subtask.getId());
            epic.setStatus(calculateEpicStatus(epic));
        }
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(calculateEpicStatus(epic));
        }
        subtasks.clear();
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>(subtaskIds.size());
        for (Integer subtaskId : subtaskIds) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }

        return epicSubtasks;
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
