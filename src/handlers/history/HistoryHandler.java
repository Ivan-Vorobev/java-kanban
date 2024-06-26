package handlers.history;

import com.sun.net.httpserver.HttpExchange;
import handlers.MainHandler;

import java.io.IOException;

public class HistoryHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setStatusCode(200);
        setResponseBody(
                gson.toJson(getTaskManager().getHistory())
        );
    }
}
