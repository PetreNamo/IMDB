import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class User<T extends Comparable<T>> implements Comparable, Observer, ExperienceStrategy {
    private Information userInformation;
    private AccountType userType;
    private String username;
    private int experience;
    private List<String> notifications;
    private SortedSet<T> favourites;

    static class Information {
        private Credentials credentials;
        private String nume, tara;
        private String gen;
        private LocalDate datetime;
        private int age;

        public Information(Credentials credentials, String nume, String tara, String gen, LocalDate datetime, int age) {
            this.credentials = credentials;
            this.nume = nume;
            this.tara = tara;
            this.gen = gen;
            this.datetime = datetime;
            this.age = age;
        }

        public String getEmail() {
            return this.credentials.getEmail();
        }

        public String getPassword() {
            return this.credentials.getPassword();
        }

        @Override
        public String toString() {
            return "Information{" +
                    "credentials=" + credentials +
                    ", nume='" + nume + '\'' +
                    ", tara='" + tara + '\'' +
                    ", gen='" + gen + '\'' +
                    ", datetime=" + datetime +
                    ", age=" + age +
                    '}';
        }

        public static class Builder {
            private Credentials credentials;
            private String nume, tara;
            private String gen;
            private LocalDate datetime;
            private int age;

            public Builder setCredentials(Credentials credentials) {
                this.credentials = credentials;
                return this;
            }

            public Builder setName(String name) {
                this.nume = name;
                return this;
            }

            public Builder setCountry(String country) {
                this.tara = country;
                return this;
            }

            public Builder setGender(String gen) {
                this.gen = gen;
                return this;
            }

            public Builder setBirthday(LocalDate time) {
                this.datetime = time;
                return this;
            }

            public Builder setAge(int age) {
                this.age = age;
                return this;
            }

            public Information build() {
                if (credentials == null || nume == null || tara == null || gen == null || datetime == null || age == 0)
                    throw new IllegalStateException();
                return new Information(credentials, nume, tara, gen, datetime, age);
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }

    public User(Information userInformation, AccountType userType, String username, int experience) {
        this.userInformation = userInformation;
        this.userType = userType;
        this.username = username;
        this.experience = experience;
        this.favourites = new TreeSet<>();
    }

    public User() {
        this.userType = null;
        this.username = "";
        this.experience = 0;
        this.notifications = new ArrayList<>();
        this.favourites = new TreeSet<>();
    }

    public int compareTo(Object o1) {
        return this.getClass().getName().compareTo(o1.getClass().getName());
    }

    public void addProduction(Production p) {
        this.favourites.add((T) p);
    }

    public void addActor(Actor a) {
        this.favourites.add((T) a);
    }

    public void removeMovie(Movie m) {
        this.favourites.remove(m);
    }

    public void removeSeries(Series s) {
        this.favourites.remove(s);
    }

    public void removeActor(Actor a) {
        this.favourites.remove(a);
    }

    public void setExperience(int nr) {
        this.experience = nr;
    }

    public void setUserInformation(Information info) {
        this.userInformation = info;
    }

    public AccountType getUserType() {
        return userType;
    }

    public void incExp() {
        this.experience++;
    }

    public void setUserType(String userType) {
        this.userType = AccountType.valueOf(userType.toUpperCase());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addNotification(String notification) {
        this.notifications.add(notification);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return this.userInformation.getEmail();
    }

    public String getPassword() {
        return this.userInformation.getPassword();
    }

    public int getExperience() {
        return experience;
    }

    public List getNotifications() {
        return notifications;
    }

    public SortedSet<T> getFavourites() {
        return favourites;
    }

    @Override
    public String toString() {
        return "User{" +
                "userInformation=" + userInformation +
                ", userType=" + userType +
                ", username='" + username + '\'' +
                ", experience=" + experience +
                ", notifications=" + notifications +
                ", favourites=" + favourites + "\n" +
                '}';
    }

    public abstract void addContrib(Production p);

    public abstract void addContrib(Actor a);

    public abstract List getRequestList();

    public abstract void addRequest(Request r);

    @Override
    public void calculateExperienceaddprod() {
        this.experience = this.experience + 3;
    }

    @Override
    public void calculateExperiencereveiw() {
        this.experience = this.experience + 2;
    }

    @Override
    public void calculateExperienceissue() {
        this.experience = this.experience + 1;
    }

    @Override
    public void update(String message) {
        notifications.add(message);
    }
}
