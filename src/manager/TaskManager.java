package manager;
import org.jetbrains.annotations.NotNull;
import tasks.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addTask(Task task) {
        if (isNotNull(task)) {
            task.setId(nextId);
            nextId++;
            tasks.put(task.getId(), task);
        }
    }

    public void updateTask(Task task, Task oldTask) {
        if (isNotNull(task) && isNotNull(oldTask)) {
            task.setId(oldTask.getId());
            tasks.put(task.getId(), task);
        }
    }

    public void addEpic(Epic epic) {
        if (isNotNull(epic)) {
            epic.setId(nextId);
            nextId++;
            epics.put(epic.getId(), epic);
        }
    }

    public void addSubtask(Subtask subtask, Epic epic) {
        if (isNotNull(subtask) && isNotNull(epic)) {
            subtask.setId(nextId);
            nextId++;
            subtask.setEpicId(epic.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpic(epic);
        }
    }

    public void updateSubtask(Subtask subtask, Subtask oldSubtask) {
        if (isNotNull(subtask) && isNotNull(oldSubtask)) {
            subtask.setId(oldSubtask.getId());
            subtask.setEpicId(oldSubtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            updateEpic(epics.get(subtask.getEpicId()));
        }
    }


    public boolean isNew(@NotNull Epic epic) {
        if (epic.getSubtasksId() == null) {
            return true;
        }
        for (int id : epic.getSubtasksId()) {
            if (!(subtasks.get(id).getStatus().equals("New"))) {
                return false;
            }
        }
        return true;
    }

    public boolean isDone(@NotNull Epic epic) {
        for (int id : epic.getSubtasksId()) {
            if (!(Objects.equals(subtasks.get(id).getStatus(), "Done"))) {
                return false;
            }
        }
        return true;
    }

    public void updateEpic(Epic epic) {
        if (isNotNull(epic)) {
            if (isNotNull(subtasks)) {
                for (int id : subtasks.keySet()) {
                    if (subtasks.get(id).getEpicId() == epic.getId()) {
                        epic.addSubtaskId(id);
                    }
                }
            }
            if (isNew(epic)) {
                epic.setStatus("New");
                epics.put(epic.getId(), epic);
            } else if (isDone(epic)) {
                epic.setStatus("Done");
                epics.put(epic.getId(), epic);
            } else {
                epic.setStatus("In Progress");
                epics.put(epic.getId(), epic);
            }

        }
    }

    public void printAllTasks() {
        if (isNotNull(tasks)) {
            for (Task task : tasks.values()) {
                System.out.println(task);
            }
        }
    }

    public void printAllEpics() {
        if (isNotNull(epics)) {
            for (Epic epic : epics.values()) {
                System.out.println(epic);
                if (isNotNull(subtasks)) {
                    for (Subtask subtask : subtasks.values()) {
                        if (subtask.getEpicId() == epic.getId()) {
                            System.out.println(subtask);
                        }
                    }
                }
            }
        }
    }

    public void getById(int taskId) {
        if(isNotNull(tasks)) {
            if (tasks.containsKey(taskId)) {
                System.out.println(tasks.get(taskId));
            }
        }
        if(isNotNull(epics)) {
            if (epics.containsKey(taskId)) {
                System.out.println(epics.get(taskId));
            }
        }
        if(isNotNull(subtasks)) {
            if (subtasks.containsKey(taskId)) {
                System.out.println(subtasks.get(taskId));
            }
        }
    }

    public void removeById(int taskId) {
        if(isNotNull(tasks)) {
            for (Task task : tasks.values()) {
                if(task.getId() == taskId) {
                    tasks.remove(task.getId());
                    return;
                }
            }
        }
        if(isNotNull(epics)) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == taskId) {
                    epics.remove(epic.getId());
                    if (isNotNull(subtasks)) {
                        ArrayList<Integer> subtasksToDelete = new ArrayList<>();

                        for (Subtask subtask : subtasks.values()) {
                            if (subtask.getEpicId() == taskId) {
                                subtasksToDelete.add(subtask.getId());
                            }
                        }
                        for (int subId : subtasksToDelete) {
                            subtasks.remove(subId);
                        }
                        return;
                    }
                }
            }
        }
        if(isNotNull(subtasks)) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getId() == taskId) {
                    subtasks.remove(subtask.getId());
                    return;
                }
            }
        }
    }

    public void removeAll() {
        tasks = null;
        epics = null;
        subtasks = null;
    }
    public boolean isNotNull(Object object) {
        return object != null;
    }
}

