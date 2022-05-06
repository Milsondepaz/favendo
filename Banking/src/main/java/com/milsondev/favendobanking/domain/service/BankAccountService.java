/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.domain.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.milsondev.favendobanking.exceptionhandler.BusinessException;
import com.milsondev.favendobanking.domain.model.BankAccount;
import com.milsondev.favendobanking.domain.repository.BankAccountRepository;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Milson
 */
@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InfoWithdraw {

        private String name;
        @NotNull
        private BigDecimal amount;
        @NotNull
        private Long id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InfoTransfer {

        @NotNull
        private Long idSource;
        @NotNull
        private BigDecimal amount;
        @NotNull
        private Long idDestination;
        private String beneficiary;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InfoInterestRate {
        @NotNull
        private BigDecimal interest_rate;
    }

    public BankAccount salvar(BankAccount bankAccount) {
        BankAccount existingAccount = bankAccountRepository.findByAccountOwner(bankAccount.getAccountOwner());
        if (existingAccount != null && !existingAccount.equals(bankAccount)) {
            throw new BusinessException("There is already a customer registered with this name");
        }
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount balance(Long id) {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(id);
        BankAccount bankAccount = optionalBankAccount.get();

        BankAccount nbk = new BankAccount();

        nbk.setBalance(bankAccount.getBalance());
        nbk.setAccountOwner(bankAccount.getAccountOwner());

        return nbk;
    }

    public InfoWithdraw withdrawMoney(InfoWithdraw withdraw) {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(withdraw.getId());
        BankAccount bankAccount = optionalBankAccount.get();

        if (!bankAccount.checkMoneyAvailability(bankAccount.getRestrictedLimit(), withdraw.getAmount(), bankAccount.getBalance())) {
            throw new BusinessException("You do not have enough balance or limit to withdraw the requested amount.");
        }

        // update balance
        bankAccount.setBalance(bankAccount.getBalance().subtract(withdraw.getAmount()));

        // update on database
        bankAccount = bankAccountRepository.save(bankAccount);

        var w = new InfoWithdraw();
        w.setName(bankAccount.getAccountOwner());
        w.setAmount(withdraw.getAmount());

        return w;
    }

    public InfoTransfer transferMoney(InfoTransfer t) {
        if (Objects.equals(t.getIdDestination(), t.getIdSource())) {
            throw new BusinessException("Invalid operation, account is the same.");
        }

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(t.getIdSource());
        BankAccount sourceBankAccount = optionalBankAccount.get();

        if (!sourceBankAccount.checkMoneyAvailability(sourceBankAccount.getRestrictedLimit(), t.getAmount(), sourceBankAccount.getBalance())) {
            throw new BusinessException("You do not have enough balance or limit to complete this operation.");
        }

        // update balance
        sourceBankAccount.setBalance(sourceBankAccount.getBalance().subtract(t.getAmount()));

        optionalBankAccount = bankAccountRepository.findById(t.getIdDestination());

        BankAccount destinationBankAccount = optionalBankAccount.get();

        destinationBankAccount.setBalance(destinationBankAccount.getBalance().add(t.getAmount()));

        bankAccountRepository.save(destinationBankAccount);

        bankAccountRepository.save(sourceBankAccount);

        InfoTransfer transferInfo = new InfoTransfer();

        transferInfo.setAmount(t.getAmount());
        transferInfo.setBeneficiary(destinationBankAccount.getAccountOwner());

        return transferInfo;
    }

    public void updateInterestRates(InfoInterestRate interest_rate) {                        
        bankAccountRepository.updateInterestRates(interest_rate.getInterest_rate());                
    }
}
