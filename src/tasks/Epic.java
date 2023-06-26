package tasks;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, Status.NEW, description);
    }

    public Epic(int id, String title,  Status status, String description) {
        super(id, title, status, description);
    }

    public void addSubtaskId(int idNumber) {
        if (!(subtasksId.contains(idNumber))) {
            subtasksId.add(idNumber);
        }
    }

    public void removeSubtaskId(Integer idNumber) {
        if (subtasksId.contains(idNumber)) {
            subtasksId.remove(idNumber);
        }
    }

    public void clearSubtasksId() {
        subtasksId.clear();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
    @Override
    public String toString() {
        return getId() + "," + TasksTypes.EPIC + "," + getTitle() + "," + getStatus() + "," + getDescription();
    }

}
