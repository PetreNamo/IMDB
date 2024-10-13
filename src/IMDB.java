import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IMDB {
    public List<User> users;
    public List<Actor> actors;
    public List<Request> requests;
    public List<Production> productions;

    public IMDB() {
        this.users = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.productions = new ArrayList<>();
    }

    public void run() throws IOException, ParseException {
        String directory = "src/";
        File director = new File(directory);
        File[] files = director.listFiles();
        for (File file : files)
            if (file.isFile() && file.getName().equals("actors.json"))
                readfromActors(file);
        for (File file : files)
            if (file.isFile() && file.getName().equals("production.json"))
                readfromProduction(file);
        for (File file : files)
            if (file.isFile() && file.getName().equals("accounts.json"))
                readfromAccounts(file);
        for (File file : files)
            if (file.isFile() && file.getName().equals("requests.json"))
                readfromRequests(file);
    }

    public void readfromAccounts(File file) throws IOException, ParseException {
        FileReader fileReader = new FileReader(file);
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(fileReader);
        JSONArray jsonArray = (JSONArray) obj;
        for (Object element : jsonArray) {
            JSONObject jsonObject = (JSONObject) element;
            String username = (String) jsonObject.get("username");
            String experience = (String) jsonObject.get("experience");
            int exp = 0;
            if (experience != null)
                exp = Integer.parseInt(experience);
            JSONObject info = (JSONObject) jsonObject.get("information");
            JSONObject cred = (JSONObject) info.get("credentials");
            String email = (String) cred.get("email");
            String password = (String) cred.get("password");
            Credentials credentials = new Credentials(email, password);
            String name = (String) info.get("name");
            String country = (String) info.get("country");
            Long age = (Long) info.get("age");
            int age1 = age.intValue();
            String gender = (String) info.get("gender");
            String birthday = (String) info.get("birthDate");
            LocalDate time = LocalDate.parse(birthday, DateTimeFormatter.ISO_LOCAL_DATE);
            User.Information information = User.Information.builder()
                    .setName(name)
                    .setAge(age1)
                    .setCountry(country)
                    .setGender(gender)
                    .setCredentials(credentials)
                    .setBirthday(time)
                    .build();
            String type = (String) jsonObject.get("userType");
            UserFactory userFactory = new UserFactory();
            User user = userFactory.getUser(type);
            user.setUsername(username);
            user.setExperience(exp);
            user.setUserInformation(information);
            user.setUserType(type);
            JSONArray pcontrib = (JSONArray) jsonObject.get("productionsContribution");
            if (pcontrib != null && (user.getUserType() == AccountType.ADMIN || user.getUserType() == AccountType.CONTRIBUTOR)) {
                Iterator<String> iterator = pcontrib.iterator();
                while (iterator.hasNext()) {
                    String prod = iterator.next();
                    Production p = getprod(prod);
                    if (type.compareTo("Admin") != 0)
                        user.addContrib(p);
                    else
                        Admin.admincontrib.add(p);
                }
            }
            JSONArray acontrib = (JSONArray) jsonObject.get("actorsContribution");
            if (acontrib != null && (user.getUserType() == AccountType.ADMIN || user.getUserType() == AccountType.CONTRIBUTOR)) {
                Iterator<String> iterator = acontrib.iterator();
                while (iterator.hasNext()) {
                    String act = iterator.next();
                    Actor actor = getActor(act);
                    if (type.compareTo("Admin") != 0)
                        user.addContrib(actor);
                    else
                        Admin.admincontrib.add(actor);
                }
            }
            JSONArray favpr = (JSONArray) jsonObject.get("favoriteProductions");
            if (favpr != null) {
                Iterator<String> iterator = favpr.iterator();
                while (iterator.hasNext()) {
                    String prod = iterator.next();
                    Production p = getprod(prod);
                    user.addProduction(p);
                }
            }
            JSONArray favact = (JSONArray) jsonObject.get("favoriteActors");
            if (favact != null) {
                Iterator<String> iterator = favact.iterator();
                while (iterator.hasNext()) {
                    String act = iterator.next();
                    Actor actor = getActor(act);
                    user.addActor(actor);
                }
            }
            JSONArray notifications = (JSONArray) jsonObject.get("notifications");
            if (notifications != null) {
                Iterator<String> iterator = notifications.iterator();
                while (iterator.hasNext()) {
                    String notif = iterator.next();
                    user.addNotification(notif);
                }
            }
            users.add(user);
        }
    }

    public void readfromActors(File file) throws IOException, ParseException {
        FileReader fileReader = new FileReader(file);
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(fileReader);
        JSONArray jsonArray = (JSONArray) obj;
        for (Object element : jsonArray) {
            JSONObject Actors = (JSONObject) element;
            String name = (String) Actors.get("name");
            Actor actor = new Actor(name, "");
            JSONArray performances = (JSONArray) Actors.get("performances");
            Iterator<JSONObject> iterator = performances.iterator();
            while (iterator.hasNext()) {
                JSONObject production = iterator.next();
                String title = (String) production.get("title");
                String type = (String) production.get("type");
                actor.addMovie(title, type);
            }
            String bio = (String) Actors.get("biography");
            actor.addBio(bio);
            actors.add(actor);
        }
    }

    public void readfromProduction(File file) throws IOException, ParseException {
        FileReader fileReader = new FileReader(file);
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(fileReader);
        JSONArray jsonArray = (JSONArray) obj;
        for (Object element : jsonArray) {
            JSONObject prod = (JSONObject) element;
            String title = (String) prod.get("title");
            String type = (String) prod.get("type");
            if (type.equals("Movie")) {
                Movie movie = new Movie(title);
                JSONArray directors = (JSONArray) prod.get("directors");
                Iterator<String> iterator = directors.iterator();
                while (iterator.hasNext()) {
                    String dir = iterator.next();
                    movie.addDirector(dir);
                }
                JSONArray actors = (JSONArray) prod.get("actors");
                Iterator<String> iterator2 = actors.iterator();
                while (iterator2.hasNext()) {
                    String act = iterator2.next();
                    movie.addActor(act);
                }
                JSONArray genres = (JSONArray) prod.get("genres");
                Iterator<String> iterator3 = genres.iterator();
                while (iterator3.hasNext()) {
                    String gen = iterator3.next();
                    movie.addGenre(gen);
                }
                JSONArray reviews = (JSONArray) prod.get("ratings");
                Iterator<JSONObject> iterator4 = reviews.iterator();
                while (iterator4.hasNext()) {
                    JSONObject rating = iterator4.next();
                    String username = (String) rating.get("username");
                    Long mark = (Long) rating.get("rating");
                    int mark1 = mark.intValue();
                    String comments = (String) rating.get("comment");
                    Rating rate = new Rating(username, mark1, comments);
                    movie.addRating(rate);
                }
                String desc = (String) prod.get("plot");
                movie.addSummary(desc);
                Double avg = (Double) prod.get("averageRating");
                movie.addMark(avg);
                String duration = (String) prod.get("duration");
                movie.addLength(duration);
                Long year = (Long) prod.get("releaseYear");
                int year1 = 0;
                if (year != null)
                    year1 = year.intValue();
                else
                    year1 = 0;
                movie.addYear(year1);
                productions.add(movie);
            } else if (type.equals("Series")) {
                Series series = new Series(title);
                JSONArray directors = (JSONArray) prod.get("directors");
                Iterator<String> iterator = directors.iterator();
                while (iterator.hasNext()) {
                    String dir = iterator.next();
                    series.addDirector(dir);
                }
                JSONArray actors = (JSONArray) prod.get("actors");
                Iterator<String> iterator2 = actors.iterator();
                while (iterator2.hasNext()) {
                    String act = iterator2.next();
                    series.addActor(act);
                }
                JSONArray genres = (JSONArray) prod.get("genres");
                Iterator<String> iterator3 = genres.iterator();
                while (iterator3.hasNext()) {
                    String gen = iterator3.next();
                    series.addGenre(gen);
                }
                JSONArray reviews = (JSONArray) prod.get("ratings");
                Iterator<JSONObject> iterator4 = reviews.iterator();
                while (iterator4.hasNext()) {
                    JSONObject rating = iterator4.next();
                    String username = (String) rating.get("username");
                    Long mark = (Long) rating.get("rating");
                    int mark1 = mark.intValue();
                    String comments = (String) rating.get("comment");
                    Rating rate = new Rating(username, mark1, comments);
                    series.addRating(rate);
                }
                String desc = (String) prod.get("plot");
                series.addSummary(desc);
                Double avg = (Double) prod.get("averageRating");
                series.addMark(avg);
                Long year = (Long) prod.get("releaseYear");
                int year1 = year.intValue();
                series.addYear(year1);
                Long seas = (Long) prod.get("numSeasons");
                int seas1 = seas.intValue();
                series.addnoSeas(seas1);
                JSONObject sezon = (JSONObject) prod.get("seasons");
                for (Object nrsez : sezon.keySet()) {
                    List<Episode> episodeList = new ArrayList<>();
                    JSONArray episodes = (JSONArray) sezon.get(nrsez);
                    Iterator<JSONObject> iterator5 = episodes.iterator();
                    while (iterator5.hasNext()) {
                        JSONObject episode = iterator5.next();
                        String name = (String) episode.get("episodeName");
                        String duration = (String) episode.get("duration");
                        Episode episod = new Episode(name, duration);
                        episodeList.add(episod);
                    }
                    series.addSeason((String) nrsez, episodeList);
                }
                productions.add(series);
            }
        }
    }

    public void readfromRequests(File file) throws IOException, ParseException {
        FileReader fileReader = new FileReader(file);
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(fileReader);
        JSONArray jsonArray = (JSONArray) obj;
        for (Object element : jsonArray) {
            JSONObject req = (JSONObject) element;
            String type = (String) req.get("type");
            String datetime = (String) req.get("createdDate");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
            String username = (String) req.get("username");
            User user = getUserusername(username);
            String title = "";
            title = (String) req.get("actorName");
            if (title == null)
                title = (String) req.get("movieTitle");
            if (title == null)
                title = "-";
            String to = (String) req.get("to");
            String desc = (String) req.get("description");
            Request request = new Request(RequestTypes.valueOf(type), localDateTime, title, desc, to, username);
            request.addObserver(user);
            requests.add(request);
            if (request.getTo().compareTo("ADMIN") == 0) {
                Admin.RequestsHolder.requestList.add(request);
                for (User user1 : users)
                    if (user1.getUserType().compareTo(AccountType.ADMIN) == 0)
                        request.addObserverst(user1);
            } else if (request.getTo().compareTo("ADMIN") != 0 && request.getTo().compareTo("-") != 0) {
                for (User aux : users) {
                    if (aux.getUsername().compareTo(request.getTo()) == 0) {
                        aux.addRequest(request);
                        request.addObserverst(aux);
                    }
                }
            }
            request.notifyObserversSt("New request!");
        }
    }

    public Production getprod(String s) {
        for (Production p : productions)
            if (p.title.toLowerCase().compareTo(s.toLowerCase()) == 0)
                return p;
        return null;
    }

    public Actor getActor(String s) {
        for (Actor aux : actors)
            if (aux.name.toLowerCase().compareTo(s.toLowerCase()) == 0)
                return aux;
        return null;
    }

    public User getUser(String email) {
        for (User aux : users)
            if (aux.getEmail().compareTo(email) == 0)
                return aux;
        return null;
    }

    public User getUserusername(String username) {
        for (User aux : users)
            if (aux.getUsername().compareTo(username) == 0)
                return aux;
        return null;
    }


    public void showProductions() {
        for (Production aux : productions)
            System.out.println(aux);
    }

    public Production getProduction(String s) {
        for (Production aux : productions)
            if (aux.title.toLowerCase().compareTo(s.toLowerCase()) == 0)
                return aux;
        return null;
    }

    public void showActors() {
        for (Actor actor : actors)
            System.out.println(actor);
    }

    public void showProductionsGenre(Genre genre) {
        for (Production aux : productions)
            if (aux.genres.contains(genre))
                System.out.println(aux);
    }

    public void showProductionsRating(int nr) {
        for (Production aux : productions)
            if (aux.ratings.size() >= nr)
                System.out.println(aux);
    }

    public void usershowProd(Scanner scanner) {
        showProductions();
        int ok1 = 0;
        while (ok1 == 0) {
            System.out.println("\t1) Filter by genre");
            System.out.println("\t2) Filter by number of ratings");
            System.out.println("\t3) Exit");
            int choice1 = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    choice1 = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (choice1 == 1) {
                int ok2 = 0;
                while (ok2 == 0) {
                    System.out.println("Choose a genre: ");
                    String genre = scanner.next();
                    if (Genre.find(genre) != null) {
                        showProductionsGenre(Genre.find(genre));
                        ok2 = 1;
                    } else
                        System.out.println("Inexsistent Genre. Try again!");
                }
            } else if (choice1 == 2) {
                int ok2 = 0;
                while (ok2 == 0) {
                    System.out.println("Choose a minimum number of ratings: ");
                    int nr = 0;
                    validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number: ");
                            nr = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                    } while (!validInput);
                    if (nr > 0) {
                        showProductionsRating(nr);
                        ok2 = 1;
                    } else
                        System.out.println("Negative number. Insert another one!");
                }
            } else if (choice1 == 3)
                ok1 = 1;
            else
                System.out.println("Invalid number!");
        }
    }

    public void usershowActors(Scanner scanner) {
        showActors();
        int ok1 = 0;
        while (ok1 == 0) {
            System.out.println("\t1) Sort alphabetically");
            System.out.println("\t2) Exit");
            int choice1 = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    choice1 = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (choice1 == 1) {
                actors.sort(Actor::compareTo);
                showActors();
            } else if (choice1 == 2)
                ok1 = 1;
            else
                System.out.println("Number not allowed!");
        }
    }

    public void usershowNotifications(User user) {
        if (!user.getNotifications().isEmpty()) {
            System.out.println(user.getNotifications());
        } else
            System.out.println("No notifications");
    }

    public void userSearch(Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            System.out.println("\t1) Enter name of actor or production");
            System.out.println("\t2) Exit");
            int nr = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    nr = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (nr == 1) {
                System.out.println("Search: ");
                String search = scanner.nextLine();
                int ok1 = 0;
                for (Actor act : actors)
                    if (act.name.toLowerCase().compareTo(search.toLowerCase()) == 0) {
                        System.out.println(act);
                        ok1 = 1;
                    }
                for (Production prod : productions)
                    if (prod.title.toLowerCase().compareTo(search.toLowerCase()) == 0) {
                        System.out.println(prod);
                        ok1 = 1;
                    }
                if (ok1 == 0)
                    System.out.println("Inexistent Actor or Production!");
            } else if (nr == 2) {
                ok = 1;
            } else
                System.out.println("Number not allowed!");
        }
    }

    public void userchangeFavorites(User user, Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            System.out.println("\t1) Add to favorites");
            System.out.println("\t2) Remove from favorites");
            System.out.println("\t3) Exit");
            int nr = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    nr = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (nr == 1) {
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter the name of an actor or of a production:");
                    String entered = scanner.nextLine();
                    for (Actor act : actors)
                        if (act.name.toLowerCase().compareTo(entered.toLowerCase()) == 0) {
                            user.getFavourites().add(act);
                            ok1 = 1;
                            System.out.println(user.getFavourites());
                        }
                    for (Production prod : productions)
                        if (prod.title.toLowerCase().compareTo(entered.toLowerCase()) == 0) {
                            user.getFavourites().add(prod);
                            ok1 = 1;
                            System.out.println(user.getFavourites());
                        }
                    if (ok1 == 0)
                        System.out.println("Inexistent actor or production. Try again!");
                }
            } else if (nr == 2 && !user.getFavourites().isEmpty()) {
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println(user.getFavourites());
                    System.out.println("Enter the name of an actor or of a production:");
                    String entered = scanner.nextLine();
                    Iterator<Object> iterator = user.getFavourites().iterator();
                    while (iterator.hasNext()) {
                        Object obj = iterator.next();
                        if (obj instanceof Actor) {
                            if (((Actor) obj).name.toLowerCase().compareTo(entered.toLowerCase()) == 0) {
                                iterator.remove();
                                ok1 = 1;
                                System.out.println(user.getFavourites());
                            }
                        } else if (obj instanceof Production) {
                            if (((Production) obj).title.toLowerCase().compareTo(entered.toLowerCase()) == 0) {
                                iterator.remove();
                                ok1 = 1;
                                System.out.println(user.getFavourites());
                            }
                        }
                    }
                    if (ok1 == 0)
                        System.out.println("Inexistent actor or production. Try again!");
                }
            } else if (nr == 2 && user.getFavourites().isEmpty())
                System.out.println("Favorites list is empty");
            else if (nr == 3)
                ok = 1;
            else
                System.out.println("Invalid number!");
        }
    }

    public void userRequest(User user, Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            String type = "";
            String to = "";
            String desc = "";
            String title = "";
            LocalDateTime localDateTime = LocalDateTime.now();
            System.out.println("\t1) Create a request");
            System.out.println("\t2) Delete a request");
            System.out.println("\t3) Exit");
            int choice = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (choice == 1) {
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter request type.");
                    System.out.println("Choose from DELETE_ACCOUNT, ACTOR_ISSUE, MOVIE_ISSUE, OTHERS: ");
                    type = scanner.nextLine();
                    if (type.compareTo("DELETE_ACCOUNT") == 0 || type.compareTo("ACTOR_ISSUE") == 0 || type.compareTo("MOVIE_ISSUE") == 0 || type.compareTo("OTHERS") == 0)
                        ok1 = 1;
                    else
                        System.out.println("Type error. Try again!");
                }
                localDateTime = LocalDateTime.now();
                User auxu = null;
                if (type.compareTo("OTHERS") == 0 || type.compareTo("DELETE_ACCOUNT") == 0)
                    to = "ADMIN";
                else if (type.compareTo("ACTOR_ISSUE") == 0 || type.compareTo("MOVIE_ISSUE") == 0) {
                    for (User aux : users)
                        if (aux.getUserType().compareTo(AccountType.CONTRIBUTOR) == 0)
                            System.out.println(aux.getUsername());
                    int ok2 = 0;

                    while (ok2 == 0) {
                        System.out.println("Choose a contributor you would like to address this request to:");
                        to = scanner.nextLine();
                        for (User aux2 : users)
                            if (aux2.getUsername().compareTo(to) == 0) {
                                ok2 = 1;
                                auxu = getUserusername(to);
                            }
                        if (ok2 == 0)
                            System.out.println("Inexistent user. Try again!");
                    }
                    ok2 = 0;
                    while (ok2 == 0) {
                        System.out.println("Choose the title or name:");
                        title = scanner.nextLine();
                        for (Actor act : actors)
                            if (act.name.toLowerCase().compareTo(title.toLowerCase()) == 0)
                                ok2 = 1;
                        for (Production prod : productions)
                            if (prod.title.toLowerCase().compareTo(title.toLowerCase()) == 0)
                                ok2 = 1;
                        if (ok2 == 0)
                            System.out.println("Inexistent Production or Actor. Try again!");
                    }
                }
                System.out.println("Describe your issue:");
                desc = scanner.nextLine();
                Request request = new Request(RequestTypes.valueOf(type), localDateTime, title, desc, to, user.getUsername());
                requests.add(request);

                if (to.compareTo("ADMIN") == 0) {
                    Admin.RequestsHolder.requestList.add(request);
                } else {
                    for (User user1 : users)
                        if (user1.getUsername().compareTo(to) == 0) {
                            user1.addRequest(request);
                        }
                }
                if (auxu != null)
                    request.addObserverst(auxu);
                else {
                    for (User user1 : users)
                        if (user1.getUserType().compareTo(AccountType.ADMIN) == 0)
                            request.addObserverst(user1);
                }
                request.notifyObserversSt("New request");
                request.addObserver(user);
            } else if (choice == 2) {
                List<Request> lista = new ArrayList<>();
                for (Request req : requests)
                    if (req.getUsername().compareTo(user.getUsername()) == 0)
                        lista.add(req);
                int ok1 = 0;
                while (ok1 == 0) {
                    if (!lista.isEmpty()) {
                        System.out.println(lista);
                        System.out.print("Choose a number from 1 to ");
                        System.out.println(lista.size() + " to delete the respective request");
                        int del = 0;
                        validInput = false;
                        do {
                            try {
                                System.out.print("Enter a number: ");
                                del = scanner.nextInt();
                                scanner.nextLine();
                                validInput = true;
                            } catch (InputMismatchException e) {
                                System.out.println("Insert a number!");
                                scanner.nextLine();
                            }
                        } while (!validInput);
                        if (del >= 1 && del <= lista.size()) {
                            del--;
                            Request request = lista.get(del);
                            requests.remove(lista.get(del));
                            if (request.getTo().compareTo("ADMIN") == 0)
                                Admin.RequestsHolder.requestList.remove(request);
                            else {
                                User aux = getUserusername(request.getTo());
                                aux.getRequestList().remove(request);
                            }

                            ok1 = 1;
                        } else
                            System.out.println("Invalid index!");
                    } else {
                        ok1 = 1;
                        System.out.println("No requests made!");
                    }
                }
            } else if (choice == 3)
                ok = 1;
            else
                System.out.println("Invalid number!");
        }
    }

    public void userRating(User user, Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            int rating = 0;
            String produ = "";
            System.out.println("\t1) Add a rating");
            System.out.println("\t2) Delete a rating");
            System.out.println("\t3) Exit");
            int choice = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (choice == 1) {
                List<String> titluri = new ArrayList<>();
                for (Production prod : productions) {
                    int ok1 = 1;
                    for (Rating rate : prod.ratings)
                        if (rate.username.compareTo(user.getUsername()) == 0)
                            ok1 = 0;
                    if (ok1 == 1)
                        titluri.add(prod.title);
                }
                System.out.println(titluri);
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Choose a production to rate:");
                    produ = scanner.nextLine();
                    if (titluri.contains(produ))
                        ok1 = 1;
                    else
                        System.out.println("Invalid Production. Try again!");
                }
                int ok2 = 0;
                while (ok2 == 0) {
                    System.out.println("Enter a rating, integer from 1 to 10");
                    rating = 0;
                    validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number: ");
                            rating = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                    } while (!validInput);
                    if (rating >= 1 && rating <= 10)
                        ok2 = 1;
                    else
                        System.out.println("Number not allowed!");
                }
                System.out.println("Enter comments:");
                String comments = scanner.nextLine();
                Rating rating1 = new Rating(user.getUsername(), rating, comments);
                Production pro = getProduction(produ);
                if (pro != null) {
                    pro.calculateMarkadd(rating);
                    pro.addRating(rating1);
                    user.calculateExperiencereveiw();
                }
            } else if (choice == 2) {
                String product = "";
                List<String> titluri = new ArrayList<>();
                for (Production prod : productions) {
                    int ok1 = 0;
                    for (Rating rate : prod.ratings)
                        if (rate.username.compareTo(user.getUsername()) == 0)
                            ok1 = 1;
                    if (ok1 == 1)
                        titluri.add(prod.title);
                }
                System.out.println(titluri);
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Choose a production to delete rating:");
                    product = scanner.nextLine();
                    if (titluri.contains(product))
                        ok1 = 1;
                    else
                        System.out.println("Invalid Production. Try again!");
                }
                Production production = getProduction(product);
                Iterator<Rating> iterator = production.ratings.iterator();
                while (iterator.hasNext()) {
                    Rating aux = iterator.next();
                    if (aux.username.equals(user.getUsername())) {
                        production.calculateMarkrem(aux.rating);
                        iterator.remove();
                    }
                }
            } else if (choice == 3)
                ok = 1;
            else
                System.out.println("Invalid number!");
        }
    }

    public void addRemove(User user, Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            System.out.println("\t1) Add production");
            System.out.println("\t2) Add actor");
            System.out.println("\t3) Remove production/actor");
            System.out.println("\t4) Exit");
            int choice = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (choice == 1) {
                System.out.println("\t1) Add Movie");
                System.out.println("\t2) Add Series");
                int choice2 = 0;
                validInput = false;
                do {
                    try {
                        System.out.print("Enter a number: ");
                        choice2 = scanner.nextInt();
                        scanner.nextLine();
                        validInput = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Insert a number!");
                        scanner.nextLine();
                    }
                } while (!validInput);
                if (choice2 == 1) {
                    System.out.println("Insert title:");
                    String title = scanner.nextLine();
                    Movie movie = new Movie(title);
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Insert directors, type done if finished:");
                        String director = scanner.nextLine();
                        if (director.toLowerCase().compareTo("done") == 0)
                            ok1 = 1;
                        else
                            movie.addDirector(director);
                    }
                    ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Insert actors, type done if finished:");
                        String actor = scanner.nextLine();
                        if (actor.toLowerCase().compareTo("done") == 0)
                            ok1 = 1;
                        else
                            movie.addActor(actor);
                    }
                    ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Insert genres, type done if finished:");
                        String genre = scanner.nextLine();
                        if (genre.toLowerCase().compareTo("done") == 0)
                            ok1 = 1;
                        else {
                            if (Genre.find(genre) != null)
                                movie.addGenre(genre);
                            else
                                System.out.println("Inexistent genre!");
                        }
                    }
                    System.out.println("Insert movie summary:");
                    String summary = scanner.nextLine();
                    movie.addSummary(summary);
                    System.out.println("Insert movie release year:");
                    int ryear = 0;
                    validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number between 1888 and 2023: ");
                            ryear = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                        if (ryear < 1888 || ryear > 2023)
                            validInput = false;
                    } while (!validInput);
                    movie.addYear(ryear);
                    System.out.println("Insert movie length:");
                    String length = scanner.nextLine();
                    movie.addLength(length);
                    productions.add(movie);
                    if (user instanceof Staff<?> && user.getUserType().compareTo(AccountType.ADMIN) != 0) {
                        ((Staff) user).getAdded().add(movie);
                        user.calculateExperienceaddprod();
                    } else
                        Admin.admincontrib.add(movie);
                    System.out.println(movie);
                } else if (choice2 == 2) {
                    System.out.println("Insert title:");
                    String title = scanner.nextLine();
                    Series series = new Series(title);
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Insert directors, type done if finished:");
                        String director = scanner.nextLine();
                        if (director.toLowerCase().compareTo("done") == 0)
                            ok1 = 1;
                        else
                            series.addDirector(director);
                    }
                    ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Insert actors, type done if finished:");
                        String actor = scanner.nextLine();
                        if (actor.toLowerCase().compareTo("done") == 0)
                            ok1 = 1;
                        else
                            series.addActor(actor);
                    }
                    ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Insert genres, type done if finished:");
                        String genre = scanner.nextLine();
                        if (genre.toLowerCase().compareTo("done") == 0)
                            ok1 = 1;
                        else {
                            if (Genre.find(genre) != null)
                                series.addGenre(genre);
                            else
                                System.out.println("Inexistent genre!");
                        }
                    }
                    System.out.println("Insert series summary:");
                    String summary = scanner.nextLine();
                    series.addSummary(summary);
                    System.out.println("Insert series release year:");
                    int ryear = 0;
                    validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number between 1888 and 2023: ");
                            ryear = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                        if (ryear < 1888 || ryear > 2023)
                            validInput = false;
                    } while (!validInput);
                    series.addYear(ryear);
                    System.out.println("Insert number of seasons:");
                    int ns = 0;
                    validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number: ");
                            ns = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                    } while (!validInput);
                    series.addnoSeas(ns);
                    for (int i = 1; i <= series.no_seasons; i++) {
                        int ok2 = 0;
                        List<Episode> epi = new ArrayList<>();
                        while (ok2 == 0) {
                            System.out.println("Insert episode name, done if finished:");
                            String epiname = scanner.nextLine();
                            if (epiname.toLowerCase().compareTo("done") == 0)
                                ok2 = 1;
                            else {
                                System.out.println("Insert episode length:");
                                String epilen = scanner.nextLine();
                                Episode episode = new Episode(epiname, epilen);
                                epi.add(episode);
                            }
                        }
                        series.addSeason("Season " + i, epi);
                    }
                    productions.add(series);
                    if (user instanceof Staff<?> && user.getUserType().compareTo(AccountType.ADMIN) != 0) {
                        ((Staff) user).getAdded().add(series);
                        user.calculateExperienceaddprod();
                    } else
                        Admin.admincontrib.add(series);
                    System.out.println(series);
                }
            } else if (choice == 2) {
                System.out.println("Insert Actor name:");
                String name = scanner.nextLine();
                System.out.println("Insert Actor biography:");
                String bio = scanner.nextLine();
                Actor actor = new Actor(name, bio);
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Insert a production the actor played in, done if finished");
                    String play = scanner.nextLine();
                    if (play.toLowerCase().compareTo("done") == 0)
                        ok1 = 1;
                    else {
                        String type = "";
                        int ok2 = 0;
                        while (ok2 == 0) {
                            System.out.println("Insert type: Movie or Series");
                            type = scanner.nextLine();
                            if (type.toLowerCase().compareTo("Movie".toLowerCase()) == 0 || type.toLowerCase().compareTo("Series".toLowerCase()) == 0)
                                ok2 = 1;
                            else
                                System.out.println("Enter valid production type!");
                        }
                        actor.addMovie(play, type);
                    }
                }
                actors.add(actor);
                if (user instanceof Staff<?> && user.getUserType().compareTo(AccountType.ADMIN) != 0) {
                    ((Staff) user).getAdded().add(actor);
                    user.calculateExperienceaddprod();
                } else
                    Admin.admincontrib.add(actor);
                System.out.println(actor);
            } else if (choice == 3) {
                if (user instanceof Staff<?> && user.getUserType().compareTo(AccountType.ADMIN) != 0) {
                    System.out.println(((Staff<?>) user).getAdded());
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Introduce the name of a production or actor you want to delete");
                        String prod = scanner.nextLine();
                        Iterator<Object> iterator = (Iterator<Object>) ((Staff<?>) user).getAdded().iterator();
                        while (iterator.hasNext()) {
                            Object aux = iterator.next();

                            if (aux instanceof Production && ((Production) aux).title.equalsIgnoreCase(prod)) {
                                productions.remove(aux);
                                iterator.remove();
                                ok1 = 1;
                            } else if (aux instanceof Actor && ((Actor) aux).name.equalsIgnoreCase(prod)) {
                                actors.remove(aux);
                                iterator.remove();
                                ok1 = 1;
                            }
                        }
                        if (ok1 == 0)
                            System.out.println("Inexistent production or actor!");
                    }
                } else if (user instanceof Admin) {
                    System.out.println(Admin.admincontrib);
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("Introduce the name of a production or actor you want to delete");
                        String prod = scanner.nextLine();
                        Iterator<Object> iterator = Admin.admincontrib.iterator();
                        while (iterator.hasNext()) {
                            Object aux = iterator.next();

                            if (aux instanceof Production && ((Production) aux).title.equals(prod)) {
                                productions.remove(aux);
                                iterator.remove();
                                ok1 = 1;
                            } else if (aux instanceof Actor && ((Actor) aux).name.equals(prod)) {
                                actors.remove(aux);
                                iterator.remove();
                                ok1 = 1;
                            }
                        }
                        if (ok1 == 0)
                            System.out.println("Inexistent production!");
                    }
                }
            } else if (choice == 4) {
                ok = 1;
            } else
                System.out.println("Invalid number!");
        }
    }

    public void seeRequests(User user, Scanner scanner) {
        if (user instanceof Staff<?> && user.getUserType().compareTo(AccountType.ADMIN) != 0) {
            if (!user.getRequestList().isEmpty()) {
                int ok = 0;
                while (ok == 0) {
                    System.out.println(user.getRequestList());
                    System.out.println("Choose a request. After you solve it, press delete and enter it's number.");
                    System.out.println("\t1) Delete a request");
                    System.out.println("\t2) Exit");
                    int choice = 0;
                    boolean validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number: ");
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                    } while (!validInput);
                    if (choice == 1) {
                        if (!user.getRequestList().isEmpty()) {
                            int ok1 = 0;
                            while (ok1 == 0) {
                                System.out.print("Choose a number between 1 and ");
                                System.out.println(user.getRequestList().size());
                                int nr = 0;
                                validInput = false;
                                do {
                                    try {
                                        System.out.print("Enter a number: ");
                                        nr = scanner.nextInt();
                                        scanner.nextLine();
                                        validInput = true;
                                    } catch (InputMismatchException e) {
                                        System.out.println("Insert a number!");
                                        scanner.nextLine();
                                    }
                                } while (!validInput);
                                if (nr >= 1 && nr <= user.getRequestList().size()) {
                                    Request request = (Request) user.getRequestList().remove(nr - 1);
                                    request.notifyObservers("Request resolved!");
                                    User user1 = getUserusername(request.getUsername());
                                    user1.calculateExperienceissue();
                                    ok1 = 1;
                                } else
                                    System.out.println("Number not in range!");
                            }
                        } else
                            System.out.println("List is empty!");
                    } else if (choice == 2)
                        ok = 1;
                    else
                        System.out.println("Invalid number!");
                }
            } else
                System.out.println("There are no requests");
        } else if (user instanceof Admin) {
            if (!Admin.RequestsHolder.requestList.isEmpty()) {
                int ok = 0;
                while (ok == 0) {
                    System.out.println(Admin.RequestsHolder.requestList);
                    System.out.println("Choose a request. After you solve it, press delete and enter it's number.");
                    System.out.println("\t1) Delete a request");
                    System.out.println("\t2) Exit");
                    int choice = 0;
                    boolean validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number: ");
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                    } while (!validInput);
                    if (choice == 1) {
                        if (!Admin.RequestsHolder.requestList.isEmpty()) {
                            int ok1 = 0;
                            while (ok1 == 0) {
                                System.out.print("Choose a number between 1 and ");
                                System.out.println(Admin.RequestsHolder.requestList.size());
                                int nr = 0;
                                validInput = false;
                                do {
                                    try {
                                        System.out.print("Enter a number: ");
                                        nr = scanner.nextInt();
                                        scanner.nextLine();
                                        validInput = true;
                                    } catch (InputMismatchException e) {
                                        System.out.println("Insert a number!");
                                        scanner.nextLine();
                                    }
                                } while (!validInput);
                                if (nr >= 1 && nr <= Admin.RequestsHolder.requestList.size()) {
                                    Request request = Admin.RequestsHolder.requestList.remove(nr - 1);
                                    request.notifyObservers("Request resolved!");
                                    User user1 = getUserusername(request.getUsername());
                                    user1.calculateExperienceissue();
                                    ok1 = 1;
                                } else
                                    System.out.println("Number not in range!");
                            }
                        }
                    } else if (choice == 2)
                        ok = 1;
                    else
                        System.out.println("Invalid number!");
                }
            } else
                System.out.println("There are no requests");
        }
    }

    public void addUpdate(Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            System.out.println("\t1) Update Actor details");
            System.out.println("\t2) Update Production details");
            System.out.println("\t3) Exit");
            int ch = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    ch = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (ch == 1) {
                System.out.println(actors);
                Actor actor = null;
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter an actor's name:");
                    String act = scanner.nextLine();
                    for (Actor aux : actors)
                        if (aux.name.toLowerCase().compareTo(act.toLowerCase()) == 0) {
                            ok1 = 1;
                            actor = aux;
                        }
                    if (ok1 == 0)
                        System.out.println("Inexistent actor");
                }
                ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter a field you want to modify:");
                    String field = scanner.nextLine();
                    if (field.toLowerCase().compareTo("name") == 0) {
                        System.out.println("Enter updated name:");
                        String name = scanner.nextLine();
                        actor.addName(name);
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("bio") == 0) {
                        System.out.println("Enter updated bio:");
                        String bio = scanner.nextLine();
                        actor.addBio(bio);
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("roles") == 0) {
                        int ok2 = 0;
                        while (ok2 == 0) {
                            System.out.println("\t1) Add production");
                            System.out.println("\t2) Remove production");
                            System.out.println("\t3) Exit");
                            int a = 0;
                            validInput = false;
                            do {
                                try {
                                    System.out.print("Enter a number: ");
                                    a = scanner.nextInt();
                                    scanner.nextLine();
                                    validInput = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Insert a number!");
                                    scanner.nextLine();
                                }
                            } while (!validInput);
                            if (a == 1) {
                                System.out.println("Enter production name");
                                String prod = scanner.nextLine();
                                String type = "";
                                int ok3 = 0;
                                while (ok3 == 0) {
                                    System.out.println("Insert type: Movie or Series");
                                    type = scanner.nextLine();
                                    if (type.compareTo("Movie") == 0 || type.compareTo("Series") == 0)
                                        ok3 = 1;
                                    else
                                        System.out.println("Enter valid production type!");
                                }
                                actor.addMovie(prod, type);
                            } else if (a == 2) {
                                int ok3 = 0;
                                while (ok3 == 0) {
                                    System.out.println(actor.roles);
                                    System.out.print("Choose a role to delete(number from 1 to ");
                                    System.out.print(actor.roles.size());
                                    System.out.println("): ");
                                    int nr = 0;
                                    validInput = false;
                                    do {
                                        try {
                                            System.out.print("Enter a number: ");
                                            nr = scanner.nextInt();
                                            scanner.nextLine();
                                            validInput = true;
                                        } catch (InputMismatchException e) {
                                            System.out.println("Insert a number!");
                                            scanner.nextLine();
                                        }
                                    } while (!validInput);
                                    if (nr >= 1 && nr <= actor.roles.size()) {
                                        ok3 = 1;
                                        actor.roles.remove(nr - 1);
                                    } else
                                        System.out.println("invalid number!");
                                }
                            } else if (a == 3)
                                ok2 = 1;
                            else
                                System.out.println("Invalid number!");
                        }
                        ok1 = 1;
                    } else
                        System.out.println("Inexistent field");
                }
                ok = 1;
            } else if (ch == 2) {
                System.out.println(productions);
                Production p = null;
                int ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter production name:");
                    String prod = scanner.nextLine();
                    if (getProduction(prod) != null) {
                        ok1 = 1;
                        p = getProduction(prod);
                    }
                    if (ok1 == 0)
                        System.out.println("Not a valid production");
                }
                ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter the field you would like to modify:");
                    String field = scanner.nextLine();
                    if (field.toLowerCase().compareTo("title") == 0) {
                        System.out.println("Enter new title:");
                        String title = scanner.nextLine();
                        p.title = title;
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("directors") == 0) {
                        int ok2 = 0;
                        while (ok2 == 0) {
                            System.out.println("\t1) Add director");
                            System.out.println("\t2) Remove director");
                            System.out.println("\t3) Exit");
                            int nr = 0;
                            validInput = false;
                            do {
                                try {
                                    System.out.print("Enter a number: ");
                                    nr = scanner.nextInt();
                                    scanner.nextLine();
                                    validInput = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Insert a number!");
                                    scanner.nextLine();
                                }
                            } while (!validInput);
                            if (nr == 1) {
                                System.out.println("\t1) Enter director:");
                                String dir = scanner.nextLine();
                                p.directors.add(dir);
                                ok2 = 1;
                            } else if (nr == 2) {
                                System.out.println(p.directors);
                                int ok3 = 0;
                                while (ok3 == 0) {
                                    System.out.println("\t Enter number of the director to delete:");
                                    int nr2 = 0;
                                    validInput = false;
                                    do {
                                        try {
                                            System.out.print("Enter a number: ");
                                            nr2 = scanner.nextInt();
                                            scanner.nextLine();
                                            validInput = true;
                                        } catch (InputMismatchException e) {
                                            System.out.println("Insert a number!");
                                            scanner.nextLine();
                                        }
                                    } while (!validInput);
                                    if (nr2 >= 1 && nr2 <= p.directors.size()) {
                                        p.directors.remove(nr2 - 1);
                                        ok3 = 1;
                                    } else
                                        System.out.println("Invalid number!");
                                }
                                ok2 = 1;
                            } else if (nr == 3)
                                ok2 = 1;
                            else
                                System.out.println("Invalid number!");
                        }
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("actors") == 0) {
                        int ok2 = 0;
                        while (ok2 == 0) {
                            System.out.println("\t1) Add actor");
                            System.out.println("\t2) Remove actor");
                            System.out.println("\t3) Exit");
                            int nr = 0;
                            validInput = false;
                            do {
                                try {
                                    System.out.print("Enter a number: ");
                                    nr = scanner.nextInt();
                                    scanner.nextLine();
                                    validInput = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Insert a number!");
                                    scanner.nextLine();
                                }
                            } while (!validInput);
                            if (nr == 1) {
                                System.out.println("\t1) Enter actor:");
                                String act = scanner.nextLine();
                                p.actors.add(act);
                                ok2 = 1;
                            } else if (nr == 2) {
                                System.out.println(p.actors);
                                int ok3 = 0;
                                while (ok3 == 0) {
                                    System.out.println("\t Enter number of the actor to delete:");
                                    int nr2 = 0;
                                    validInput = false;
                                    do {
                                        try {
                                            System.out.print("Enter a number: ");
                                            nr2 = scanner.nextInt();
                                            scanner.nextLine();
                                            validInput = true;
                                        } catch (InputMismatchException e) {
                                            System.out.println("Insert a number!");
                                            scanner.nextLine();
                                        }
                                    } while (!validInput);
                                    if (nr2 >= 1 && nr2 <= p.actors.size()) {
                                        p.actors.remove(nr2 - 1);
                                        ok3 = 1;
                                    } else
                                        System.out.println("Invalid number!");
                                }
                                ok2 = 1;
                            } else if (nr == 3)
                                ok2 = 1;
                            else
                                System.out.println("Invalid number!");
                        }
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("genres") == 0) {
                        int ok2 = 0;
                        while (ok2 == 0) {
                            System.out.println("\t1) Add genre");
                            System.out.println("\t2) Remove genre");
                            System.out.println("\t3) Exit");
                            int nr = 0;
                            validInput = false;
                            do {
                                try {
                                    System.out.print("Enter a number: ");
                                    nr = scanner.nextInt();
                                    scanner.nextLine();
                                    validInput = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Insert a number!");
                                    scanner.nextLine();
                                }
                            } while (!validInput);
                            if (nr == 1) {
                                int ok3 = 0;
                                while (ok3 == 0) {
                                    System.out.println("\t Enter genre:");
                                    String gen = scanner.nextLine();
                                    if (Genre.find(gen) != null) {
                                        ok3 = 1;
                                        p.genres.add(Genre.valueOf(gen));
                                    } else
                                        System.out.println("Inexistent genre!");
                                }
                                ok2 = 1;
                            } else if (nr == 2) {
                                System.out.println(p.genres);
                                int ok3 = 0;
                                while (ok3 == 0) {
                                    System.out.println("\t Enter number of the genre to delete:");
                                    int nr2 = 0;
                                    validInput = false;
                                    do {
                                        try {
                                            System.out.print("Enter a number: ");
                                            nr2 = scanner.nextInt();
                                            scanner.nextLine();
                                            validInput = true;
                                        } catch (InputMismatchException e) {
                                            System.out.println("Insert a number!");
                                            scanner.nextLine();
                                        }
                                    } while (!validInput);
                                    if (nr2 >= 1 && nr2 <= p.genres.size()) {
                                        p.genres.remove(nr2 - 1);
                                        ok3 = 1;
                                    } else
                                        System.out.println("Invalid number!");
                                }
                                ok2 = 1;
                            } else if (nr == 3)
                                ok2 = 1;
                            else
                                System.out.println("Invalid number!");
                        }
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("summary") == 0) {
                        System.out.println("Enter new summary:");
                        String sum = scanner.nextLine();
                        p.summary = sum;
                        ok1 = 1;
                    } else if (field.toLowerCase().compareTo("year") == 0) {
                        if (p instanceof Movie) {
                            int ok2 = 0;
                            int year = 0;
                            while (ok2 == 0) {
                                System.out.println("Enter new year between 1888 and 2023:");
                                year = 0;
                                validInput = false;
                                do {
                                    try {
                                        System.out.print("Enter a number: ");
                                        year = scanner.nextInt();
                                        scanner.nextLine();
                                        validInput = true;
                                    } catch (InputMismatchException e) {
                                        System.out.println("Insert a number!");
                                        scanner.nextLine();
                                    }
                                } while (!validInput);
                                if (year >= 1888 && year <= 2023)
                                    ok2 = 1;
                                else
                                    System.out.println("Invalid year!");
                            }
                            ((Movie) p).year = year;
                            ok1 = 1;
                        } else
                            System.out.println("Inexistent field!");
                    } else if (field.toLowerCase().compareTo("length") == 0) {
                        if (p instanceof Movie) {
                            System.out.println("Enter new length:");
                            String len = scanner.nextLine();
                            ((Movie) p).length = len;
                            ok1 = 1;
                        } else
                            System.out.println("Inexistent field!");
                    } else if (field.toLowerCase().compareTo("release_year") == 0) {
                        if (p instanceof Series) {
                            int ok2 = 0;
                            int year = 0;
                            while (ok2 == 0) {
                                System.out.println("Enter new year:");
                                year = 0;
                                validInput = false;
                                do {
                                    try {
                                        System.out.print("Enter a number: ");
                                        year = scanner.nextInt();
                                        scanner.nextLine();
                                        validInput = true;
                                    } catch (InputMismatchException e) {
                                        System.out.println("Insert a number!");
                                        scanner.nextLine();
                                    }
                                } while (!validInput);
                                if (year >= 1888 && year <= 2023)
                                    ok2 = 1;
                                else
                                    System.out.println("Invalid year!");
                            }
                            ((Series) p).release_year = year;
                            ok1 = 1;
                        } else
                            System.out.println("Inexistent field!");
                    } else if (field.toLowerCase().compareTo("seasons") == 0) {
                        if (p instanceof Series) {
                            int ok2 = 0;
                            while (ok2 == 0) {
                                System.out.println(((Series) p).seasons);
                                System.out.println("Choose an action:");
                                System.out.println("\t1) Add season");
                                System.out.println("\t2) Remove season");
                                System.out.println("\t3) Add episode");
                                System.out.println("\t4) Remove episode");
                                System.out.println("\t5) Exit");
                                int nr = 0;
                                validInput = false;
                                do {
                                    try {
                                        System.out.print("Enter a number: ");
                                        nr = scanner.nextInt();
                                        scanner.nextLine();
                                        validInput = true;
                                    } catch (InputMismatchException e) {
                                        System.out.println("Insert a number!");
                                        scanner.nextLine();
                                    }
                                } while (!validInput);
                                if (nr == 1) {
                                    int ok3 = 0;
                                    List<Episode> epi = new ArrayList<>();
                                    while (ok3 == 0) {
                                        System.out.println("Insert episode name, done if finished:");
                                        String epiname = scanner.nextLine();
                                        if (epiname.toLowerCase().compareTo("done") == 0)
                                            ok3 = 1;
                                        else {
                                            System.out.println("Insert episode length:");
                                            String epilen = scanner.nextLine();
                                            Episode episode = new Episode(epiname, epilen);
                                            epi.add(episode);
                                        }
                                    }
                                    int no = ((Series) p).no_seasons;
                                    no++;
                                    ((Series) p).seasons.put("Season " + no, epi);
                                    ((Series) p).no_seasons++;
                                    ok2 = 1;
                                } else if (nr == 2) {
                                    int ok3 = 0;
                                    int seas = 0;
                                    while (ok3 == 0) {
                                        System.out.println(((Series) p).seasons);
                                        System.out.println("Enter season to remove:");
                                        seas = 0;
                                        validInput = false;
                                        do {
                                            try {
                                                System.out.print("Enter a number: ");
                                                seas = scanner.nextInt();
                                                scanner.nextLine();
                                                validInput = true;
                                            } catch (InputMismatchException e) {
                                                System.out.println("Insert a number!");
                                                scanner.nextLine();
                                            }
                                        } while (!validInput);
                                        if (seas >= 1 && seas <= ((Series) p).no_seasons)
                                            ok3 = 1;
                                        else
                                            System.out.println("Invalid number!");
                                    }
                                    ((Series) p).seasons.remove("Season " + seas);
                                    ((Series) p).no_seasons--;
                                    ok2 = 1;
                                } else if (nr == 3) {
                                    int ok3 = 0;
                                    int seas = 0;
                                    while (ok3 == 0) {
                                        System.out.println("Enter season in which episode is to be added:");
                                        seas = 0;
                                        validInput = false;
                                        do {
                                            try {
                                                System.out.print("Enter a number: ");
                                                seas = scanner.nextInt();
                                                scanner.nextLine();
                                                validInput = true;
                                            } catch (InputMismatchException e) {
                                                System.out.println("Insert a number!");
                                                scanner.nextLine();
                                            }
                                        } while (!validInput);
                                        if (seas >= 1 && seas <= ((Series) p).no_seasons)
                                            ok3 = 1;
                                        else
                                            System.out.println("Invalid season number!");
                                    }
                                    System.out.println("Enter episode name:");
                                    String epin = scanner.nextLine();
                                    System.out.println("Enter episode length:");
                                    String epil = scanner.nextLine();
                                    Episode episode = new Episode(epin, epil);
                                    ((Series) p).getSeason(seas).add(episode);
                                    ok2 = 1;
                                } else if (nr == 4) {
                                    int ok3 = 0;
                                    int seas = 0;
                                    int epi = 0;
                                    while (ok3 == 0) {
                                        System.out.println("Enter season in which episode is to be deleted:");
                                        seas = 0;
                                        validInput = false;
                                        do {
                                            try {
                                                System.out.print("Enter a number: ");
                                                seas = scanner.nextInt();
                                                scanner.nextLine();
                                                validInput = true;
                                            } catch (InputMismatchException e) {
                                                System.out.println("Insert a number!");
                                                scanner.nextLine();
                                            }
                                        } while (!validInput);
                                        if (seas >= 1 && seas <= ((Series) p).no_seasons)
                                            ok3 = 1;
                                        else
                                            System.out.println("Invalid season number!");
                                    }
                                    ok3 = 0;
                                    while (ok3 == 0) {
                                        System.out.println("Enter episode number:");
                                        epi = 0;
                                        validInput = false;
                                        do {
                                            try {
                                                System.out.print("Enter a number: ");
                                                epi = scanner.nextInt();
                                                scanner.nextLine();
                                                validInput = true;
                                            } catch (InputMismatchException e) {
                                                System.out.println("Insert a number!");
                                                scanner.nextLine();
                                            }
                                        } while (!validInput);
                                        if (epi >= 1 && epi <= ((Series) p).getSeason(seas).size())
                                            ok3 = 1;
                                        else
                                            System.out.println("Invalid season number!");
                                    }
                                    ((Series) p).getSeason(seas).remove(epi - 1);
                                    ok2 = 1;
                                } else if (nr == 5)
                                    ok2 = 1;
                                else
                                    System.out.println("Invalid number!");
                            }
                            ok1 = 1;
                        } else
                            System.out.println("Inexistent field!");
                    } else
                        System.out.println("Inexistent field!");
                }
                ok = 1;
            } else if (ch == 3)
                ok = 1;
            else
                System.out.println("Invalid number!");
        }
    }

    public void addremUser(Scanner scanner) {
        int ok = 0;
        while (ok == 0) {
            System.out.println("\t1) Add user");
            System.out.println("\t2) Remove user");
            System.out.println("\t3) Exit");
            int choice = 0;
            boolean validInput = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!validInput);
            if (choice == 1) {
                System.out.println("Insert first name:");
                String fname = scanner.nextLine();
                System.out.println("Insert last name:");
                String lname = scanner.nextLine();
                String name = fname + " " + lname;
                Random random = new Random();
                int randomNumber = random.nextInt(9900) + 100;
                fname = fname.toLowerCase();
                lname = lname.toLowerCase();
                String username = fname + "_" + lname + "_" + randomNumber;
                String password = genPass();
                int ok1 = 0;
                String email = "";
                while (ok1 == 0) {
                    System.out.println("Enter email:");
                    email = scanner.nextLine();
                    int index1 = email.indexOf("@");
                    int index2 = email.indexOf(".com");
                    if (index1 != -1 && index2 != -1)
                        ok1 = 1;
                    else
                        System.out.println("Not a valid email address!");
                }
                Credentials credentials = new Credentials(email, password);
                System.out.println("Insert country:");
                String country = scanner.nextLine();
                ok1 = 0;
                int age = 0;
                while (ok1 == 0) {
                    System.out.println("Enter age between 10 and 100:");
                    age = 0;
                    validInput = false;
                    do {
                        try {
                            System.out.print("Enter a number: ");
                            age = scanner.nextInt();
                            scanner.nextLine();
                            validInput = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Insert a number!");
                            scanner.nextLine();
                        }
                    } while (!validInput);
                    if (age >= 10 && age <= 100)
                        ok1 = 1;
                    else
                        System.out.println("Not a valid age!");
                }
                ok1 = 0;
                String gender = "";
                while (ok1 == 0) {
                    System.out.println("Enter a gender(Male or Female):");
                    gender = scanner.nextLine();
                    if (gender.toLowerCase().compareTo("Male".toLowerCase()) == 0 || gender.toLowerCase().compareTo("Female".toLowerCase()) == 0)
                        ok1 = 1;
                    else
                        System.out.println("Not a valid gender!");
                }
                LocalDate date = null;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter day of birth(yyyy-MM-dd):");
                    String dat = scanner.nextLine();
                    try {
                        date = LocalDate.parse(dat, dateFormatter);
                        ok1 = 1;
                    } catch (Exception e) {
                        System.out.println("Not a valid date!");
                    }
                }
                User.Information information = User.Information.builder()
                        .setName(name)
                        .setAge(age)
                        .setCountry(country)
                        .setGender(gender)
                        .setCredentials(credentials)
                        .setBirthday(date)
                        .build();
                String type = "";
                ok1 = 0;
                while (ok1 == 0) {
                    System.out.println("Enter user type:");
                    type = scanner.nextLine();
                    if (type.toLowerCase().compareTo("Admin".toLowerCase()) == 0 || type.toLowerCase().compareTo("Regular".toLowerCase()) == 0 || type.toLowerCase().compareTo("Contributor".toLowerCase()) == 0)
                        ok1 = 1;
                    else
                        System.out.println("Not a valid user type!");
                }
                UserFactory userFactory = new UserFactory();
                User user = userFactory.getUser(type);
                user.setUsername(username);
                user.setUserInformation(information);
                user.setUserType(type);
                users.add(user);
                ok = 1;
            } else if (choice == 2) {
                for (User aux : users)
                    System.out.println(aux.getUsername());
                int ok1 = 0;
                User user = null;
                while (ok1 == 0) {
                    System.out.println("Enter username of user to delete");
                    String uname = scanner.nextLine();
                    if (getUserusername(uname) != null) {
                        ok1 = 1;
                        user = getUserusername(uname);
                    } else
                        System.out.println("Inexistent user!");
                }
                for (Production aux : productions) {
                    Iterator<Rating> iterator = aux.ratings.iterator();
                    while (iterator.hasNext()) {
                        Rating r = iterator.next();
                        if (r.username.compareTo(user.getUsername()) == 0) {
                            aux.calculateMarkrem(r.rating);
                            iterator.remove();
                        }
                    }
                }
                if (user instanceof Contributor)
                    Admin.admincontrib.addAll(((Contributor) user).getAdded());
                users.remove(user);
                ok = 1;
            } else if (choice == 3)
                ok = 1;
            else
                System.out.println("Not a valid number!");
        }
    }

    public String genPass() {
        final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#%";
        final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i <= 6; i++)
            if (i % 2 == 0) {
                password.append(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
            } else {
                password.append(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
            }
        return password.toString();
    }
}
