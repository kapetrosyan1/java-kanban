package tasks;

import static tasks.TasksTypes.*;

public class Task {
    private int id;
    private final String title;
    private Status status;
    private final String description;

    public Task(String title, Status status, String description) {
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public Task(int id, String title, Status status, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," + TasksTypes.TASK + "," + title + "," + status + "," + description;
    }

}
