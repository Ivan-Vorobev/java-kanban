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
import services.task.memory.InMemoryTaskManagerTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    private File tmpFile;
    protected FileBackedTaskManager fileTaskManager;

    @Override
    @BeforeEach
    protected void init() {
        try {
            tmpFile = File.createTempFile("file_db_", "");
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка создания временного файла", exception);
        }

        fileTaskManager = new FileBackedTaskManager(tmpFile, new InMemoryHistoryManager());
        taskManager = fileTaskManager;
    }

    @AfterEach
    protected void close() {
        tmpFile.delete();
    }

    @Test
    @DisplayName("Проверяем что задачи/подзадачи/эпики пишутся в файл после создания")
    void save_saveTasksSubtasksEpicsToFile_afterCreate() {
        Task task1 = fileTaskManager.createTask(new Task("Title", "Description", Status.NEW));

        assertTrue(tmpFile.isFile(), "Файл отсутствует");

        long fileSize = tmpFile.length();

        assertTrue(fileSize > 0, "В файл ничего не записано. Пишем Subtask");

        Epic createdEpic = fileTaskManager.createEpic(new Epic("Epic title", "Epic description", Status.DONE));

        assertTrue(fileSize < tmpFile.length(), "В файл ничего не записано. Пишем Task");

        fileSize = tmpFile.length();

        Subtask subtask1 = fileTaskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask2 = fileTaskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));
        Subtask subtask3 = fileTaskManager.createSubtask(createdEpic, new Subtask("New Subtask title", "New Subtask description", Status.NEW));

        assertTrue(fileSize < tmpFile.length(), "В файл ничего не записано. Пишем Epic");
    }

    @Test
    @DisplayName("Проверяем что задачи/подзадачи/эпики восстанавливаются из файла после создания")
    void save_loadTasksSubtasksEpicsFromFile_afterRestore() {
        Task task1 = fileTaskManager.createTask(new Task("Task-1", "Description", Status.NEW));
        Task task2 = fileTaskManager.createTask(new Task("Task-2", "Description", Status.NEW));
        Epic createdEpic1 = fileTaskManager.createEpic(new Epic("Epic-1 title", "Epic description", Status.DONE));
        Epic createdEpic2 = fileTaskManager.createEpic(new Epic("Epic-2 title", "Epic description", Status.DONE));
        Subtask subtask1 = fileTaskManager.createSubtask(createdEpic1, new Subtask("New Subtask-1 title", "New Subtask description", Status.NEW));
        Subtask subtask2 = fileTaskManager.createSubtask(createdEpic1, new Subtask("New Subtask-2 title", "New Subtask description", Status.NEW));
        Subtask subtask3 = fileTaskManager.createSubtask(createdEpic1, new Subtask("New Subtask-3 title", "New Subtask description", Status.NEW));

        fileTaskManager.removeAllTasks();
        fileTaskManager.removeAllEpics();
        fileTaskManager.loadFromFile(tmpFile);

        Task task3 = fileTaskManager.createTask(new Task("Task-3", "Description", Status.NEW));

        assertTrue(task3.getId() > subtask3.getId(), "Не восстановлен genId");
        assertEquals(task1, fileTaskManager.getTask(task1.getId()), "Не восстановлен Task-1");
        assertEquals(task2, fileTaskManager.getTask(task2.getId()), "Не восстановлен Task-2");
        assertEquals(createdEpic1, fileTaskManager.getEpic(createdEpic1.getId()), "Не восстановлен Epic-1");
        assertEquals(createdEpic2, fileTaskManager.getEpic(createdEpic2.getId()), "Не восстановлен Epic-2");
        assertEquals(subtask1, fileTaskManager.getSubtask(subtask1.getId()), "Не восстановлен Subtask-1");
        assertEquals(subtask2, fileTaskManager.getSubtask(subtask2.getId()), "Не восстановлен Subtask-2");
        assertEquals(subtask3, fileTaskManager.getSubtask(subtask3.getId()), "Не восстановлен Subtask-3");
    }


    @Test
    @DisplayName("Проверяем что менеджер работает при восстановлении из пустого файла")
    void save_loadEmptyFile_afterRestore() {
        assertDoesNotThrow(() -> {
            fileTaskManager.loadFromFile(tmpFile);
        });
    }
}
