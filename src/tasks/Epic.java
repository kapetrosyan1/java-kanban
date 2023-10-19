package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

import tasks.Enums.Status;
import tasks.Enums.TasksTypes;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, Status.NEW, description);
    }

    public Epic(int id, String title, Status status, String description) {
        super(title, status, description);
        this.setId(id);
    }

    public Epic(int id, String title, String description) {
        super(title, Status.NEW, description);
        this.setId(id);
    }

    public void addSubtaskId(int idNumber) {
        if (!(subtasksId.contains(idNumber))) {
            subtasksId.add(idNumber);
        }
    }

    public void removeSubtaskId(Integer idNumber) {
        subtasksId.remove(idNumber);
    }

    public void clearSubtasksId() {
        subtasksId.clear();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TasksTypes getTaskType() {
        return TasksTypes.EPIC;
    }

    @Override
    public String toString() {
        return getId() + "," + getTaskType() + "," + getTitle() + "," + getStatus() + "," + getDescription();
    }
}