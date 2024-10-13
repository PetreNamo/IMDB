import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Singleton singleton = Singleton.getInstance();
        singleton.app.run();
        Scanner scanner = new Scanner(System.in);
        User user = null;

        int end = 0;
        while (end == 0) {
            System.out.println("\t1) Start app");
            System.out.println("\t2) Exit");
            int ch = 0;
            boolean valid = false;
            do {
                try {
                    System.out.print("Enter a number: ");
                    ch = scanner.nextInt();
                    scanner.nextLine();
                    valid = true;
                } catch (InputMismatchException e) {
                    System.out.println("Insert a number!");
                    scanner.nextLine();
                }
            } while (!valid);
            if (ch == 1) {
                int login = 0;
                while (login == 0) {
                    System.out.println("Welcome back! Enter your Credentials!");
                    System.out.print("email: ");
                    String email = scanner.nextLine();
                    System.out.print("password: ");
                    String password = scanner.nextLine();
                    int ok = 0;
                    for (User aux : singleton.app.users) {
                        if (aux.getEmail().compareTo(email) == 0) {
                            ok = 1;
                            if (aux.getPassword().compareTo(password) == 0) {
                                System.out.print("Welcome back user ");
                                System.out.print(aux.getUsername());
                                System.out.println("!");
                                login = 1;
                                user = singleton.app.getUser(email);
                            } else
                                System.out.println("Parola gresita!");
                        }
                    }
                    if (ok == 0)
                        System.out.println("Utilizator inexistent. Incercati din nou");
                }
                System.out.print("Username: ");
                System.out.println(user.getUsername());
                System.out.print("User experience: ");
                System.out.println(user.getExperience());
                System.out.println("Choose action:");

                if (user.getUserType().compareTo(AccountType.REGULAR) == 0) {
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("\t1) View production details");
                        System.out.println("\t2) View actor details");
                        System.out.println("\t3) View notifications");
                        System.out.println("\t4) Search for actor/movie/series");
                        System.out.println("\t5) Add/Delete actor/movie/series to/from favorites");
                        System.out.println("\t6) Create/Delete request");
                        System.out.println("\t7) Add/Remove a review from a production");
                        System.out.println("\t8) Logout");
                        System.out.print("\t");
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
                            singleton.app.usershowProd(scanner);
                        } else if (choice == 2) {
                            singleton.app.usershowActors(scanner);
                        } else if (choice == 3) {
                            singleton.app.usershowNotifications(user);
                        } else if (choice == 4) {
                            singleton.app.userSearch(scanner);
                        } else if (choice == 5) {
                            singleton.app.userchangeFavorites(user, scanner);
                        } else if (choice == 6) {
                            singleton.app.userRequest(user, scanner);
                        } else if (choice == 7) {
                            singleton.app.userRating(user, scanner);
                        } else if (choice == 8)
                            ok1 = 1;
                        else
                            System.out.println("Invalid number!");
                    }
                } else if (user.getUserType().compareTo(AccountType.CONTRIBUTOR) == 0) {
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("\t1) View production details");
                        System.out.println("\t2) View actor details");
                        System.out.println("\t3) View notifications");
                        System.out.println("\t4) Search for actor/movie/series");
                        System.out.println("\t5) Add/Delete actor/movie/series to/from favorites");
                        System.out.println("\t6) Create/Delete request");
                        System.out.println("\t7) Add/Remove production/actor to/from system");
                        System.out.println("\t8) View and solve requests");
                        System.out.println("\t9) Update information about actor/production");
                        System.out.println("\t10) Logout");
                        System.out.print("\t");
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
                            singleton.app.usershowProd(scanner);
                        } else if (choice == 2) {
                            singleton.app.usershowActors(scanner);
                        } else if (choice == 3) {
                            singleton.app.usershowNotifications(user);
                        } else if (choice == 4) {
                            singleton.app.userSearch(scanner);
                        } else if (choice == 5) {
                            singleton.app.userchangeFavorites(user, scanner);
                        } else if (choice == 6) {
                            singleton.app.userRequest(user, scanner);
                        } else if (choice == 7) {
                            singleton.app.addRemove(user, scanner);
                        } else if (choice == 8) {
                            singleton.app.seeRequests(user, scanner);
                        } else if (choice == 9) {
                            singleton.app.addUpdate(scanner);
                        } else if (choice == 10) {
                            ok1 = 1;
                        } else
                            System.out.println("Invalid number!");
                    }
                } else if (user.getUserType().compareTo(AccountType.ADMIN) == 0) {
                    int ok1 = 0;
                    while (ok1 == 0) {
                        System.out.println("\t1) View production details");
                        System.out.println("\t2) View actor details");
                        System.out.println("\t3) View notifications");
                        System.out.println("\t4) Search for actor/movie/series");
                        System.out.println("\t5) Add/Delete actor/movie/series to/from favorites");
                        System.out.println("\t6) Add/Delete user");
                        System.out.println("\t7) Add/Remove production/actor to/from system");
                        System.out.println("\t8) View and solve requests");
                        System.out.println("\t9) Update information about actor/production");
                        System.out.println("\t10) Logout");
                        System.out.print("\t");
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
                            singleton.app.usershowProd(scanner);
                        } else if (choice == 2) {
                            singleton.app.usershowActors(scanner);
                        } else if (choice == 3) {
                            singleton.app.usershowNotifications(user);
                        } else if (choice == 4) {
                            singleton.app.userSearch(scanner);
                        } else if (choice == 5) {
                            singleton.app.userchangeFavorites(user, scanner);
                        } else if (choice == 6) {
                            singleton.app.addremUser(scanner);
                        } else if (choice == 7) {
                            singleton.app.addRemove(user, scanner);
                        } else if (choice == 8) {
                            singleton.app.seeRequests(user, scanner);
                        } else if (choice == 9) {
                            singleton.app.addUpdate(scanner);
                        } else if (choice == 10) {
                            ok1 = 1;
                        } else
                            System.out.println("Invalid number!");
                    }
                }
            } else if (ch == 2)
                end = 1;
            else
                System.out.println("Not a valid number!");
        }
    }
}
