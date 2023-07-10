package com.tm.loanapi.service;

import com.tm.loanapi.database.LoanRepository;
import com.tm.loanapi.database.TransactionRepository;
import com.tm.loanapi.model.Loan;
import com.tm.loanapi.model.Transaction;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class LoanService {
    private final HikariDataSource hikariDataSource;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, hh:mm a");
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    TransactionRepository transactionRepository;

    public LoanService(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    private boolean isValid(String telephone) {
        return telephone.matches("0\\d{9}");
    }

    public String request(String telephone, Integer amount) {
        if (telephone != null && amount != null) {
            if (isValid(telephone)) {
                Loan lastLoan = loanRepository.findLast(telephone);
                if (lastLoan == null) {
                    Loan newLoan = new Loan(telephone, amount, System.currentTimeMillis());
                    Transaction loanRequestTransaction = new Transaction(telephone, amount, "Lending", newLoan.getTimestamp());
                    loanRepository.save(newLoan);
                    transactionRepository.save(loanRequestTransaction);
                    return telephone + " has been granted a loan of " + amount + " at " + sdf.format(new Date(newLoan.getTimestamp()));
                } else {
                    if (lastLoan.getBalance() > 0) {
                        return telephone + " has a pending loan taken on " + sdf.format(new Date(lastLoan.getTimestamp())) + ", the remaining balance is " + lastLoan.getBalance();
                    } else {
                        Loan newLoan = new Loan(telephone, amount, System.currentTimeMillis());
                        Transaction loanRequestTransaction = new Transaction(telephone, amount, "Lending", newLoan.getTimestamp());
                        loanRepository.save(newLoan);
                        transactionRepository.save(loanRequestTransaction);
                        return telephone + " has been granted a loan of " + amount + " at " + sdf.format(new Date(newLoan.getTimestamp()));
                    }
                }
            } else {
                return "Invalid telephone";
            }
        } else {
            return "Invalid input";
        }
    }
    public String repay(String telephone, Integer amount) {
        if (telephone != null && amount != null) {
            if (isValid(telephone)) {
                Loan lastLoan = loanRepository.findLast(telephone);
                if (lastLoan == null) {
                    return telephone + " has no pending loans";
                } else {
                    if (lastLoan.getBalance() < 1) {
                        return telephone + " has no pending loans";
                    } else {
                        if (amount > lastLoan.getBalance()) {
                            return "The repayment amount exceeds the remaining balance of " + lastLoan.getBalance();
                        } else {
                            lastLoan.setBalance(lastLoan.getBalance() - amount);
                            Transaction loanRepaymentTransaction = new Transaction(telephone, amount, "Repayment", System.currentTimeMillis());
                            loanRepository.save(lastLoan);
                            transactionRepository.save(loanRepaymentTransaction);
                            return telephone + " has made a loan repayment of " + amount + " at " + sdf.format(new Date(loanRepaymentTransaction.getTimestamp())) + ", remaining balance is " + lastLoan.getBalance();
                        }
                    }
                }
            } else {
                return "Invalid telephone";
            }
        } else {
            return "Invalid input";
        }
    }
    public String clear() {
        loanRepository.deleteOldClearedLoans();
        return "Old loans cleared";
    }
    public String dump() {
        Path sqlFile = Paths.get("C:\\dump\\" + System.currentTimeMillis() + ".sql");

        try {
            ByteArrayOutputStream stdErr = new ByteArrayOutputStream();
            OutputStream stdOut = new BufferedOutputStream(Files.newOutputStream(sqlFile, StandardOpenOption.CREATE_NEW));
            ExecuteWatchdog watchdog = new ExecuteWatchdog(TimeUnit.HOURS.toMillis(1));

            DefaultExecutor defaultExecutor = new DefaultExecutor();
            defaultExecutor.setWatchdog(watchdog);
            defaultExecutor.setStreamHandler(new PumpStreamHandler(stdOut, stdErr));

            CommandLine commandLine = new CommandLine("mysqldump");
            commandLine.addArgument("-u" + hikariDataSource.getUsername());
            commandLine.addArgument("-p" + hikariDataSource.getPassword());
            commandLine.addArgument("loan-api");


            int exitCode = defaultExecutor.execute(commandLine);
            if (defaultExecutor.isFailure(exitCode) && watchdog.killedProcess()) {
                return "Dump failed!";
            }
            return "Dump success!";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
