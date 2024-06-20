package models;

import java.time.Instant;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private Instant endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status, null, 0);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, Status status, Instant startTime, Integer duration) {
        super(title, description, status, startTime, duration);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", duration=" + getDuration().toMinutes() +
                ", subtasks=" + subtaskIds +
                '}';
    }

}
