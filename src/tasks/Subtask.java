package tasks;

public class Subtask extends Task {
    private static int epicId;

    public Subtask(String title, Status status, String description) {
        super(title, status, description);
    }

    public Subtask(int id, String title, Status status, String description, int epicId) {
        super(id, title, status, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return getId() + "," + TasksTypes.SUBTASK + "," + getTitle() + "," + getStatus() + "," + getDescription() + ","
                + getEpicId();
    }
}
