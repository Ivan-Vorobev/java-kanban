package models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Подзадача")
class SubtaskTest {

    @Test
    @DisplayName("Проверяем равенство подзадач, если их id равны")
    void shouldBeEqualsById() {
        Subtask subtask = new Subtask("Title-1", "Description-1", Status.NEW);
        subtask.setId(10);
        Subtask expectedSubtask = new Subtask("Title-2", "Description-2", Status.IN_PROGRESS);
        expectedSubtask.setId(10);
        assertEquals(subtask, expectedSubtask, "Подзадачи не равны");
    }
}