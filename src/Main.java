import Server.HttpTaskServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer("http://localhost:8078/");
        server.start();
    }
}
