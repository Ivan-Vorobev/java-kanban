package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DeleteTaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        getTaskManager().removeTask(getId().get());
        setStatusCode(204);
        setResponseBody(gson.toJson(""));
    }
}
