package handlers.tasks;

import com.sun.net.httpserver.HttpExchange;
import handlers.MainHandler;

import java.io.IOException;

public class TaskListHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setStatusCode(200);
        setResponseBody(
                gson.toJson(getTaskManager().getAllTasks())
        );
    }
}
