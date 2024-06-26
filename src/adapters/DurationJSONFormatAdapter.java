package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationJSONFormatAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter writer, Duration value) throws IOException {
        if (value == null) {
            value = Duration.ofMinutes(0);
        }
        writer.jsonValue(String.valueOf(value.getSeconds() / 60));
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        try {
            return Duration.ofMinutes(Long.parseLong(reader.nextString()));
        } catch (Exception exception) {
            return Duration.ofMinutes(0);
        }
    }
}