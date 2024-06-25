package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.BadRequestException;
import models.Task;

import java.io.IOException;
import java.util.Objects;

public class CreateTaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Task task = readJSON(Task.class, exchange);

        if (
            Objects.isNull(task.getTitle())
            || Objects.isNull(task.getDescription())
            || task.getTitle().isEmpty()
            || task.getDescription().isEmpty()
        ) {
            throw new BadRequestException("Заголовок и описание обязательны");
        }

        Task createdTask = getTaskManager().createTask(task);
        setStatusCode(201);
        setResponseBody(gson.toJson(createdTask));
    }
}
