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
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 256)
    private String accountOwner;

    @NotNull
    private BigDecimal balance;

    @NotNull
    private BigDecimal restrictedLimit;

    private BigDecimal auxLimit;

    private BigDecimal interest_rate;


    public boolean checkMoneyAvailability2(BigDecimal amount) {
        long limitAux = getAuxLimit().longValue();
        long blc = getBalance().longValue();
        long amountlng = amount.longValue();
        if (blc >= 0) {
            if (amountlng <= blc) {
                blc = blc - amountlng;
                setBalance(new BigDecimal(blc));
                setAuxLimit(getRestrictedLimit());
                return true;
            } else {
                long blcPlusLimit = blc + limitAux;
                if (amountlng <= blcPlusLimit) {
                    long dif = amountlng - blc;
                    limitAux = limitAux - dif;
                    setAuxLimit(new BigDecimal(limitAux));
                    setBalance(new BigDecimal((dif * -1)));
                    return true;
                }
            }
        } else {
            if (limitAux >= amountlng) {
                limitAux = limitAux - amountlng;

                long neg = ((getRestrictedLimit().longValue() - getAuxLimit().longValue()) + amountlng);

                setAuxLimit(new BigDecimal(limitAux));
                setBalance(new BigDecimal(neg * -1));
                return true;
            }
        }

        return false;
    }

    public void moneyReceived(BigDecimal amount) {
        setBalance(getBalance().add(amount));
        if (getBalance().intValue() >= 0) {
            setAuxLimit(getRestrictedLimit());
        }
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
        final Account other = (Account) obj;
        return Objects.equals(this.id, other.id);
    }
}
