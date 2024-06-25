package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class EpicListHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setStatusCode(200);
        setResponseBody(
                gson.toJson(getTaskManager().getAllEpics())
        );
    }
}
