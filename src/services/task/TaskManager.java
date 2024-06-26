package services.task;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    Task createTask(Task task); // done

    Subtask createSubtask(Epic epic, Subtask subtask); // done

    Epic createEpic(Epic epic); // done

    Task getTask(int taskId); // done

    Subtask getSubtask(int subtaskId); // done

    Epic getEpic(int epicId); // done

    List<Task> getAllTasks(); // done

    List<Subtask> getAllSubtasks(); // done

    List<Epic> getAllEpics(); // done

    Task updateTask(Task task); // done

    Subtask updateSubtask(Subtask subtask); // done

    Epic updateEpic(Epic epic); // done

    void removeTask(int taskId); // done

    void removeEpic(int epicId); // done

    void removeSubtask(int subtaskId); // done

    void removeAllTasks(); // done

    void removeAllSubtasks(); // done

    void removeAllEpics(); // done

    List<Subtask> getEpicSubtasks(Epic epic); // done

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
