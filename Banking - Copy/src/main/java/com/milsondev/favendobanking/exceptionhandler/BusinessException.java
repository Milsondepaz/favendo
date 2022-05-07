/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.exceptionhandler;

/**
 *
 * @author Milson
 */
public class BusinessException extends RuntimeException{    
    private static final long serialVersionUID = 1L;    
    public BusinessException(String message) {        
        super(message);
    }    
}
