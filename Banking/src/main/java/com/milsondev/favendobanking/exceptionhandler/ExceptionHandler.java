/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.exceptionhandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Milson
 */
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler{
    
    @Autowired
    private MessageSource messageSource;
    

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusiness(BusinessException ex, WebRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        
        var problem = new Problem();
        problem.setStatus(status.value());
        problem.setTitle(ex.getMessage()); 
        problem.setDataTime(LocalDateTime.now()); 
        
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        
        var fields = new ArrayList<Problem.Field>();
        
        for (ObjectError error: ex.getBindingResult().getAllErrors()) {
            String name = ((FieldError)error).getField();
            String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());            
            fields.add(new Problem.Field(name, message));
        }
         
        var problem = new Problem();
        problem.setStatus(status.value());
        problem.setTitle("One or more fields are empty, please fill them out and try again"); 
        problem.setDataTime(LocalDateTime.now()); 
        problem.setFields(fields); 
        
        return super.handleExceptionInternal(ex, problem, headers, status, request); 
    }
    
    
    
    
    
    
}
