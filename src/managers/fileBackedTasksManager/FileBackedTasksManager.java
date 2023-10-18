package managers.fileBackedTasksManager;

import exceptions.ManagerSaveException;
import managers.inMemoryManagers.InMemoryTasksManager;
import managers.utilityClasses.Transformer;
import tasks.Enums.TasksTypes;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {
    private final Path filePath;

    public FileBackedTasksManager(Path filePath) {
        this.filePath = filePath;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))) {

            String header = "id,type,name,status,description,startTime,duration,epicId" + "\n";
            writer.write(header);

            writer.write(Transformer.allTasksToString(this) + "\n"
                    + Transformer.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        Path filePath = Path.of(file.toString());
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(filePath);

        List<String> csvToString = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                csvToString.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }

        if (csvToString.isEmpty() || csvToString.size() <= 2) {
            return fileBackedTasksManager;
        }
        int nextId = 1;

        for (int i = 1; i <= csvToString.size() - 2; i++) {
            Task task = Transformer.taskFromString(csvToString.get(i));

            if (csvToString.get(i).contains(String.valueOf(TasksTypes.SUBTASK))) {
                fileBackedTasksManager.addBackedSubtask((Subtask) task);
                if (task.getId() >= nextId) {
                    nextId = task.getId() + 1;
                }
            } else if (csvToString.get(i).contains(String.valueOf(TasksTypes.EPIC))) {
                fileBackedTasksManager.addBackedEpic((Epic) task);
                fileBackedTasksManager.updateEpic((Epic) task);
                if (task.getId() >= nextId) {
                    nextId = task.getId() + 1;
                }
            } else if (csvToString.get(i).contains(String.valueOf(TasksTypes.TASK))) {
                fileBackedTasksManager.addBackedTask(task);
                if (task.getId() >= nextId) {
                    nextId = task.getId() + 1;
                }
            }
        }
        fileBackedTasksManager.setNextId(nextId);

        if (!csvToString.get(csvToString.size() - 1).isBlank()) {
            String history = csvToString.get(csvToString.size() - 1);
            List<Integer> historyRecs = Transformer.historyFromString(history);
            for (
                    Integer id : historyRecs) {
                if (fileBackedTasksManager.tasks.containsKey(id)) {
                    fileBackedTasksManager.getTaskById(id);
                } else if (fileBackedTasksManager.epics.containsKey(id)) {
                    fileBackedTasksManager.getEpicById(id);
                } else if (fileBackedTasksManager.subtasks.containsKey(id)) {
                    fileBackedTasksManager.getSubtaskById(id);
                }
            }
        }
        return fileBackedTasksManager;
    }

    @Override
    public int addTask(Task task) {
        if (task == null) {
            return 0;
        }
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask == null) {
            return 0;
        }
        super.addSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            super.updateTask(task);
            save();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            super.updateEpic(epic);
            save();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            super.updateSubtask(subtask);
            save();
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task returnedTask = super.getTaskById(taskId);
        save();
        return returnedTask;
    }

    @Override
    public Epic getEpicById(int taskId) {
        Epic returnedEpic = super.getEpicById(taskId);
        save();
        return returnedEpic;
    }

    @Override
    public Subtask getSubtaskById(int taskId) {
        Subtask returnedSubtask = super.getSubtaskById(taskId);
        save();
        return returnedSubtask;
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int taskId) {
        super.removeEpicById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(int taskId) {
        super.removeSubtaskById(taskId);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }
}