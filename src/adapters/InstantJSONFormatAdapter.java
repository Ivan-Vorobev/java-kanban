package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantJSONFormatAdapter extends TypeAdapter<Instant> {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    @Override
    public void write(JsonWriter writer, Instant value) throws IOException {
        if (value == null) {
            writer.jsonValue("null");
        } else {
            writer.jsonValue(String.format("\"%s\"", timeFormatter.format(value)));
        }
    }

    @Override
    public Instant read(JsonReader reader) throws IOException {
        try {
            return Instant.from(timeFormatter.parse(reader.nextString()));
        } catch (Exception exception) {
            return null;
        }
    }
}