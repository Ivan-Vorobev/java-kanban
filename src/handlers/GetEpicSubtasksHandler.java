package handlers;

import com.sun.net.httpserver.HttpExchange;
import models.Epic;

import java.io.IOException;

public class GetEpicSubtasksHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Epic epic = getTaskManager().getEpic(getId().get());
        setStatusCode(200);
        setResponseBody(gson.toJson(getTaskManager().getEpicSubtasks(epic)));
    }
}
