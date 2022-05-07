/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.domain.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.milsondev.favendobanking.exceptionhandler.BusinessException;
import com.milsondev.favendobanking.domain.model.Account;
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

    public Account salvar(Account bankAccount) {
        Account existingAccount = bankAccountRepository.findByAccountOwner(bankAccount.getAccountOwner());
        if (existingAccount != null && !existingAccount.equals(bankAccount)) {
            throw new BusinessException("There is already a customer registered with this name");
        }
        return bankAccountRepository.save(bankAccount);
    }

    public Account balance(Long id) {
        Optional<Account> optionalBankAccount = bankAccountRepository.findById(id);
        Account bankAccount = optionalBankAccount.get();

        Account nbk = new Account();

        nbk.setBalance(bankAccount.getBalance());
        nbk.setAccountOwner(bankAccount.getAccountOwner());

        return nbk;
    }

    public InfoWithdraw withdrawMoney(InfoWithdraw withdraw) {
        Optional<Account> optionalBankAccount = bankAccountRepository.findById(withdraw.getId());
        Account bankAccount = optionalBankAccount.get();
        if (withdraw.getAmount().intValue() < 0) {
            throw new BusinessException("Invalid operation.");
        }
        if (!bankAccount.checkMoneyAvailability2(withdraw.getAmount())) {
            throw new BusinessException("You do not have enough balance or limit to withdraw the requested amount.");
        }

        bankAccount = bankAccountRepository.save(bankAccount);

        var w = new InfoWithdraw();
        w.setName(bankAccount.getAccountOwner());
        w.setAmount(withdraw.getAmount());

        return w;
    }

    public InfoTransfer transferMoney(InfoTransfer t) {
        if (Objects.equals(t.getIdDestination(), t.getIdSource()) || t.getAmount().intValue() < 0) {
            throw new BusinessException("Invalid operation.");
        }

        Optional<Account> optionalBankAccount = bankAccountRepository.findById(t.getIdSource());
        Account sourceBankAccount = optionalBankAccount.get();

        if (!sourceBankAccount.checkMoneyAvailability2(t.getAmount())) {
            throw new BusinessException("You do not have enough balance or limit to complete this operation.");
        }

        optionalBankAccount = bankAccountRepository.findById(t.getIdDestination());

        Account destinationBankAccount = optionalBankAccount.get();

        destinationBankAccount.moneyReceived(t.getAmount());

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
