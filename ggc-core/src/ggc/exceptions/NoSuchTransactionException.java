package ggc.exceptions;

public class NoSuchTransactionException extends Exception {
    private static final long serialVersionUID = 202111072046L;

    private final int _id;

    public NoSuchTransactionException(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }
}