package tasks;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
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
        return "{Epic - " + getTitle()
                + ", Task - " + getDescription()
                + ", Task Status - " + getStatus() + "};";
    }

}
