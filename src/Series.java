import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Series extends Production {
    public int release_year, no_seasons;
    public Map<String, List<Episode>> seasons;

    public Series(String title) {
        super(title);
        this.release_year = 0;
        this.no_seasons = 0;
        this.seasons = new LinkedHashMap<>();
    }

    public void addYear(int year) {
        this.release_year = year;
    }

    public void addnoSeas(int nr) {
        this.no_seasons = nr;
    }

    public void addSeason(String name, List episodes) {
        seasons.put(name, episodes);
    }

    public List<Episode> getSeason(int nr) {
        String season = "Season " + nr;
        return seasons.get(season);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Series: \n" +
                "Title: " + title + "\n" +
                "Release_year: " + release_year + "\n" +
                "Summary: " + summary + "\n" + "Directors: ");
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
        s.append("Number of seasons: ");
        s.append(no_seasons);
        s.append("\n");
        s.append("Seasons: ");
        s.append(seasons);
        s.append("\n");
        s.append("Ratings: ");
        s.append(ratings);
        s.append("\n");
        s.append("\n");
        return s.toString();
    }

    @Override
    public void displayInfo() {
        System.out.println(directors);
        System.out.println(actors);
        System.out.println(genres);
        System.out.println(ratings);
        System.out.println(summary);
        System.out.println(mark);
        System.out.println(release_year);
        System.out.println(no_seasons);
        System.out.println(seasons);
    }
}
