/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.controller;

import com.milsondev.favendobanking.domain.model.BankAccount;
import com.milsondev.favendobanking.domain.repository.BankAccountRepository;
import com.milsondev.favendobanking.exceptionhandler.BusinessException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 *
 * @author Milson
 */
@RestController
@RequestMapping("/banking/aux")
@Tag(name = "Auxiliar Banking")
public class BankAuxController { 

    @Autowired
    private BankAccountRepository bankAccountRepository;
   
    @Operation(summary = "Get all accounts", responses = {
        @ApiResponse(description = "Get all accounts success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class)))
    })
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    @Operation(summary = "Get an account", responses = {
        @ApiResponse(description = "Get account success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class)))
    })
    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
    public ResponseEntity<BankAccount> getAccount(@PathVariable Long id) {
        if (!bankAccountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(id);
        BankAccount bankAccount = optionalBankAccount.get();
        return ResponseEntity.ok(bankAccount);
    }

    @Operation(summary = "Create, add or open a new an account", responses = {
        @ApiResponse(description = "create a new account success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class)))
    })
    @RequestMapping(value = "/accounts/new", method = RequestMethod.POST)
    public BankAccount createNewAccount(@Valid @RequestBody BankAccount bankAccount) {
        BankAccount existingAccount = bankAccountRepository.findByAccountOwner(bankAccount.getAccountOwner());
        if (existingAccount != null && !existingAccount.equals(bankAccount)) {
            throw new BusinessException("There is already a customer registered with this name");
        }
        
        return bankAccountRepository.save(bankAccount);
    }

    @Operation(summary = "Update an account", responses = {
        @ApiResponse(description = "Update account success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class)))
    })
    @RequestMapping(value = "/accounts/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BankAccount> update(@Valid @PathVariable Long id, @RequestBody BankAccount bankAccount) {
        if (!bankAccountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bankAccount.setId(id);
        
        BankAccount existingAccount = bankAccountRepository.findByAccountOwner(bankAccount.getAccountOwner());
        if (existingAccount != null && !existingAccount.equals(bankAccount)) {
            throw new BusinessException("There is already a customer registered with this name");
        }
        
        bankAccountRepository.save(bankAccount);
                
        return ResponseEntity.ok(bankAccount);
    }

    @Operation(summary = "Delete an account", responses = {
        @ApiResponse(description = "Delete account success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class))),
        @ApiResponse(description = "Account not found", responseCode = "409", content = @Content)
    })
    @RequestMapping(value = "/accounts/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bankAccountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bankAccountRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
