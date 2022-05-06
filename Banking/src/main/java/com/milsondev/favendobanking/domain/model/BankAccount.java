/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.milsondev.favendobanking.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Milson
 */
@Entity(name = "BankAccount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 256)
    private String accountOwner;

    @NotNull
    private BigDecimal balance;

    private BigDecimal restrictedLimit;

    private BigDecimal auxLimit;

    private BigDecimal interest_rate;

    public boolean withdrawMoney(BigDecimal amount) {

        // check the limit
        if ((amount.compareTo(restrictedLimit)) < 1) {

            // check if is checkings account
            setBalance(getBalance().subtract(amount));
            return true;

        }
        return false;
    }

    public boolean receiveMoney(BigDecimal amount) {
        setBalance(getBalance().add(amount));
        return false;
    }

    public boolean checkMoneyAvailability(BigDecimal limit, BigDecimal amount, BigDecimal balance) {
        long lim = limit.longValue();
        long amoun = amount.longValue();
        long bal = balance.longValue();

        return amoun <= lim;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BankAccount other = (BankAccount) obj;
        return Objects.equals(this.id, other.id);
    }
}
