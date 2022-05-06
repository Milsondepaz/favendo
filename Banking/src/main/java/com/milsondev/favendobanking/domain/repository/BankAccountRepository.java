/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.milsondev.favendobanking.domain.repository;

import com.milsondev.favendobanking.domain.model.BankAccount;
import java.math.BigDecimal;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Milson
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    BankAccount findByAccountOwner(String accountOwner);

    @Modifying
    @Transactional
    @Query(value = "update bank_account set interest_rate = ?1", nativeQuery = true)
    void updateInterestRates(BigDecimal interest_rate);

}
