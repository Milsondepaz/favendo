/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.controller;

import com.milsondev.favendobanking.domain.model.BankAccount;
import com.milsondev.favendobanking.domain.service.BankAccountService;
import com.milsondev.favendobanking.domain.repository.BankAccountRepository;
import com.milsondev.favendobanking.domain.service.BankAccountService.InfoInterestRate;
import com.milsondev.favendobanking.domain.service.BankAccountService.InfoTransfer;
import com.milsondev.favendobanking.domain.service.BankAccountService.InfoWithdraw;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 *
 * @author Milson
 */
@RestController
@RequestMapping("/banking")
@Tag(name = "Banking")
public class BankController {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankAccountService bankAccountService;

    // tratar erro de end-point invalido
    // tratar tmbm exception not found
    // tratar internal server error 500
    // 404
        
    //get balance for a customer
    @Operation(summary = "Get a balance", responses = {
        @ApiResponse(description = "Get balance success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    })
    @RequestMapping(value = "/accounts/balance/{id}", method = RequestMethod.GET)
    public ResponseEntity<BankAccount> getBalance(@PathVariable Long id) {
        
        if (!bankAccountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        var bank = bankAccountService.balance(id);
        return ResponseEntity.ok(bank);
    }

    //withdraw money
    @Operation(summary = "Withdraw money", responses = {
        @ApiResponse(description = "withdraw money success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = InfoWithdraw.class)))
    })
    @RequestMapping(value = "/accounts/withdrawmoney", method = RequestMethod.PUT)
    public ResponseEntity<BankAccountService.InfoWithdraw> withdrawMoney(@Valid @RequestBody InfoWithdraw withdraw) {
        if (!bankAccountRepository.existsById(withdraw.getId())) {
            return ResponseEntity.notFound().build();
        }
        var withdrawM = bankAccountService.withdrawMoney(withdraw);
        return ResponseEntity.ok(withdrawM);
    }

    //transfer money
    @Operation(summary = "Transfer money", responses = {
        @ApiResponse(description = "Transfer money success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = InfoTransfer.class)))
    })
    @RequestMapping(value = "/accounts/transfermoney", method = RequestMethod.PUT)
    public ResponseEntity<BankAccountService.InfoTransfer> transferMoney(@Valid @RequestBody InfoTransfer trans) {
        
        if (!bankAccountRepository.existsById(trans.getIdSource()) || !bankAccountRepository.existsById(trans.getIdDestination())) {
            return ResponseEntity.notFound().build();
        }
        
        var trns = bankAccountService.transferMoney(trans);
        return ResponseEntity.ok(trns);
    }

    //interest rates
    @Operation(summary = "Set or update interest rates", responses = {
        @ApiResponse(description = "update interest rates success", responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = InfoInterestRate.class)))
    })
    @RequestMapping(value = "/accounts/interestrate", method = RequestMethod.PUT)
    public ResponseEntity<InfoInterestRate> interestRate(@Valid @RequestBody InfoInterestRate interest_rate) {                                      
        bankAccountService.updateInterestRates(interest_rate);                    
        return ResponseEntity.noContent().build();
    }
    
    
    
    
    
}
