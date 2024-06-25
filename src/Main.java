import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import services.Managers;
import services.task.TaskManager;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Random;

public class Main {
    private static TaskManager taskManager;
    private static Instant startTime;
    private static final int COUNT_OF_TASKS = 25;
    private static final int COUNT_OF_SUBTASKS = 50;
    private static final int COUNT_OF_EPICS = 10;

    public static void main(String[] args) throws IOException {
        taskManager = Managers.getDefault();
        startTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
                .atZone(ZoneId.of("Europe/Moscow"))
                .toInstant();

        generateTasks();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.run();
    }

    private static void generateTasks() {
        OptionalInt maxIter = Arrays.stream(new int[]{COUNT_OF_TASKS, COUNT_OF_SUBTASKS, COUNT_OF_EPICS}).max();
        Random random = new Random();
        int subtaskCount = COUNT_OF_SUBTASKS;

        for (var i = 1; i <= maxIter.getAsInt(); i++) {
            if (i <= COUNT_OF_TASKS) {
                createTask(i);
            }

            Epic epic = null;
            if (i <= COUNT_OF_EPICS) {
                epic = createEpic(i);
            }

            if (epic != null) {
                if (subtaskCount == 0) {
                    continue;
                }

                int subtaskSize = random.nextInt(1, 6);
                if (i == COUNT_OF_EPICS || subtaskCount < subtaskSize) {
                    subtaskSize = subtaskCount;
                }


                for (int j = 1; j <= subtaskSize; j++) {
                    createSubtask(i * maxIter.getAsInt() + j, epic);
                    subtaskCount--;
                }
            }
        }
    }

    private static Instant getStartTime() {
        startTime = startTime.plusSeconds(31 * 60);
        return startTime;
    }

    private static void createTask(int index) {
        taskManager.createTask(new Task("Task-" + index, "Task title-" + index, Status.NEW, getStartTime(), 30));
    }

    private static Epic createEpic(int index) {
        return taskManager.createEpic(new Epic("Epic-" + index, "Epic title-" + index, Status.NEW));
    }

    private static void createSubtask(int index, Epic epic) {
        taskManager.createSubtask(epic, new Subtask("Subtask-" + index, "Subtask title-" + index, Status.IN_PROGRESS, getStartTime(), 30));
    }
}
