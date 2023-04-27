package manager;
import org.jetbrains.annotations.NotNull;
import tasks.*;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addTask(Task task) {
        if(task != null) {
            task.setId(nextId);
            nextId++;
            tasks.put(task.getId(), task);
        }
    }

    public void updateTask(Task task, Task oldTask) {
        if (task != null && oldTask != null) {
            task.setId(oldTask.getId());
            tasks.put(task.getId(), task);
        }
    }
    public void addEpic(Epic epic) {
        if(epic != null) {
            epic.setId(nextId);
            nextId++;
            epics.put(epic.getId(), epic);
        }
    }

    public void addSubtask(Subtask subtask, Epic epic) {
        if(subtask != null && epic != null) {
            subtask.setId(nextId);
            nextId++;
            subtask.setEpicId(epic.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpic(epic);
        }
    }

    public void updateSubtask(Subtask subtask, Subtask oldSubtask) {
        if (subtask != null && oldSubtask != null) {
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
        if(epic != null) {
            for (int id: subtasks.keySet()) {
                if(subtasks.get(id).getEpicId() == epic.getId()) {
                    epic.addSubtaskId(id);
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
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }
    public void printAllEpics() {
        for (Epic epic: epics.values()) {
            System.out.println(epic);
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epic.getId()) {
                    System.out.println(subtask);
                }
            }
        }
    }
    public void getById(int taskId) {
        if(tasks.containsKey(taskId)) {
            System.out.println(tasks.get(taskId));
        } else if (epics.containsKey(taskId)) {
            System.out.println(epics.get(taskId));
        } else if (subtasks.containsKey(taskId)) {
            System.out.println(subtasks.get(taskId));
        }
    }
    public void removeById (int taskId) {
        if(tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            return;
        } else if (epics.containsKey(taskId)) {
            epics.remove(taskId);
            for (int id : subtasks.keySet()) {
                if(subtasks.get(id).getEpicId() == taskId) {
                    subtasks.remove(id);
                }
                return;
            }
        } else if (subtasks.containsKey(taskId)) {
            subtasks.remove(taskId);
        } else {
            System.out.println("Такой задачи нет");
        }
    }
    public void removeAll() {
        tasks = null;
        epics = null;
        subtasks = null;
    }
}

