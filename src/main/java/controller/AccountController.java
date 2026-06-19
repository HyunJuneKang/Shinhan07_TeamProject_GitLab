package controller;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import model.Account;
import model.AccountStatus;
import repository.AccountRepositories;

public class AccountController {
    
    private final AccountRepositories accountRepository;
    private final Scanner scanner;

    // 의존성 주입 (팀원 A가 만든 Repository를 주입받음)
    public AccountController(AccountRepositories accountRepository) {
        this.accountRepository = accountRepository;
        this.scanner = new Scanner(System.in);
    }

    // 1. 계좌 개설 요청 처리
    public void createAccount() {
        System.out.println("\n--- 계좌 개설 ---");
        System.out.print("고객 ID(숫자) 입력: ");
        Long customerId = scanner.nextLong();
        System.out.print("최초 입금액 입력: ");
        BigDecimal balance = scanner.nextBigDecimal();

        // 롬복 빌더 패턴으로 객체 생성
        Account newAccount = Account.builder()
                .customerId(customerId)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .build();

        boolean isSuccess = accountRepository.save(newAccount);
        if (isSuccess) {
            System.out.println("계좌 개설 요청 성공! (시퀀스에 의해 계좌번호가 자동 발급됩니다.)");
        } else {
            System.out.println("계좌 개설 실패.");
        }
    }

    // 2. 계좌 단건 조회 요청 처리
    public void getAccount() {
        System.out.println("\n--- 계좌 정보 조회 ---");
        System.out.print("조회할 계좌번호 입력: ");
        String accountNo = scanner.next();

        Account account = accountRepository.findByAccountNo(accountNo);
        if (account != null) {
            System.out.println("====== 조회 결과 ======");
            System.out.println(account.toString()); // 롬복 @ToString 활용
        } else {
            System.out.println("해당 계좌를 찾을 수 없습니다.");
        }
    }

    // 3. 특정 고객의 전체 계좌 조회
    public void getCustomerAccounts() {
        System.out.println("\n--- 고객별 보유 계좌 목록 조회 ---");
        System.out.print("고객 ID(숫자) 입력: ");
        int customerId = scanner.nextInt();

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        if (accounts.isEmpty()) {
            System.out.println("해당 고객이 보유한 계좌가 없습니다.");
        } else {
            System.out.println("====== 보유 계좌 목록 ======");
            for (Account acc : accounts) {
                System.out.println(acc);
            }
        }
    }
    public void f_deposit() {
    	System.out.println("\n--- 계좌 입금 ---");
    	System.out.print("고객 ID(숫자) 입력: ");
    	int customerId = scanner.nextInt();
    	
    	List<Account> accounts = accountRepository.update(customerId);
    	if (accounts.isEmpty()) {
    		System.out.println("해당 고객이 보유한 계좌가 없습니다.");
    	} else {
    		System.out.println("====== 보유 계좌 목록 ======");
    		for (Account acc : accounts) {
    			System.out.println(acc);
    		}
    	}
    }
    public void f_withdraw() {
    	System.out.println("\n--- 계좌 출금 ---");
    	System.out.print("고객 ID(숫자) 입력: ");
    	String accountNo = scanner.nextLine();
    	
    	 Account account = accountRepository.findByAccountNo(accountNo);
    	if (accounts.isEmpty()) {
    		System.out.println("해당 고객이 보유한 계좌가 없습니다.");
    	} else {
    		System.out.println("====== 보유 계좌 목록 ======");
    		for (Account acc : accounts) {
    			System.out.println(acc);
    		}
    	}
    }
    public void f_transfer() {
    	System.out.println("\n--- 송금 발신 계좌  ---");
    	System.out.print("발신 고객 ID(숫자) 입력: ");
    	int fromAccId = scanner.nextInt();
    	System.out.println("\n--- 송금 수신 계좌 ---");
    	System.out.print("수신 고객 ID(숫자) 입력: ");
    	int toAccId = scanner.nextInt();
    	
    	List<Account> accounts = accountRepository.update(fromAccId);
    	if (accounts.isEmpty()) {
    		System.out.println("해당 고객이 보유한 계좌가 없습니다.");
    	} else {
    		System.out.println("====== 보유 계좌 목록 ======");
    		for (Account acc : accounts) {
    			System.out.println(acc);
    		}
    	}
    }
}