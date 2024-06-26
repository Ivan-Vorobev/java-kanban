package handlers;

import adapters.DurationJSONFormatAdapter;
import adapters.InstantJSONFormatAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import models.ErrorResponse;
import models.Rote;
import services.task.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainHttpHandler implements HttpHandler {
    protected Gson gson;
    private final List<Rote> rotes;
    private final TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public MainHttpHandler(TaskManager taskManager) {
        rotes = new ArrayList<>();
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantJSONFormatAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationJSONFormatAdapter());
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public MainHttpHandler withRoute(Rote rote) {
        rotes.add(rote);
        return this;
    }

    public MainHttpHandler withRouteGroup(String groupName, Rote... rote) {
        Arrays.stream(rote).peek(r -> {
            String path = r.getPath().equals("/") ? groupName : groupName + r.getPath();
            rotes.add(new Rote(r.getMethod(), path, r.getHandler()));
        }).collect(Collectors.toList());
        return this;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        MainHandler handler = new DefaultHandler();
        int responseCode = 500;
        String responseString = "";
        try {
            for (Rote rote : rotes) {
                if (rote.matches(exchange)) {
                    handler = rote.getHandler();
                    handler.setId(rote.getId(exchange));
                    handler.setTaskManager(taskManager);
                    break;
                }
            }
            handler.setGson(gson);
            handler.handle(exchange);
            responseCode = handler.getStatusCode();
            responseString = handler.getResponseBody();
        } catch (BadRequestException exception) {
            responseCode = 400;
            responseString = buildExceptionResponse(exception);
        } catch (NotFoundException exception) {
            responseCode = 404;
            responseString = buildExceptionResponse(exception);
        } catch (ValidationException exception) {
            responseCode = 406;
            responseString = buildExceptionResponse(exception);
        } catch (RuntimeException exception) {
            responseCode = 500;
            responseString = buildExceptionResponse(exception);
        } finally {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            byte[] body = responseString.getBytes(DEFAULT_CHARSET);
            int responseLength = responseCode == 204 ? -1 : body.length;
            exchange.sendResponseHeaders(responseCode, responseLength);

            if (body.length > 0) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            }

            exchange.close();
        }
    }

    private String buildExceptionResponse(Exception exception) {
        return gson.toJson(new ErrorResponse(exception));
    }
}
