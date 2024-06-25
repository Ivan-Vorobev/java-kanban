package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DeleteEpicHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        getTaskManager().removeEpic(getId().get());
        setStatusCode(204);
        setResponseBody(gson.toJson(""));
    }
}
