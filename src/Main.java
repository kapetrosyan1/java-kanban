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
        Task updateTask1 = new Task("Лекарства", "Выпить кларитин", "Done");
        Subtask updateSubtask3 = new Subtask("1.", "Прочитать Руслан и Людмила", "Done");
        manager.updateTask(updateTask1, task1);
        manager.updateSubtask(updateSubtask3, subtask3);
        manager.removeAllEpics();
        manager.removeAllSubtasks();
        System.out.println(manager.getAllEpicsSubtasks(epic2));

    }
}
