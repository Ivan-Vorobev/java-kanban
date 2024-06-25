package handlers;

import com.sun.net.httpserver.HttpExchange;
import models.Subtask;

import java.io.IOException;

public class GetSubtaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Subtask subtask = getTaskManager().getSubtask(getId().get());
        setStatusCode(200);
        setResponseBody(gson.toJson(subtask));
    }
}
