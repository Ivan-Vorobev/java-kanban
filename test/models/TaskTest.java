package models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Test
    @DisplayName("Проверяем расчет времени окончания задачи")
    void returnEndTime_taskWithStartTime() {
        Instant startTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
                .atZone(ZoneId.of("Europe/Moscow"))
                .toInstant();
        Task task = new Task("Title-1", "Description-1", Status.NEW, startTime, 10);
        assertDoesNotThrow(
                task::getEndTime,
                "Не удалось рассчитать время окончания задачи"
        );
        assertEquals(1704057000000L, task.getEndTime().toEpochMilli(), "Неправильно рассчитывается время окончания таски");
    }
}