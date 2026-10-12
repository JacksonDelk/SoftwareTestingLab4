package lab4;

public class EmailFailureException extends RuntimeException {
    public EmailFailureException(String message){
        super("Email Failed!" + message);
    }
}
