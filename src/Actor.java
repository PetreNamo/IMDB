import java.util.ArrayList;
import java.util.List;

public class Actor implements Comparable {
    public String name;
    public List<Pair<String, String>> roles;
    public String bio;

    public Actor(String name, String bio) {
        this.name = name;
        this.roles = new ArrayList<>();
        this.bio = bio;
    }

    public void addMovie(String name, String type) {
        roles.add(new Pair<>(name, type));
    }

    public void addBio(String bio) {
        this.bio = bio;
    }

    public void addName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Name: " + name + "\n" +
                "Bio: " + bio + "\n" +
                "Roles: \n");
        for (Pair p : roles) {
            s.append("Title: ");
            s.append(p.getFirst());
            s.append("\n");
            s.append("Type: ");
            s.append(p.getSecond());
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Actor)
            return this.name.compareTo(((Actor) o).name);
        else if (o instanceof Production)
            return this.name.compareTo(((Production) o).title);
        return 0;
    }
}

class Pair<T, U> {
    private final T first;
    private final U second;

    @Override
    public String toString() {
        return "Title: " + first + "\n" +
                "Type: " + second + "\n\n";
    }

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}