public class Singleton {
    private static Singleton single;
    public IMDB app;

    private Singleton() {
        app = new IMDB();
    }

    public static Singleton getInstance() {
        if (single == null)
            single = new Singleton();
        return single;
    }
}
