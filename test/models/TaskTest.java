package models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Задача")
class TaskTest {

    @Test
    @DisplayName("Проверяем равенство задач, если их id равны")
    void equals_returnTrue_idIsSameOtherFieldsNo() {
        Task task = new Task("Title-1", "Description-1", Status.NEW);
        task.setId(10);
        Task expectedTask = new Task("Title-2", "Description-2", Status.IN_PROGRESS);
        expectedTask.setId(10);
        assertEquals(task, expectedTask, "Задачи не равны");
    }
}