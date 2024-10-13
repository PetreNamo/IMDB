import java.util.List;

public class Regular extends User implements RequestsManager {

    public Regular(Information userInformation, AccountType userType, String username, int experience) {
        super(userInformation, userType, username, experience);
    }

    public Regular() {
        super();
    }

    @Override
    public void addContrib(Production p) {

    }

    @Override
    public void addContrib(Actor a) {

    }

    @Override
    public List getRequestList() {
        return null;
    }

    @Override
    public void addRequest(Request r) {

    }

    @Override
    public void createRequest(Request r) {

    }

    @Override
    public void removeRequest(Request r) {

    }
}
