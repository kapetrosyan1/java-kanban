package tasks;

import tasks.Enums.Status;
import tasks.Enums.TasksTypes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private final String title;
    private Status status;
    private String description;
    private LocalDateTime startTime;
    private long duration;

    public Task(String title, Status status, String description) {
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public Task(String title, Status status, String description, LocalDateTime startTime, long duration) {
        this.title = title;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, Status status, String description, LocalDateTime startTime, long duration) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, Status status, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public void setDescription(String description) {
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

    public LocalDateTime getEndTime() {
        if (startTime == null) return null;
        return startTime.plusMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public String startTimeToString() {
        if (startTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return startTime.format(formatter);
    }

    public TasksTypes getTaskType() {
        return TasksTypes.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && title.equals(task.title) && status == task.status &&
                description.equals(task.description) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, status, description);
    }

    public String toString() {
        return id + "," + getTaskType() + "," + title + "," + status + "," + description + "," + startTimeToString()
                + "," + duration;
    }
}