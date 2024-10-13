public class Episode {
    public String name;
    public String length;

    public Episode(String name, String length) {
        this.name = name;
        this.length = length;
    }

    @Override
    public String toString() {
        return "{" +
                "name=" + name +
                ", length=" + length +
                '}';
    }
}
