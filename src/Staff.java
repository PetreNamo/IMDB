import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Staff<T extends Comparable<T>> extends User<T> implements StaffInterface {
    private List<Request> requestList;
    private SortedSet<T> added;

    public Staff(Information userInformation, AccountType userType, String username, int experience) {
        super(userInformation, userType, username, experience);
        this.requestList = new ArrayList<>();
        this.added = new TreeSet<>();
    }

    public Staff() {
        super();
        this.requestList = new ArrayList<>();
        this.added = new TreeSet<>();
    }

    public void addMovie(Movie m) {
        this.added.add((T) m);
    }

    public void addSeries(Series s) {
        this.added.add((T) s);
    }

    @Override
    public void addContrib(Production p) {
        addProductionSystem(p);
    }

    @Override
    public void addContrib(Actor a) {
        addActorSystem(a);
    }

    @Override
    public void addProductionSystem(Production p) {
        this.added.add((T) p);
    }

    @Override
    public void addActorSystem(Actor a) {
        this.added.add((T) a);
    }

    @Override
    public void removeProductionSystem(String name) {

    }

    @Override
    public void removeActorSystem(String name) {

    }

    @Override
    public void updateProduction(Production p) {

    }

    @Override
    public void updateActor(Actor a) {

    }

    public List getRequestList() {
        return this.requestList;
    }

    public SortedSet<T> getAdded() {
        return added;
    }

    @Override
    public void addRequest(Request r) {
        this.requestList.add(r);
    }

    @Override
    public String toString() {
        return "Staff{" +
                "requestList=" + requestList +
                ", added=" + added +
                '}';
    }
}
