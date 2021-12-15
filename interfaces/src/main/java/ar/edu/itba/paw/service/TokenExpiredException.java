package ar.edu.itba.paw.service;

public class TokenExpiredException extends Exception{
    public TokenExpiredException(String s) {
        super(s);
    }

}
