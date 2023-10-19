package tasks;

import tasks.Enums.Status;
import tasks.Enums.TasksTypes;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, Status status, String description, int epicId) {
        super(title, status, description);
        this.epicId = epicId;
    }

    public Subtask(String title, Status status, String description, LocalDateTime startTime, long duration,
                   int epicId) {
        super(title, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, Status status, String description, LocalDateTime startTime, long duration,
                   int epicId) {
        super(id, title, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, Status status, String description, int epicId) {
        super(id, title, status, description);
        this.epicId = epicId;
    }

    @Override
    public TasksTypes getTaskType() {
        return TasksTypes.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getId() + "," + getTaskType() + "," + getTitle() + "," + getStatus() + "," + getDescription()
                + "," + startTimeToString() + "," + getDuration() + "," + getEpicId();
    }
}