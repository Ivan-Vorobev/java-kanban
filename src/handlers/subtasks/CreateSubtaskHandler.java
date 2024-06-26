package handlers.subtasks;

import com.sun.net.httpserver.HttpExchange;
import exceptions.BadRequestException;
import handlers.MainHandler;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.util.Objects;

public class CreateSubtaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Subtask subtask = readJSON(Subtask.class, exchange);

        if (
                Objects.isNull(subtask.getTitle())
                        || Objects.isNull(subtask.getDescription())
                        || Objects.isNull(subtask.getEpicId())
                        || subtask.getTitle().isEmpty()
                        || subtask.getDescription().isEmpty()
        ) {
            throw new BadRequestException("Заголовок, описание и идентификатор эпика обязательны");
        }

        Epic epic = getTaskManager().getEpic(subtask.getEpicId());
        Task createdSubtask = getTaskManager().createSubtask(epic, subtask);
        setStatusCode(201);
        setResponseBody(gson.toJson(createdSubtask));
    }
}
