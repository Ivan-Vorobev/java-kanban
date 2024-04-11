package models;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}
