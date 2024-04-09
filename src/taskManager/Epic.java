package taskManager;

import java.util.Collection;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        subtask.setEpic(this);
    }

    public Collection<Subtask> getSubtasks() {
        HashMap newSubtasks = (HashMap) subtasks.clone();
        return newSubtasks.values();
    }

    void removeSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    void removeSubtasks() {
        subtasks.clear();
    }

    @Override
    public Status getStatus() {
        if (subtasks.isEmpty()) {
            return Status.NEW;
        }

        Status status = null;
        for (Subtask subtask : subtasks.values()) {
            if (status == null) {
                status = subtask.getStatus();
            } else if (status != subtask.getStatus()) {
                return Status.IN_PROGRESS;
            }
        }

        return status;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", subtasks=" + subtasks.values() +
                '}';
    }
}
