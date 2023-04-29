import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();
        Task task = new Task("Др", "Купить алкоголь", "New");
        Task task1 = new Task("Лекарства", "Выпить ново-пассит", "New");
        manager.addTask(task);
        manager.addTask(task1);

        Epic epic = new Epic("Обед", "Покупка продуктов");
        Subtask subtask = new Subtask("1.", "Купить гречку", "New");
        Subtask subtask1 = new Subtask("2.", "Купить мясо", "New");
        manager.addEpic(epic);
        manager.addSubtask(subtask, epic);
        manager.addSubtask(subtask1, epic);

        Epic epic2 = new Epic("Уроки", "Подготовка к литературе");
        Subtask subtask3 = new Subtask("1.", "Прочитать Войну и Мир", "In Progress");
        manager.addEpic(epic2);
        manager.addSubtask(subtask3, epic2);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpicsSubtasks(epic2));

        Task newTask = new Task("Др", "Купить алкоголь", "Done");
        Task newTask1 = new Task("Лекарства", "Выпить ново-пассит", "In Progress");
        Subtask newSubtask = new Subtask("1.", "Купить гречку", "Done");
        Subtask newSubtask3 = new Subtask("1.", "Прочитать Войну и Мир", "Done");

        manager.updateTask(newTask,  task);
        manager.updateTask(newTask1, task1);
        manager.updateSubtask(newSubtask, subtask);
        manager.updateSubtask(newSubtask3, subtask3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpicsSubtasks(epic2));

        manager.removeTaskById(1);
        manager.removeEpicById(6);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpicsSubtasks(epic2));
    }
}
