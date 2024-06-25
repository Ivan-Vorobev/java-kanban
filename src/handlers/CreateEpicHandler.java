package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.BadRequestException;
import models.Epic;

import java.io.IOException;
import java.util.Objects;

public class CreateEpicHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Epic epic = readJSON(Epic.class, exchange);

        if (
                Objects.isNull(epic.getTitle())
                        || Objects.isNull(epic.getDescription())
                        || epic.getTitle().isEmpty()
                        || epic.getDescription().isEmpty()
        ) {
            throw new BadRequestException("Заголовок и описание обязательны");
        }

        Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getStatus());
        Epic createdEpic = getTaskManager().createEpic(newEpic);
        setStatusCode(201);
        setResponseBody(gson.toJson(createdEpic));
    }
}
