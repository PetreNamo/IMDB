import java.util.ArrayList;
import java.util.List;

public abstract class Production implements Comparable{
    public String title;
    public List<String> directors;
    public List<String> actors;
    public List<Genre> genres;
    public List<Rating> ratings;
    public String summary;
    public double mark;
    public Production(String title) {
        this.title=title;
        this.directors=new ArrayList<>();
        this.actors=new ArrayList<>();
        this.genres=new ArrayList<>();
        this.ratings=new ArrayList<>();
        this.summary="";
        this.mark=0;
    }
    public void addDirector(String dir) {
        directors.add(dir);
    }
    public void addActor(String act) {
        actors.add(act);
    }
    public void addGenre(String gen) {
        if(Genre.find(gen)!=null) {
            Genre aux = Genre.valueOf(gen);
            genres.add(aux);
        }
    }
    public void addRating(Rating r) {
        ratings.add(r);
    }
    public void addSummary(String summary) {
        this.summary=summary;
    }
    public void addMark(Double mark) {
        this.mark=mark;
    }
    public void calculateMarkadd(int nou) {
        this.mark=this.mark*this.ratings.size();
        this.mark=this.mark+nou;
        this.mark=this.mark/(this.ratings.size()+1);
    }
    public void calculateMarkrem(int rem) {
        this.mark=this.mark*this.ratings.size();
        this.mark=this.mark-rem;
        this.mark=this.mark/(this.ratings.size()-1);
    }
    public abstract void displayInfo();
    public String getName() {
        return this.title;
    }
    @Override
    public String toString() {
        return "Production{" +
                "title='" + title + '\'' +
                ", directors=" + directors +
                ", actors=" + actors +
                ", genres=" + genres +
                ", ratings=" + ratings +
                ", summary='" + summary + '\'' +
                ", mark=" + mark +
                '}';
    }

    public int compareTo(Object o) {
        if(o instanceof Actor)
            return this.title.compareTo(((Actor) o).name);
        else if(o instanceof Production)
            return this.title.compareTo(((Production) o).title);
        return 0;
    }
}
