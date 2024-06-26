import adapters.DurationJSONFormatAdapter;
import adapters.InstantJSONFormatAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.NotFoundException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.Managers;
import services.task.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверка API")
class HttpTaskServerTest {
    private static Gson gson;
    private static TaskManager taskManager;
    private static Instant startTime;
    private static HttpTaskServer server;

    @BeforeEach
    protected void init() {
        taskManager = Managers.getDefault();
        startTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0).atZone(ZoneId.of("Europe/Moscow")).toInstant();

        generateTasks();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantJSONFormatAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationJSONFormatAdapter());
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gson = gsonBuilder.create();

        server = new HttpTaskServer(taskManager);

        try {
            server.run();
        } catch (IOException ignored) {
        }
    }

    @AfterEach
    protected void close() {
        server.stop();
    }

    private static void generateTasks() {
        createTask(1);
        createTask(2);
        createTask(3);

        Epic epic1 = createEpic(4);
        Epic epic2 = createEpic(5);

        createSubtask(6, epic1);
        createSubtask(7, epic1);
        createSubtask(8, epic1);

        createSubtask(9, epic2);
        createSubtask(10, epic2);
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

    private static HttpResponse<String> getHttpResponse(String method, String uri, String body) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8080" + uri));

        switch (method) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(HttpRequest.BodyPublishers.ofString(body));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new RuntimeException("Method not found");
        }

        HttpRequest request = builder.build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private void assertTask(Task task1, Task task2) {
        assertEquals(task1, task2, "Задачи не равны");
        assertEquals(task1.getTitle(), task2.getTitle(), "Заголовок не равен");
        assertEquals(task1.getDescription(), task2.getDescription(), "Описание не равно");
        assertEquals(task1.getStatus(), task2.getStatus(), "Статус не равен");
        assertEquals(task1.getStartTime(), task2.getStartTime(), "Время начать не равно");
        assertEquals(task1.getDuration(), task2.getDuration(), "Интервал не равен");
    }

    private void assertSubtask(Subtask subtask1, Subtask subtask2) {
        assertTask(subtask1, subtask2);
        assertEquals(subtask1.getEpicId(), subtask2.getEpicId(), "Эпик подзадачи не равен");
    }

    private void assertEpic(Epic epic1, Epic epic2) {
        assertTask(epic1, epic2);
        assertArrayEquals(epic1.getSubtaskIds().toArray(), epic2.getSubtaskIds().toArray(), "Подзадачи эпика не равны");
    }

    @Test
    @DisplayName("Проверяем что произвольный несуществующий url отдает 404 статус")
    void shouldReturn404_whenSendIncorrectURI() {
        HttpResponse<String> response = getHttpResponse("GET", "/7CC69970-C5F5-4D83-A92C-C24D0B749916", "");
        assertEquals(404, response.statusCode(), "Несуществующий URL отдает не 404 ответ");
    }

    @Test
    @DisplayName("Получаем список задач")
    void shouldReturnTaskList() {
        HttpResponse<String> response = getHttpResponse("GET", "/tasks", "");
        assertEquals(200, response.statusCode(), "Отсутствует список задач");

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        });

        tasks.stream().peek(task -> assertTask(task, taskManager.getTask(task.getId()))).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Создаем задачу")
    void shouldCreateTask() {
        HttpResponse<String> response = getHttpResponse("POST", "/tasks", "{}");
        assertEquals(400, response.statusCode(), "Создается непонятная задача");

        response = getHttpResponse("POST", "/tasks", "");
        assertEquals(500, response.statusCode(), "Создается непонятная задача");

        Task newTask = new Task("Task-0", "Task title-0", Status.NEW, getStartTime(), 30);
        response = getHttpResponse("POST", "/tasks", gson.toJson(newTask));

        Task responseTask = gson.fromJson(response.body(), Task.class);
        newTask.setId(responseTask.getId());
        assertEquals(201, response.statusCode(), "Задача не создана: " + response.body());
        assertTask(responseTask, taskManager.getTask(responseTask.getId()));
        assertTask(newTask, taskManager.getTask(responseTask.getId()));
    }

    @Test
    @DisplayName("Находим задачу по id")
    void shouldFindTask() {
        HttpResponse<String> response = getHttpResponse("GET", "/tasks/0", "");
        assertEquals(404, response.statusCode(), "Найдена непонятная задача");

        response = getHttpResponse("GET", "/tasks/1", "");
        Task responseTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Найдена непонятная задача");
        assertTask(responseTask, taskManager.getTask(responseTask.getId()));
    }

    @Test
    @DisplayName("Обновляем задачу по id")
    void shouldUpdateTask() {
        Task newTask = new Task("Task-0", "Task title-0", Status.NEW, getStartTime(), 30);
        String jsonData = gson.toJson(newTask);
        HttpResponse<String> response = getHttpResponse("POST", "/tasks/0", jsonData);
        assertEquals(404, response.statusCode(), "Найдена непонятная задача");

        response = getHttpResponse("POST", "/tasks/1", jsonData);
        Task responseTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Найдена непонятная задача");
        newTask.setId(responseTask.getId());
        assertTask(responseTask, taskManager.getTask(responseTask.getId()));
        assertTask(newTask, taskManager.getTask(responseTask.getId()));
    }

    @Test
    @DisplayName("Удаляем задачу")
    void shouldDeleteTask() {
        Task task = taskManager.getTask(1);
        HttpResponse<String> response = getHttpResponse("DELETE", "/tasks/1", "");

        assertEquals(204, response.statusCode(), "Найдена непонятная задача");
        assertThrowsExactly(NotFoundException.class, () -> taskManager.getTask(task.getId()), "Найдена удаленная задача");
    }

    @Test
    @DisplayName("Получаем список подзадач")
    void shouldReturnSubtaskList() {
        HttpResponse<String> response = getHttpResponse("GET", "/subtasks", "");
        assertEquals(200, response.statusCode(), "Отсутствует список подзадач");

        List<Subtask> tasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        });

        tasks.stream().peek(subtask -> assertSubtask(subtask, taskManager.getSubtask(subtask.getId()))).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Создаем подзадачу")
    void shouldCreateSubtask() {
        HttpResponse<String> response = getHttpResponse("POST", "/subtasks", "{}");
        assertEquals(400, response.statusCode(), "Создается непонятная подзадача");

        response = getHttpResponse("POST", "/subtasks", "");
        assertEquals(500, response.statusCode(), "Создается непонятная подзадача");

        Subtask newSubtask = new Subtask("Subtask-0", "Subtask title-0", Status.IN_PROGRESS, getStartTime(), 30);
        newSubtask.setEpicId(4);
        response = getHttpResponse("POST", "/subtasks", gson.toJson(newSubtask));

        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        newSubtask.setId(responseSubtask.getId());
        assertEquals(201, response.statusCode(), "Подзадача не создана: " + response.body());
        assertSubtask(responseSubtask, taskManager.getSubtask(responseSubtask.getId()));
        assertSubtask(newSubtask, taskManager.getSubtask(responseSubtask.getId()));
    }

    @Test
    @DisplayName("Находим подзадачу по id")
    void shouldFindSubtask() {
        HttpResponse<String> response = getHttpResponse("GET", "/subtasks/0", "");
        assertEquals(404, response.statusCode(), "Найдена непонятная подзадача");

        response = getHttpResponse("GET", "/subtasks/6", "");
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Найдена непонятная подзадача");
        assertSubtask(responseSubtask, taskManager.getSubtask(responseSubtask.getId()));
    }

    @Test
    @DisplayName("Обновляем подзадачу по id")
    void shouldUpdateSubtask() {
        Subtask newSubtask = new Subtask("Subtask-0", "Subtask title-0", Status.IN_PROGRESS, getStartTime(), 30);
        newSubtask.setEpicId(4);
        String jsonData = gson.toJson(newSubtask);
        HttpResponse<String> response = getHttpResponse("POST", "/subtasks/0", jsonData);
        assertEquals(404, response.statusCode(), "Найдена непонятная подзадача");

        response = getHttpResponse("POST", "/subtasks/6", jsonData);
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Найдена непонятная подзадача");
        newSubtask.setId(responseSubtask.getId());
        assertSubtask(responseSubtask, taskManager.getSubtask(responseSubtask.getId()));
        assertSubtask(newSubtask, taskManager.getSubtask(responseSubtask.getId()));
    }

    @Test
    @DisplayName("Удаляем подзадачу")
    void shouldDeleteSubtask() {
        Subtask subtask = taskManager.getSubtask(6);
        HttpResponse<String> response = getHttpResponse("DELETE", "/subtasks/6", "");

        assertEquals(204, response.statusCode(), "Найдена непонятная подзадача");
        assertThrowsExactly(NotFoundException.class, () -> taskManager.getSubtask(subtask.getId()), "Найдена удаленная подзадача");
    }

    @Test
    @DisplayName("Получаем список эпиков")
    void shouldReturnEpicList() {
        HttpResponse<String> response = getHttpResponse("GET", "/epics", "");
        assertEquals(200, response.statusCode(), "Отсутствует список эпиков");

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        });
        epics.stream().peek(epic -> assertEpic(epic, taskManager.getEpic(epic.getId()))).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Находим эпик по id")
    void shouldFindEpic() {
        HttpResponse<String> response = getHttpResponse("GET", "/epics/0", "");
        assertEquals(404, response.statusCode(), "Найден непонятный эпик");

        response = getHttpResponse("GET", "/epics/4", "");
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Найден непонятный эпик");
        assertEpic(responseEpic, taskManager.getEpic(responseEpic.getId()));
    }

    @Test
    @DisplayName("Находим подзадачи эпика по id эпика")
    void shouldFindEpicSubtasks() {
        HttpResponse<String> response = getHttpResponse("GET", "/epics/0/subtasks", "");
        assertEquals(404, response.statusCode(), "Найдены непонятные подзадачи");

        response = getHttpResponse("GET", "/epics/4/subtasks", "");
        List<Subtask> responseSubtasks = gson.fromJson(response.body(), new TypeToken<>() {
        });

        assertEquals(200, response.statusCode(), "Найдены непонятные подзадачи");
        assertEquals(3, responseSubtasks.size(), "Количество подзадач не верное");
        assertArrayEquals(responseSubtasks.toArray(), taskManager.getEpicSubtasks(taskManager.getEpic(4)).toArray(), "Подзадачи отличаются");
    }

    @Test
    @DisplayName("Создаем эпик")
    void shouldCreateEpic() {
        HttpResponse<String> response = getHttpResponse("POST", "/epics", "{}");
        assertEquals(400, response.statusCode(), "Создается непонятный эпик");

        response = getHttpResponse("POST", "/epics", "");
        assertEquals(500, response.statusCode(), "Создается непонятный эпик");

        Epic newEpic = new Epic("Epic-0", "Epic title-0", Status.NEW);
        response = getHttpResponse("POST", "/epics", gson.toJson(newEpic));

        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        newEpic.setId(responseEpic.getId());
        assertEquals(201, response.statusCode(), "Эпик не создан: " + response.body());
        assertEpic(responseEpic, taskManager.getEpic(responseEpic.getId()));
        assertEpic(newEpic, taskManager.getEpic(responseEpic.getId()));
    }

    @Test
    @DisplayName("Обновляем эпик по id")
    void shouldUpdateEpic() {
        Epic newEpic = new Epic("Epic-0", "Epic title-0", Status.NEW);
        String jsonData = gson.toJson(newEpic);
        HttpResponse<String> response = getHttpResponse("POST", "/epics/0", jsonData);
        assertEquals(404, response.statusCode(), "Найден непонятный эпик");

        response = getHttpResponse("POST", "/epics/4", jsonData);
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Найден непонятный эпик");
        newEpic.setId(responseEpic.getId());
        newEpic.setStatus(responseEpic.getStatus());
        newEpic.setStartTime(responseEpic.getStartTime());
        newEpic.setDuration(responseEpic.getDuration());
        newEpic.setEndTime(responseEpic.getEndTime());
        newEpic.getSubtaskIds().addAll(responseEpic.getSubtaskIds());
        assertEpic(responseEpic, taskManager.getEpic(responseEpic.getId()));
        assertEpic(newEpic, taskManager.getEpic(responseEpic.getId()));
    }

    @Test
    @DisplayName("Удаляем эпик")
    void shouldDeleteEpic() {
        Epic epic = taskManager.getEpic(4);
        HttpResponse<String> response = getHttpResponse("DELETE", "/epics/4", "");

        assertEquals(204, response.statusCode(), "Найден непонятный эпик");
        assertThrowsExactly(NotFoundException.class, () -> taskManager.getEpic(epic.getId()), "Найден удаленный эпик");
    }

    @Test
    @DisplayName("Проверяем историю")
    void shouldReturnHistoryList() {
        taskManager.getAllTasks().stream().map(t -> taskManager.getTask(t.getId())).collect(Collectors.toList());
        taskManager.getAllSubtasks().stream().map(t -> taskManager.getSubtask(t.getId())).collect(Collectors.toList());
        taskManager.getAllEpics().stream().map(t -> taskManager.getEpic(t.getId())).collect(Collectors.toList());
        HttpResponse<String> response = getHttpResponse("GET", "/history", "");
        assertEquals(200, response.statusCode(), "Отсутствует список с историей");

        List<Task> historyTasks = gson.fromJson(response.body(), new TypeToken<>() {
        });
        assertEquals(10, historyTasks.size(), "Просмотренное количество задач не соответствует ожидаемому");
    }

    @Test
    @DisplayName("Получаем список приоритизированных задач")
    void shouldReturnPrioritizedList() {
        HttpResponse<String> response = getHttpResponse("GET", "/prioritized", "");
        assertEquals(200, response.statusCode(), "Отсутствует список приоритетными задачами");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<>() {
        });
        assertEquals(8, prioritizedTasks.size(), "Количество задач не соответствует ожидаемому");
        assertEquals(prioritizedTasks.get(0).getId(), 1, "Первая задача не на месте");
        assertEquals(prioritizedTasks.get(7).getId(), 10, "Последняя задача не на месте");
    }
}