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

   public ArrayList<Task> getAllTasks() {
       ArrayList<Task> allTasks = new ArrayList<>();

       if (!isNotNull(tasks)) return null;
       for (Task task : tasks.values()) {
           allTasks.add(task);
       }
       return allTasks;
   }

   public ArrayList<Epic> getAllEpics() {
       ArrayList<Epic> allEpics = new ArrayList<>();

       if (!isNotNull(epics)) return null;
       for (Epic epic : epics.values()) {
           allEpics.add(epic);
       }
       return allEpics;
   }

   public ArrayList<Subtask> getAllSubtasks() {
       if (!isNotNull(subtasks)) return new ArrayList<>();

       ArrayList<Subtask> allSubtasks = new ArrayList<>();

       for (Subtask subtask : subtasks.values()) {
           allSubtasks.add(subtask);
       }
       return allSubtasks;
   }

   public ArrayList<Subtask> getAllEpicsSubtasks(Epic epic) {
       if (!isNotNull(epic) && !isNotNull(subtasks)) return new ArrayList<>();

       ArrayList<Subtask> epicsSubtasks = new ArrayList<>();

       for (Subtask subtask : subtasks.values()) {
           if (subtask.getEpicId() == epic.getId()) {
               epicsSubtasks.add(subtask);
           }
       }
       return epicsSubtasks;
   }

    public Task getTaskById(int taskId) {
        if (isNotNull(tasks) && tasks.containsKey(taskId)) {
            if (tasks.containsKey(taskId)) {
                return tasks.get(taskId);
            }
        }
        return null;
    }

    public Epic getEpicById(int taskId) {
        if (isNotNull(epics) && epics.containsKey(taskId)) {
            if (epics.containsKey(taskId)) {
                return epics.get(taskId);
            }
        }
        return null;
    }

    public Subtask getSubtaskById(int taskId) {
        if (isNotNull(subtasks) && subtasks.containsKey(taskId)) {
            if (subtasks.containsKey(taskId)) {
                return subtasks.get(taskId);
            }
        }
        return null;
    }

    public void removeTaskById(int taskId) {
        if (isNotNull(tasks) && tasks.containsKey(taskId)) {
            for (Task task : tasks.values()) {
                if (task.getId() == taskId) {
                    tasks.remove(task.getId());
                    return;
                }
            }
        }
    }

    public void removeEpicById(int taskId) {
        if (isNotNull(epics) && epics.containsKey(taskId)) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == taskId) {
                    epics.remove(epic.getId());
                    return;
                }
            }
        }
    }

    public void removeSubtaskById(int taskId) {
        if(isNotNull(subtasks) && subtasks.containsKey(taskId)) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getId() == taskId) {

                    Epic subtasksEpic = epics.get(subtask.getEpicId());

                    subtasksEpic.removeSubtaskId(taskId);
                    subtasks.remove(taskId);
                    updateEpic(subtasksEpic);
                    return;
                }
            }
        }
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        if (isNotNull(epics)) {
            for (Epic epic : epics.values()) {
                epic.clearSubtasksId();
                updateEpic(epic);
            }
        }
    }

    public boolean isNotNull(Object object) {
        return object != null;
    }
}

