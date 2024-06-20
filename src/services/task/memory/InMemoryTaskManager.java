package services.task.memory;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import services.history.HistoryManager;
import services.task.TaskManager;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;
    protected int genId;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;
    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        genId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private int generateId() {
        return ++genId;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        checkTaskTime(task);
        addPrioritizedTask(task);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(generateId());
        checkTaskTime(subtask);
        addPrioritizedTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        subtask.setEpicId(epic.getId());
        epic.getSubtaskIds().add(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        changeEpicTime(epic);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.getSubtaskIds().clear();
        epic.setDuration(0);
        epic.setStartTime(null);
        epics.put(epic.getId(), epic);
        epic.setStatus(calculateEpicStatus(epic));
        return epic;
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);

        if (task == null) {
            throw new NotFoundException("Task not found, id = " + taskId);
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);

        if (subtask == null) {
            throw new NotFoundException("Subtask not found, id = " + subtaskId);
        }

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            throw new NotFoundException("Epic not found, id = " + epicId);
        }

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
        if (updatedTask == null) {
            throw new NotFoundException("Task not found, id = " + task.getId());
        }

        checkTaskTime(task);
        removePrioritizedTask(task);
        addPrioritizedTask(task);

        updatedTask.setStatus(task.getStatus());
        updatedTask.setTitle(task.getTitle());
        updatedTask.setDescription(task.getDescription());
        updatedTask.setDuration(task.getDuration());
        updatedTask.setStartTime(task.getStartTime());

        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = subtasks.get(subtask.getId());
        if (updatedSubtask == null) {
            throw new NotFoundException("Subtask not found, id = " + subtask.getId());
        }

        Epic epic = epics.get(updatedSubtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Epic not found, id = " + updatedSubtask.getEpicId());
        }

        checkTaskTime(subtask);
        removePrioritizedTask(subtask);
        addPrioritizedTask(subtask);

        updatedSubtask.setStatus(subtask.getStatus());
        updatedSubtask.setTitle(subtask.getTitle());
        updatedSubtask.setDescription(subtask.getDescription());
        updatedSubtask.setDuration(subtask.getDuration());
        updatedSubtask.setStartTime(subtask.getStartTime());

        epic.setStatus(calculateEpicStatus(epic));
        changeEpicTime(epic);

        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = epics.get(epic.getId());
        if (updatedEpic == null) {
            throw new NotFoundException("Epic not found, id = " + epic.getId());
        }

        updatedEpic.setTitle(epic.getTitle());
        updatedEpic.setDescription(epic.getDescription());

        return updatedEpic;
    }

    @Override
    public void removeTask(int taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            removePrioritizedTask(task);
        }
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);

        if (epic != null) {
            removePrioritizedTask(epic);
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                removePrioritizedTask(subtasks.get(subtaskId));
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
            removePrioritizedTask(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove(subtask.getId());
            epic.setStatus(calculateEpicStatus(epic));
            historyManager.remove(subtask.getId());
            changeEpicTime(epics.get(subtask.getEpicId()));
        }

    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            removePrioritizedTask(tasks.get(taskId));
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(calculateEpicStatus(epic));
            changeEpicTime(epic);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
            removePrioritizedTask(subtasks.get(subtaskId));
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            removePrioritizedTask(epic);
            historyManager.remove(epic.getId());
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                historyManager.remove(subtaskId);
                removePrioritizedTask(subtasks.get(subtaskId));
            }
            subtaskIds.clear();
        }

        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    protected void addPrioritizedTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
    }

    private void checkTaskTime(Task task) {
        if (task.getStartTime() != null) {
            for (Task prioritizedTask : getPrioritizedTasks()) {
                if (task.getEndTime().isBefore(prioritizedTask.getStartTime())) {
                    break;
                }

                if (!Objects.equals(prioritizedTask.getId(), task.getId()) && isIntersectStartTime(prioritizedTask, task)) {
                    throw new ValidationException("Пересечение с задачей id = " + prioritizedTask.getId());
                }
            }
        }
    }

    private boolean isIntersectStartTime(Task task1, Task task2) {
        if (!task1.getStartTime().isAfter(task2.getStartTime())) {
            return !task1.getEndTime().isBefore(task2.getStartTime());
        }

        if (!task2.getStartTime().isAfter(task1.getStartTime())) {
            return !task2.getEndTime().isBefore(task1.getStartTime());
        }

        return false;
    }

    private void changeEpicTime(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Epic not found");
        }

        List<Integer> subtasks = epic.getSubtaskIds();
        epic.setDuration(0);
        epic.setStartTime(null);
        epic.setEndTime(null);

        if (!subtasks.isEmpty()) {
            subtasks.stream()
                    .peek(subtaskId -> {
                        Subtask subtask = this.subtasks.get(subtaskId);
                        boolean epicStartTimeIsNull = epic.getStartTime() == null;

                        if (subtask.getStartTime() != null) {
                            if (epicStartTimeIsNull || epic.getStartTime().isAfter(subtask.getStartTime())) {
                                epic.setStartTime(subtask.getStartTime());
                            }

                            if (epicStartTimeIsNull || epic.getEndTime().isBefore(subtask.getEndTime())) {
                                epic.setEndTime(subtask.getEndTime());
                            }
                        }

                        epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
                    }).collect(Collectors.toList());
        } else {
            epic.setDuration(0);
            epic.setStartTime(null);
        }
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
