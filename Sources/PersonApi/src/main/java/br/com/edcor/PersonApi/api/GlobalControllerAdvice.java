package br.com.edcor.PersonApi.api;

import br.com.edcor.PersonApi.exceptions.FlowException;
import br.com.edcor.PersonApi.openapi.ErrorInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(FlowException.class)
    public ResponseEntity<ErrorInfo> handleFlowException(FlowException exception) {
        var status = exception.getStatus();
        var body = new ErrorInfo()
                .timestamp(LocalDateTime.now(ZoneId.of("UTC")).toString())
                .status(status.value())
                .error(exception.getError())
                .message(exception.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
