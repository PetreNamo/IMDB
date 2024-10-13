public class Rating {
    public String username;
    public int rating;
    public String comments;

    public Rating(String username, int rating, String comments) {
        this.username = username;
        this.rating = rating;
        this.comments = comments;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "{" +
                "username=" + username +
                ", rating=" + rating +
                ", comments=" + comments +
                '}';
    }
}
