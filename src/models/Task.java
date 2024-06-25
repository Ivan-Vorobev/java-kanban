package models;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task {
    private Integer id;
    private String title;
    private String description;
    private Status status;
    private Duration duration;
    private Instant startTime;

    public Duration getDuration() {
        if (duration == null) {
            return Duration.ofMinutes(0);
        }
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Task(String title, String description, Status status) {
        this(title, description, status, null, 0);
    }

    public Task(String title, String description, Status status, Instant startTime, Integer duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        setDuration(duration);
    }

    public Task(Integer id, String title, String description, Status status, Instant startTime, Integer duration) {
        this(title, description, status, startTime, duration);
        setId(id);
    }

    public Task(Integer id, String title, String description, Status status) {
        this(title, description, status);
        setId(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getEndTime() {
        if (startTime == null) {
            throw new RuntimeException("Не указано время начала");
        }

        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration().toMinutes() +
                '}';
    }
}
