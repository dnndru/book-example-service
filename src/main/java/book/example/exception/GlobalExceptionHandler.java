package book.example.exception;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import book.example.payload.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        ResponseDto dto = new ResponseDto();
        dto.setTimestamp(new Date());
		dto.setStatus(HttpStatus.BAD_REQUEST.value());
		dto.setErrors(errors);
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ResponseDto> handleNotFoundException(BookNotFoundException ex) {
        ResponseDto dto = new ResponseDto();
        dto.setTimestamp(new Date());
		dto.setStatus(HttpStatus.NOT_FOUND.value());
		dto.setMessage(ex.getMessage());
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ResponseDto> handleGeneralExceptions(Exception ex) {
        ResponseDto dto = new ResponseDto();
        dto.setTimestamp(new Date());
		dto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		dto.setMessage(ex.getMessage());
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ResponseDto> handleRuntimeExceptions(RuntimeException ex) {
        ResponseDto dto = new ResponseDto();
        dto.setTimestamp(new Date());
		dto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		dto.setMessage(ex.getMessage());
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<ResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ResponseDto dto = new ResponseDto();
        // Check if the cause is an InvalidFormatException
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            
            // Check if the field causing the error is 'condition' and if the target type is BookCondition
            if (ife.getTargetType().isEnum() && ife.getPath().stream().anyMatch(ref -> "bookCondition".equals(ref.getFieldName()))) {
                dto.setMessage("Invalid value for field 'bookCondition'. Allowed values are: NEW, USED");
            }
        }
        
        if (dto.getMessage().isEmpty()) {
            // Fallback generic message if no specific field is found
        	dto.setMessage("Invalid request data");
        }
        
        dto.setTimestamp(new Date());
		dto.setStatus(HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}