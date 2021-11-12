package ggc.exceptions;

public class NotEnoughProductException extends Exception{
    private static final long serialVersionUID = 202111092223L;

    private final String _id;
    private final int _requested;
    private final int _available;

    public NotEnoughProductException(String id, int requested, int available) {
        _id = id;
        _requested = requested;
        _available = available;
    }

    public String getId() {
        return _id;
    }

    public int getRequested() {
        return _requested;
    }

    public int getAvailable() {
        return _available;
    }
}
