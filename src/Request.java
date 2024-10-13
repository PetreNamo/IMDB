import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Request implements Subject {
    private RequestTypes RequestType;
    private LocalDateTime time;
    private String title;
    private String description;
    private String to;
    private String username;
    public List<Observer> users, staff;

    public Request(RequestTypes RequestType, LocalDateTime time, String title, String problem, String aduser, String user_name) {
        this.RequestType = RequestType;
        this.time = time;
        this.title = title;
        this.description = problem;
        this.to = aduser;
        this.username = user_name;
        this.users = new ArrayList<>();
        this.staff = new ArrayList<>();
    }

    public RequestTypes getType() {
        return RequestType;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTo() {
        return to;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Request:\n\t" +
                "RequestType: " + RequestType +
                ", time: " + time +
                ", title: '" + title + '\'' +
                ", description: '" + description + '\'' +
                ", to: '" + to + '\'' +
                ", username: '" + username + '\n'
                ;
    }

    @Override
    public void addObserver(Observer observer) {
        users.add(observer);
    }

    public void addObserverst(Observer observer) {
        staff.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        users.add(observer);
    }

    public void removeObserverst(Observer observer) {
        staff.add(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : users)
            observer.update(message);
    }

    public void notifyObserversSt(String message) {
        for (Observer observer : staff)
            observer.update(message);
    }
}
