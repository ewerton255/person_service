package br.com.edcor.PersonApi.exceptions;

import br.com.edcor.PersonApi.openapi.ErrorTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class FlowException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowException.class);

    private ErrorTypeEnum error;
    private HttpStatus status;
    private String message;

    public FlowException(ErrorTypeEnum error, HttpStatus status, String message) {
        super(message);
        this.error = error;
        this.status = status;
        this.message = message;

        LOGGER.error("{} [status: {}, error: {}]", message, status, error);
    }

    public ErrorTypeEnum getError() {
        return error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
