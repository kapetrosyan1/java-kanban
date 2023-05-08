import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getHistory();
        Task task = new Task("Др", "Купить алкоголь", Status.NEW);
        Task task1 = new Task("Лекарства", "Выпить ново-пассит", Status.NEW);
        manager.addTask(task);
        manager.addTask(task1);

        Epic epic = new Epic("Обед", "Покупка продуктов");
        Subtask subtask = new Subtask("1.", "Купить гречку", Status.NEW);
        Subtask subtask1 = new Subtask("2.", "Купить мясо", Status.NEW);
        manager.addEpic(epic);
        manager.addSubtask(subtask, epic);
        manager.addSubtask(subtask1, epic);
        manager.getTaskById(1);
        manager.getTaskById(2);

        System.out.println(historyManager.getHistory());

        Epic epic2 = new Epic("Уроки", "Подготовка к литературе");
        Subtask subtask3 = new Subtask("1.", "Прочитать Войну и Мир", Status.IN_PROGRESS);
        manager.addEpic(epic2);
        manager.addSubtask(subtask3, epic2);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);

        System.out.println(historyManager.getHistory());

    }
}
