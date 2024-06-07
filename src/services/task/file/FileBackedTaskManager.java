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

    public void loadFromFile(File file) {
        TaskRepository repository = new TaskRepository(file);
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
        }

        for (Subtask subtask : subtaskList) {
            subtasks.put(subtask.getId(), subtask);
            correctGenId(subtask);
        }

        for (Epic epic : epicList) {
            epics.put(epic.getId(), epic);
            correctGenId(epic);
        }
    }

    private void correctGenId(Task task) {
        if (task.getId() > genId) {
            genId = task.getId();
        }
    }

    private void save() {
        if (file.isFile()) {
            file.delete();
        }
        TaskRepository repository = new TaskRepository(file);
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
