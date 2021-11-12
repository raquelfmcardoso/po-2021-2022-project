package ggc.exceptions;

public class NoSuchPartnerException extends Exception {
    private static final long serialVersionUID = 202110251317L;

    private final String _id;

    public NoSuchPartnerException(String id) {
        _id = id;
    }

    public String getId() {
        return _id;
    }
}