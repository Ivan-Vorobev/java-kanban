package handlers.epics;

import com.sun.net.httpserver.HttpExchange;
import handlers.MainHandler;

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
