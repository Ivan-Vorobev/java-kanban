package handlers;

import com.sun.net.httpserver.HttpExchange;
import models.Task;

import java.io.IOException;

public class UpdateTaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Task task = readJSON(Task.class, exchange);
        task.setId(getId().get());
        Task updatedTask = getTaskManager().updateTask(task);
        setStatusCode(200);
        setResponseBody(gson.toJson(updatedTask));
    }
}
