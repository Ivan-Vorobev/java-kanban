package models;

import com.sun.net.httpserver.HttpExchange;
import handlers.MainHandler;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rote {

    private final String method;
    private final String pattern;
    private final MainHandler handler;

    public Rote(String method, String path, MainHandler handler) {

        this.method = method;
        this.handler = handler;

        String newPath = path;
        if (path.contains("{id}")) {
            newPath = path.replace("{id}", "(\\d+)");

        }

        pattern = "^" + newPath + "$";
    }

    private boolean isAllowMethod(String method) {
        return this.method.equals(method);
    }

    private boolean matchUri(URI uri) {
        return uri.getPath().matches(this.pattern);
    }

    public boolean matches(HttpExchange exchange) {
        return  isAllowMethod(exchange.getRequestMethod()) && matchUri(exchange.getRequestURI());
    }

    public MainHandler getHandler() {
        return handler;
    }

    public Optional<Integer> getId(HttpExchange exchange) {
        Pattern matchPattern = Pattern.compile(pattern);
        Matcher matches = matchPattern.matcher(exchange.getRequestURI().getPath());
        if (matches.find() && matches.groupCount() == 1) {
            Integer id = Integer.valueOf(matches.group(1));
            return Optional.of(id);
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Rote{" +
                "method='" + method + '\'' +
                ", pattern='" + pattern + '\'' +
                ", handler=" + handler +
                '}';
    }
}
