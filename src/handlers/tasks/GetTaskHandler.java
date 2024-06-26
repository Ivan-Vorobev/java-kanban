package handlers.tasks;

import com.sun.net.httpserver.HttpExchange;
import handlers.MainHandler;
import models.Task;

import java.io.IOException;

public class GetTaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Task task = getTaskManager().getTask(getId().get());
        setStatusCode(200);
        setResponseBody(gson.toJson(task));
    }
}
