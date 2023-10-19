import Server.HttpTaskServer;
import com.google.gson.JsonObject;
import managers.interfaces.TaskManager;
import managers.utilityClasses.Managers;
import tasks.Enums.Status;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault("http://localhost:8078/");
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}
