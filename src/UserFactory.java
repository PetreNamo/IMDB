public class UserFactory {
    public User getUser(String type) {
        if (AccountType.valueOf(type.toUpperCase()).compareTo(AccountType.ADMIN) == 0)
            return new Admin();
        if (AccountType.valueOf(type.toUpperCase()).compareTo(AccountType.CONTRIBUTOR) == 0)
            return new Contributor();
        if (AccountType.valueOf(type.toUpperCase()).compareTo(AccountType.REGULAR) == 0)
            return new Regular();
        return null;
    }
}
