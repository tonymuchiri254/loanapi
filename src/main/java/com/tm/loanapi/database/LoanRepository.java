package com.tm.loanapi.database;

import com.tm.loanapi.model.Loan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    Loan findLast(String telephone);

    @Modifying
    @Query("delete from Loan l where l.balance < 1 and l.timestamp <= ?1")
    void deleteOldClearedLoans(long start);
}
