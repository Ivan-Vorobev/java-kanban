package models;

import java.time.Instant;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description, Status status) {
        super(title, description, status, null, 0);
    }

    public Subtask(String title, String description, Status status, Instant startTime, Integer duration) {
        super(title, description, status, startTime, duration);
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration().toMinutes() +
                ", epicId=" + epicId +
                '}';
    }
}
