package managers.inMemoryManagers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import managers.utilityClasses.Managers;
import org.jetbrains.annotations.NotNull;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    private int nextId = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));
    public final HistoryManager historyManager = Managers.getHistory();

    @Override
    public int addTask(@NotNull Task task) {
        if (isNotTimeConflicting(task)) {
            task.setId(nextId);
            nextId++;
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            return task.getId();
        }
        return 0;
    }

    @Override
    public int addEpic(@NotNull Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(@NotNull Subtask subtask) {
        if (isNotTimeConflicting(subtask)) {
            subtask.setId(nextId);
            nextId++;
            subtasks.put(subtask.getId(), subtask);
            updateEpic(epics.get(subtask.getEpicId()));
            if(subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            return subtask.getId();
        }
        return 0;
    }

    @Override
    public void updateTask(@NotNull Task task) {
        if (tasks.containsKey(task.getId()) && isNotTimeConflicting(task)) {
            tasks.put(task.getId(), task);
        }
    }

    private boolean isNewEpic(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return true;
        }
        for (int id : epic.getSubtasksId()) {
            if (!subtasks.get(id).getStatus().equals(Status.NEW)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDoneEpic(Epic epic) {
        for (int id : epic.getSubtasksId()) {
            if (!subtasks.get(id).getStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateEpic(@NotNull Epic epic) {
        if (!subtasks.isEmpty()) {
            for (int id : subtasks.keySet()) {
                if (subtasks.get(id).getEpicId() == epic.getId()) {
                    epic.addSubtaskId(id);
                }
            }
        }
        if (isNewEpic(epic)) {
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
        } else if (isDoneEpic(epic)) {
            epic.setStatus(Status.DONE);
            epics.put(epic.getId(), epic);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
            epics.put(epic.getId(), epic);
        }
        setEpicStartTime(epic);
        setEpicDuration(epic);
        setEpicsEndTime(epic);
    }

    @Override
    public void updateSubtask(@NotNull Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && isNotTimeConflicting(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            updateEpic(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
        }
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int taskId) {
        if (epics.containsKey(taskId)) {
            historyManager.add(epics.get(taskId));
        }
        return epics.get(taskId);
    }

    @Override
    public Subtask getSubtaskById(int taskId) {
        if (subtasks.containsKey(taskId)) {
            historyManager.add(subtasks.get(taskId));
        }
        return subtasks.get(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(tasks.get(taskId));
            tasks.remove(taskId);
        }
    }

    @Override
    public void removeEpicById(int taskId) {
        if (epics.containsKey(taskId)) {
            historyManager.remove(taskId);
            epics.remove(taskId);
        }
    }

    @Override
    public void removeSubtaskById(int taskId) {
        if (subtasks.containsKey(taskId)) {
            historyManager.remove(taskId);
            int epicId = subtasks.get(taskId).getEpicId();
            subtasks.remove(taskId);
            epics.get(epicId).removeSubtaskId(taskId);
            prioritizedTasks.remove(subtasks.get(taskId));
            updateEpic(epics.get(epicId));
        }
    }

    @Override
    public void clearTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            updateEpic(epic);
        }
    }

    public void addBackedTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    public void addBackedEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpic(epic);
    }

    public void addBackedSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
    }

    public void setNextId(int newValue) {
        nextId = newValue;
    }

    private void setEpicStartTime(Epic epic) {
        List<Subtask> epicsSubtasks = new ArrayList<>();
        for (int id : epic.getSubtasksId()) {
            if (subtasks.get(id).getStartTime() != null) {
                epicsSubtasks.add(subtasks.get(id));
            }
        }
        if (epicsSubtasks.isEmpty()) {
            epic.setStartTime(null);
            return;
        }
        epicsSubtasks.sort(Comparator.comparing(Task::getStartTime));
        Subtask earliestSubtask = epicsSubtasks.get(0);
        epic.setStartTime(earliestSubtask.getStartTime());
    }

    private void setEpicDuration(Epic epic) {
        List<Subtask> epicsSubtasks = new ArrayList<>();
        for (int id : epic.getSubtasksId()) {
            epicsSubtasks.add(subtasks.get(id));
        }
        long epicDuration = 0;

        for (Subtask subtask : epicsSubtasks) {
            epicDuration += subtask.getDuration();
        }

        epic.setDuration(epicDuration);
    }

    private void setEpicsEndTime(Epic epic) {
        List<Subtask> epicsSubtasks = new ArrayList<>();
        for (int id : epic.getSubtasksId()) {
            if (subtasks.get(id).getStartTime() != null) {
                epicsSubtasks.add(subtasks.get(id));
            }
        }
        if (epicsSubtasks.isEmpty()) {
            epic.setEndTime(null);
            return;
        }
        epicsSubtasks.sort(Comparator.comparing(Task::getStartTime));
        Subtask latestSubtask = epicsSubtasks.get(epicsSubtasks.size() - 1);
        epic.setEndTime(latestSubtask.getEndTime());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isNotTimeConflicting(Task newTask) {
        if (newTask.getStartTime() == null) return true;
        for (Task task : prioritizedTasks) {
            if ((newTask.getStartTime().isBefore(task.getStartTime()) && newTask.getEndTime().isAfter(
                    task.getStartTime()))
                    || (newTask.getStartTime().isAfter(task.getStartTime()) && newTask.getStartTime().isBefore(
                    task.getEndTime()))) {
                return false;
            }
        }
        return true;
    }
}
