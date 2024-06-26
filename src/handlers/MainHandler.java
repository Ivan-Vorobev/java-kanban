package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class MainHandler implements HttpHandler {
    protected Gson gson;
    private Optional<Integer> id;
    private TaskManager taskManager;
    private int statusCode = 500;
    private String responseBody = "";

    public void setId(Optional<Integer> id) {
        this.id = id;
    }

    public Optional<Integer> getId() {
        return id;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected TaskManager getTaskManager() {
        return taskManager;
    }

    protected void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    protected void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    protected <T> T readJSON(Class<T> tClass, HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, tClass);
    }
}
