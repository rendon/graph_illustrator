package edu.inforscience;

@SuppressWarnings("serial")
public class InvalidOperationException extends Exception {
    public InvalidOperationException()
    {

    }

    public InvalidOperationException(String message)
    {
        super(message);
    }
}
