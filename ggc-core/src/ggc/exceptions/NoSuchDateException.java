package ggc.exceptions;

public class NoSuchDateException extends Exception{

    private static final long serialVersionUID = 202110241513L;

    private final int _amount;


    public NoSuchDateException(int amount) {
        _amount = amount;
    }

    public int getAmount(){
        return _amount;
    }
}
