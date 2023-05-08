package tasks;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    public Task(String title, String description, Status status) {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

   @Override
    public String toString() {
       return "{Task  - " + title
               + ", Description - " + description
               + ", Task Status - " + status
               + "};";
    }

}
