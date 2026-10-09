package mg.itu.myframework.exception;

import jakarta.servlet.ServletException;

public class ParameterBindingException extends ServletException {
    public ParameterBindingException(String message) {
        super(message);
    }

    public ParameterBindingException(String message, Throwable cause) {
        super(message, cause);
    }
}