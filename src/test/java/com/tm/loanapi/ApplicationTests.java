package com.tm.loanapi;

import com.tm.loanapi.database.LoanRepository;
import com.tm.loanapi.model.Loan;
import com.tm.loanapi.model.Transaction;
import com.tm.loanapi.service.LoanService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class ApplicationTests {

	@Autowired
	LoanService loanService;
	@Autowired
	LoanRepository loanRepository;
	String telephone="0703362565";
	String invalidtelephone="703362565";
	Integer amount=5000;
	Integer min_amount=200;
	Integer excess_amount=10000;
	String outstandingloanresponse;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, hh:mm a");
	@Test
	void nooutstandingloans(){
		Loan lastLoan = loanRepository.findLast(telephone);
		String expectedresponse=loanService.repay(telephone,amount);
		if (lastLoan == null) {
			outstandingloanresponse =  telephone + " has no pending loans";
		}
		if(lastLoan.getBalance() < 1){
			outstandingloanresponse =  telephone + " has no pending loans";
		}
		Assertions.assertEquals(expectedresponse,outstandingloanresponse);
	}
	@Test
	void loanrequestsuccess(){
		Loan newLoan = new Loan(telephone, amount, System.currentTimeMillis());
		String successloanrequest = telephone + " has been granted a loan of " + amount + " at " + sdf.format(new Date(newLoan.getTimestamp()));
		String loanrequestresponse=loanService.request(telephone,amount);
		Assertions.assertEquals(successloanrequest,loanrequestresponse);
	}
	@Test
	void pendingloanrequest(){
		Loan lastLoan = loanRepository.findLast(telephone);
		String pendingloanrequest = telephone + " has a pending loan taken on " + sdf.format(new Date(lastLoan.getTimestamp())) + ", the remaining balance is " + lastLoan.getBalance();
		String loanrequestresponse=loanService.request(telephone,amount);
		Assertions.assertEquals(pendingloanrequest,loanrequestresponse);
	}
	@Test
	void invalidtelephone(){
		String invalidtelephoneresponse = "Invalid telephone";
		String loanrequestresponse=loanService.request(invalidtelephone,amount);
		Assertions.assertEquals(invalidtelephoneresponse,loanrequestresponse);
	}
	@Test
	void repaysuccessful(){
		Transaction loanRepaymentTransaction = new Transaction(telephone, min_amount, "Repayment", System.currentTimeMillis());
		String loanrepaysuccess=loanService.repay(telephone,min_amount);
		Loan lastLoan = loanRepository.findLast(telephone);
		String expectedresponse=telephone + " has made a loan repayment of " + min_amount + " at " + sdf.format(new Date(loanRepaymentTransaction.getTimestamp())) + ", remaining balance is " + lastLoan.getBalance();
		Assertions.assertEquals(expectedresponse,loanrepaysuccess);
	}
	@Test
	void repayexcess(){
		String loanpayexcess=loanService.repay(telephone,excess_amount);
		Loan lastLoan = loanRepository.findLast(telephone);
		String expectedresponse="The repayment amount exceeds the remaining balance of " + lastLoan.getBalance();
		Assertions.assertEquals(expectedresponse,loanpayexcess);
	}

}
