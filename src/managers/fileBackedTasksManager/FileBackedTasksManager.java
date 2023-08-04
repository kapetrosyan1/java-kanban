package managers.fileBackedTasksManager;

import exceptions.ManagerSaveException;
import managers.inMemoryManagers.InMemoryTasksManager;
import managers.utilityClasses.Transformer;
import org.jetbrains.annotations.NotNull;
import tasks.Enums.Status;
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

    public static void main(String[] args) {
        try {
            FileBackedTasksManager fileBackedTasksManager
                    = new FileBackedTasksManager(Path.of("resources/recovery.csv"));

            Task task = new Task("Задача 1", Status.NEW, "Купить алкоголь");
            Task task1 = new Task("Задача 2", Status.NEW, "Выпить ново-пассит");
            fileBackedTasksManager.addTask(task);
            fileBackedTasksManager.addTask(task1);
            Epic epic = new Epic("Эпик 1", "Покупка продуктов");
            Subtask subtask = new Subtask("подзадача 1.", Status.NEW, "Купить гречку", 3);
            Subtask subtask1 = new Subtask("подзадача 2.", Status.NEW, "Купить мясо", 3);
            Subtask subtask2 = new Subtask("подзадача 3.", Status.NEW, "Купить мясо", 3);

            fileBackedTasksManager.addEpic(epic);

            fileBackedTasksManager.addSubtask(subtask);
            fileBackedTasksManager.addSubtask(subtask1);
            fileBackedTasksManager.addSubtask(subtask2);

            Epic epic2 = new Epic("Эпик 2", "Подготовка к литературе");
            fileBackedTasksManager.addEpic(epic2);

            fileBackedTasksManager.getTaskById(1);
            fileBackedTasksManager.getTaskById(2);
            fileBackedTasksManager.getEpicById(3);
            fileBackedTasksManager.getSubtaskById(4);
            fileBackedTasksManager.getSubtaskById(6);
            fileBackedTasksManager.getSubtaskById(5);
            System.out.println(fileBackedTasksManager.historyManager.getHistory());
            fileBackedTasksManager = loadFromFile(new File("resources/recovery.csv"));
            fileBackedTasksManager.getTaskById(2);
            fileBackedTasksManager.getSubtaskById(4);
            System.out.println(fileBackedTasksManager.historyManager.getHistory());
            fileBackedTasksManager.clearTasks();

        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    private void save() {
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

        if (csvToString.isEmpty() || csvToString.size() <= 2) return fileBackedTasksManager;
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

        if (csvToString.size() > 1 && !csvToString.get(csvToString.size() - 1).isBlank()) {
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
    public int addTask(@NotNull Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(@NotNull Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubtask(@NotNull Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void updateTask(@NotNull Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(@NotNull Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(@NotNull Subtask subtask) {
        super.updateSubtask(subtask);
        save();
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
