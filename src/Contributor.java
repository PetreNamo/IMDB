public class Contributor extends Staff implements RequestsManager {

    public Contributor(Information userInformation, AccountType userType, String username, int experience) {
        super(userInformation, userType, username, experience);
    }

    public Contributor() {
        super();
    }

    @Override
    public void createRequest(Request r) {

    }

    @Override
    public void removeRequest(Request r) {

    }
}
