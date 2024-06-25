import com.sun.net.httpserver.HttpServer;
import handlers.*;
import models.Rote;
import services.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void run() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        MainHttpHandler mainHandler = new MainHttpHandler(taskManager);
        mainHandler
                .withRoute(new Rote("GET", "/tasks", new TaskListHandler()))
                .withRoute(new Rote("POST", "/tasks", new CreateTaskHandler()))
                .withRoute(new Rote("GET", "/tasks/{id}", new GetTaskHandler()))
                .withRoute(new Rote("POST", "/tasks/{id}", new UpdateTaskHandler()))
                .withRoute(new Rote("DELETE", "/tasks/{id}", new DeleteTaskHandler()))
                .withRoute(new Rote("GET", "/subtasks", new SubtaskListHandler()))
                .withRoute(new Rote("POST", "/subtasks", new CreateSubtaskHandler()))
                .withRoute(new Rote("GET", "/subtasks/{id}", new GetSubtaskHandler()))
                .withRoute(new Rote("POST", "/subtasks/{id}", new UpdateSubtaskHandler()))
                .withRoute(new Rote("DELETE", "/subtasks/{id}", new DeleteSubtaskHandler()))
                .withRoute(new Rote("GET", "/epics", new EpicListHandler()))
                .withRoute(new Rote("GET", "/epics/{id}", new GetEpicHandler()))
                .withRoute(new Rote("GET", "/epics/{id}/subtasks", new GetEpicSubtasksHandler()))
                .withRoute(new Rote("POST", "/epics", new CreateEpicHandler()))
                .withRoute(new Rote("POST", "/epics/{id}", new UpdateEpicHandler()))
                .withRoute(new Rote("DELETE", "/epics/{id}", new DeleteEpicHandler()))
                .withRoute(new Rote("GET", "/history", new HistoryHandler()))
                .withRoute(new Rote("GET", "/prioritized", new PrioritizedHandler()));
        httpServer.createContext("/", mainHandler);
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
