package handlers.subtasks;

import com.sun.net.httpserver.HttpExchange;
import handlers.MainHandler;
import models.Subtask;

import java.io.IOException;

public class UpdateSubtaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Subtask subtask = readJSON(Subtask.class, exchange);
        subtask.setId(getId().get());
        Subtask updatedSubtask = getTaskManager().updateSubtask(subtask);
        setStatusCode(200);
        setResponseBody(gson.toJson(updatedSubtask));
    }
}
