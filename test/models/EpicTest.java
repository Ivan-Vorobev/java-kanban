package models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эпик")
class EpicTest {

    @Test
    @DisplayName("Проверяем равенство эпиков, если их id равны")
    void equals_returnTrue_idIsSameOtherFieldsNo() {
        Epic epic = new Epic("Title-1", "Description-1", Status.NEW);
        epic.setId(10);
        Epic expectedEpic = new Epic("Title-2", "Description-2", Status.IN_PROGRESS);
        expectedEpic.setId(10);
        assertEquals(epic, expectedEpic, "Эпики не равны");
    }
}