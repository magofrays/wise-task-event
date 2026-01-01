package ru.leti.wise.task.event.service.grpc;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.exception.BusinessException;



@GrpcAdvice
@RequiredArgsConstructor
@Slf4j
public class GrpcErrorHandler {

    @GrpcExceptionHandler(BusinessException.class)
    public Status handleBusinessException(BusinessException e) {
        log.error("Business exception: {}", e.getMessage(), e);
        return processError(e);
    }

    public Status processError(BusinessException e) {
        String message = e.getMessage() == null ? "" : e.getMessage();
        return switch (e.getErrorCode()) {
            case NOT_FOUND -> Status.NOT_FOUND.withDescription(message);
            case BAD_REQUEST -> Status.UNAUTHENTICATED.withDescription(message);
            default -> Status.UNKNOWN;
        };
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleGenericException(Exception e) {
        log.error("Unexpected exception", e);
        return Status.UNKNOWN
                .withDescription("Internal server error")
                .withCause(e);
    }
}


