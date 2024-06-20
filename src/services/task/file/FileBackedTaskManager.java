package services.task.file;

import models.Epic;
import models.Subtask;
import models.Task;
import services.history.HistoryManager;
import services.repository.file.TaskRepository;
import services.task.memory.InMemoryTaskManager;

import java.io.File;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager create(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);
        manager.loadFromFile(file);
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Subtask createSubtask(Epic epic, Subtask subtask) {
        Subtask newSubtask = super.createSubtask(epic, subtask);
        save();
        return newSubtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    private void loadFromFile(File file) {
        TaskRepository repository = new TaskRepository(file, true);
        List<Task> taskList = repository.findAllTasks();
        List<Subtask> subtaskList = repository.findAllSubtasks();
        List<Epic> epicList = repository.findAllEpics();

        tasks.clear();
        subtasks.clear();
        epics.clear();
        genId = 0;

        for (Task task : taskList) {
            tasks.put(task.getId(), task);
            correctGenId(task);
            addPrioritizedTask(task);
        }

        for (Subtask subtask : subtaskList) {
            subtasks.put(subtask.getId(), subtask);
            correctGenId(subtask);
            addPrioritizedTask(subtask);
        }

        for (Epic epic : epicList) {
            epics.put(epic.getId(), epic);
            correctGenId(epic);
            addPrioritizedTask(epic);
        }
    }

    private void correctGenId(Task task) {
        if (task.getId() > genId) {
            genId = task.getId();
        }
    }

    private void save() {
        TaskRepository repository = new TaskRepository(file, true);
        for (Task task : getAllTasks()) {
            repository.add(task);
        }

        for (Subtask subtask : getAllSubtasks()) {
            repository.add(subtask);
        }

        for (Epic epic : getAllEpics()) {
            repository.add(epic);
        }
    }
}
