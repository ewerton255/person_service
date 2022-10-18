package br.com.edcor.PersonApi.exceptions;

import br.com.edcor.PersonApi.openapi.ErrorTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

@Getter
@Log4j2
@AllArgsConstructor
public class FlowException extends RuntimeException {

    private ErrorTypeEnum error;
    private HttpStatus status;
    private String message;

}
