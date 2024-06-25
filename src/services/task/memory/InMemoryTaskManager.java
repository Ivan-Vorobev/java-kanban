package services.task.memory;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import services.history.HistoryManager;
import services.task.TaskManager;

import java.time.Duration;
import java.time.Instant;
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
        prepareEpic(epic);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.getSubtaskIds().clear();
        epics.put(epic.getId(), epic);
        prepareEpic(epic);
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

        prepareEpic(epic);

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

        if (epic == null) {
            return;
        }

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

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);

        if (subtask == null) {
            return;
        }

        removePrioritizedTask(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(subtask.getId());
        prepareEpic(epic);
        historyManager.remove(subtask.getId());
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
            prepareEpic(epic);
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

    @Override
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
        if (task.getStartTime() == null) {
            return;
        }

        for (Task prioritizedTask : getPrioritizedTasks()) {
            if (isOutOfBoundPrioritized(task, prioritizedTask)) {
                break;
            }

            if (hasIntersection(task, prioritizedTask)) {
                throw new ValidationException("[" + prioritizedTask.getClass() + "] Пересечение с задачей id = " + prioritizedTask.getId());
            }
        }
    }

    private boolean isOutOfBoundPrioritized(Task task, Task prioritizedTask) {
        return task.getEndTime().isBefore(prioritizedTask.getStartTime());
    }

    private boolean hasIntersection(Task task, Task prioritizedTask) {
        if (Objects.equals(task.getId(), prioritizedTask.getId())) {
            return false;
        }

        return isIntersectStartTime(task, prioritizedTask);
    }

    private boolean isIntersectStartTime(Task task1, Task task2) {
        Long task1EndTime = task1.getEndTime().toEpochMilli();
        Long task2EndTime = task2.getEndTime().toEpochMilli();

        Duration intersectDuration = task1EndTime < task2EndTime ? task2.getDuration() : task1.getDuration();

        return Math.abs(task1EndTime - task2EndTime) <= intersectDuration.toMillis();
    }

    private void prepareEpic(Epic epic) {
        epic.setStatus(calculateEpicStatus(epic));
        changeEpicTime(epic);
    }

    private void changeEpicTime(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Epic not found");
        }

        List<Integer> subtasks = epic.getSubtaskIds();
        epic.setDuration(getEpicDuration(subtasks));
        epic.setStartTime(getEpicStartTIme(subtasks));
        epic.setEndTime(getEpicEndTIme(subtasks));
    }

    private Instant getEpicStartTIme(List<Integer> subtaskIds) {
        return subtaskIds.isEmpty() ? null : subtaskIds.stream()
                .map(subtasks::get)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Instant::compareTo)
                .orElse(null);
    }

    private Instant getEpicEndTIme(List<Integer> subtaskIds) {
        return subtaskIds.isEmpty() ? null : subtaskIds.stream()
                .map(subtasks::get)
                .map(subtask -> subtask.getStartTime() != null ? subtask.getEndTime() : null)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private Duration getEpicDuration(List<Integer> subtaskIds) {
        return Duration.ofSeconds(subtaskIds.isEmpty() ? 0 : subtaskIds.stream()
                .map(subtasks::get)
                .map(Subtask::getDuration)
                .mapToLong(Duration::toSeconds)
                .sum());
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
