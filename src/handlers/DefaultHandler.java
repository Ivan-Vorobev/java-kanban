package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;

import java.io.IOException;

public class DefaultHandler extends MainHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        throw new NotFoundException(
                String.format(
                        "Incorrect endpoint %s %s",
                        exchange.getRequestMethod(),
                        exchange.getRequestURI().getPath()
                )
        );
    }
}
