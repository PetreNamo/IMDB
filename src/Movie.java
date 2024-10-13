public class Movie extends Production {
    public int year;
    public String length;

    public Movie(String title) {
        super(title);
        this.length = "";
        this.year = 0;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Movie: \n" +
                "Title: " + title + "\n" +
                "Year: " + year + "\n" +
                "Length: " + length + "\n" +
                "Summary: " + summary + "\n" +
                "Directors: ");
        for (String d : directors) {
            s.append(d);
            s.append(", ");
        }
        s.append("\n");
        s.append("Actors: ");
        for (String d : actors) {
            s.append(d);
            s.append(", ");
        }
        s.append("\n");
        s.append("Genres: ");
        for (Genre g : genres) {
            s.append(g);
            s.append(", ");
        }
        s.append("\n");
        s.append("Mark: ");
        s.append(mark);
        s.append("\n");
        s.append("Ratings: ");
        s.append(ratings);
        s.append("\n");
        s.append("\n");
        return s.toString();
    }

    public void addLength(String length) {
        this.length = length;
    }

    public void addYear(int year) {
        this.year = year;
    }

    @Override
    public void displayInfo() {
        System.out.println(directors);
        System.out.println(actors);
        System.out.println(genres);
        System.out.println(ratings);
        System.out.println(summary);
        System.out.println(mark);
        System.out.println(length);
        System.out.println(year);
    }
}
