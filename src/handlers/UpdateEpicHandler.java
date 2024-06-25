package handlers;

import com.sun.net.httpserver.HttpExchange;
import models.Epic;

import java.io.IOException;

public class UpdateEpicHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Epic epic = readJSON(Epic.class, exchange);
        epic.setId(getId().get());
        Epic updatedEpic = getTaskManager().updateEpic(epic);
        setStatusCode(200);
        setResponseBody(gson.toJson(updatedEpic));
    }
}
