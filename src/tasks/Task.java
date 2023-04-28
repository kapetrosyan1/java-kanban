package tasks;

public class Task {
    private String title;
    private String description;
    private int id;
    private String status;

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

   @Override
    public String toString() {
       return "Task  - " + title
               + ", Description - " + description
               + ", Task Status - " + status;
    }

}
