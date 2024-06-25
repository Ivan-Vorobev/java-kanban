package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DeleteSubtaskHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        getTaskManager().removeSubtask(getId().get());
        setStatusCode(204);
        setResponseBody(gson.toJson(""));
    }
}
