import com.sun.net.httpserver.HttpServer;
import handlers.MainHttpHandler;
import handlers.epics.*;
import handlers.history.HistoryHandler;
import handlers.prioritized.PrioritizedHandler;
import handlers.subtasks.*;
import handlers.tasks.*;
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
                .withRouteGroup("/tasks",
                        new Rote("GET", "/", new TaskListHandler()),
                        new Rote("POST", "/", new CreateTaskHandler()),
                        new Rote("GET", "/{id}", new GetTaskHandler()),
                        new Rote("POST", "/{id}", new UpdateTaskHandler()),
                        new Rote("DELETE", "/{id}", new DeleteTaskHandler())
                )
                .withRouteGroup("/subtasks",
                        new Rote("GET", "/", new SubtaskListHandler()),
                        new Rote("POST", "/", new CreateSubtaskHandler()),
                        new Rote("GET", "/{id}", new GetSubtaskHandler()),
                        new Rote("POST", "/{id}", new UpdateSubtaskHandler()),
                        new Rote("DELETE", "/{id}", new DeleteSubtaskHandler())
                )
                .withRouteGroup("/epics",
                        new Rote("GET", "/", new EpicListHandler()),
                        new Rote("GET", "/{id}", new GetEpicHandler()),
                        new Rote("GET", "/{id}/subtasks", new GetEpicSubtasksHandler()),
                        new Rote("POST", "/", new CreateEpicHandler()),
                        new Rote("POST", "/{id}", new UpdateEpicHandler()),
                        new Rote("DELETE", "/{id}", new DeleteEpicHandler())
                )
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
