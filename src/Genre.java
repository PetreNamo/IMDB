public enum Genre {
    Action, Adventure, Comedy, Drama, Horror, SF,
    Fantasy, Romance, Mystery, Thriller, Crime, Biography, War, Cooking;

    public static Genre find(String s) {
        for (Genre g : Genre.values())
            if (s.equalsIgnoreCase(g.name()))
                return g;
        return null;
    }
}
