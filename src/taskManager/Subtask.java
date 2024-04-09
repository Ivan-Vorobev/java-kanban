package taskManager;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, Status status) {
        super(title, description, status);
    }

    public void setEpic(Epic epic) {
        if (this.epic == null) {
            this.epic = epic;
        }
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", epicId=" + epic.getId() +
                '}';
    }
}
