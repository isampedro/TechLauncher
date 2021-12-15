package ar.edu.itba.paw.service;

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException(String s) {
        super(s);
    }

}
