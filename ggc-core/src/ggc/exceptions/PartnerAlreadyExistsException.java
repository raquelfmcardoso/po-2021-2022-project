package ggc.exceptions;

public class PartnerAlreadyExistsException extends Exception {
    private static final long serialVersionUID = 202110251249L;

    private final String _id;

    public PartnerAlreadyExistsException(String id) {
        _id = id;
    }

    public String getId() {
        return _id;
    }
}