import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Admin<T extends Comparable<T>> extends Staff<T> {
    public static SortedSet<Object> admincontrib = new TreeSet<>();

    public Admin(Information userInformation, AccountType userType, String username, int experience) {
        super(userInformation, userType, username, experience);
    }

    public Admin() {
        super();
    }

    public static class RequestsHolder {
        public static List<Request> requestList = new ArrayList<>();

        public void addRequest(Request r) {
            requestList.add(r);
        }

        public void removeRequest(Request r) {
            requestList.remove(r);
        }

        public List getList() {
            return requestList;
        }

        @Override
        public String toString() {
            return "RequestsHolder{" + requestList + "}";
        }
    }

}
