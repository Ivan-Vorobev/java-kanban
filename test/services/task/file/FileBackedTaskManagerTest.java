package services.task.file;


import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.history.memory.InMemoryHistoryManager;
import services.task.TaskManagerTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tmpFile;

    @BeforeEach
    protected void init() {
        try {
            tmpFile = File.createTempFile("file_db_", "");
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка создания временного файла", exception);
        }

        taskManager = FileBackedTaskManager.create(tmpFile, new InMemoryHistoryManager());
    }

    @AfterEach
    protected void close() {
        tmpFile.delete();
    }

    @Test
    @DisplayName("Проверяем что задачи/подзадачи/эпики пишутся в файл после создания")
    void save_saveTasksSubtasksEpicsToFile_afterCreate() {
        taskManager.createTask(new Task("Title", "Description", Status.NEW));

        assertTrue(tmpFile.isFile(), "Файл отсутствует");

        long fileSize = tmpFile.length();

        assertTrue(fileSize > 0, "В файл ничего не записано. Пишем Subtask");

        Epic createdEpic = taskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));

        assertTrue(fileSize < tmpFile.length(), "В файл ничего не записано. Пишем Task");

        fileSize = tmpFile.length();

        taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        taskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));

        assertTrue(fileSize < tmpFile.length(), "В файл ничего не записано. Пишем Epic");
    }

    @Test
    @DisplayName("Проверяем что задачи/подзадачи/эпики восстанавливаются из файла после создания")
    void save_loadTasksSubtasksEpicsFromFile_afterRestore() {
        Task task1 = taskManager.createTask(new Task("Task-1", "Description", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Task-2", "Description", Status.NEW));
        Epic createdEpic1 = taskManager.createEpic(new Epic("Epic-1 title", "Epic description", Status.DONE));
        Epic createdEpic2 = taskManager.createEpic(new Epic("Epic-2 title", "Epic description", Status.DONE));
        Subtask subtask1 = taskManager.createSubtask(createdEpic1, new Subtask("New Subtask-1 title", "New Subtask description", Status.NEW));
        Subtask subtask2 = taskManager.createSubtask(createdEpic1, new Subtask("New Subtask-2 title", "New Subtask description", Status.NEW));
        Subtask subtask3 = taskManager.createSubtask(createdEpic1, new Subtask("New Subtask-3 title", "New Subtask description", Status.NEW));

        FileBackedTaskManager newTaskManager = FileBackedTaskManager.create(tmpFile, new InMemoryHistoryManager());

        Task task3 = newTaskManager.createTask(new Task("Task-3", "Description", Status.NEW));

        assertTrue(task3.getId() > subtask3.getId(), "Не восстановлен genId");
        assertEquals(task1, newTaskManager.getTask(task1.getId()), "Не восстановлен Task-1");
        assertEquals(task2, newTaskManager.getTask(task2.getId()), "Не восстановлен Task-2");
        assertEquals(createdEpic1, newTaskManager.getEpic(createdEpic1.getId()), "Не восстановлен Epic-1");
        assertEquals(createdEpic2, newTaskManager.getEpic(createdEpic2.getId()), "Не восстановлен Epic-2");
        assertEquals(subtask1, newTaskManager.getSubtask(subtask1.getId()), "Не восстановлен Subtask-1");
        assertEquals(subtask2, newTaskManager.getSubtask(subtask2.getId()), "Не восстановлен Subtask-2");
        assertEquals(subtask3, newTaskManager.getSubtask(subtask3.getId()), "Не восстановлен Subtask-3");
    }


    @Test
    @DisplayName("Проверяем что менеджер работает при восстановлении из пустого файла")
    void save_loadEmptyFile_afterRestore() {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager.create(tmpFile, new InMemoryHistoryManager());
        });
    }
}
