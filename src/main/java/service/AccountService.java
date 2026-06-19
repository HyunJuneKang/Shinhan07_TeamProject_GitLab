package main.java.service;

import main.java.model.Account;
import main.java.repository.*;
import java.math.BigDecimal;

public class AccountService {
    
    // DB와 통신하기 위해 팀원 A가 만든 Repository를 가져옴
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // 🚀 진짜 비즈니스 로직: 계좌 이체
    public boolean transfer(String fromAccountNo, String toAccountNo, BigDecimal amount) {
        
        // 1. 보내는 사람과 받는 사람의 계좌 정보를 DB에서 조회 (Repository 사용)
        Account fromAccount = accountRepository.findByAccountNo(fromAccountNo);
        Account toAccount = accountRepository.findByAccountNo(toAccountNo);

        // 2. [비즈니스 로직 예시 1] 계좌가 존재하는지, 활성화(ACTIVE) 상태인지 검증
        if (fromAccount == null || toAccount == null) {
            System.out.println("존재하지 않는 계좌입니다.");
            return false;
        }
        if (!fromAccount.getStatus().name().equals("ACTIVE")) {
            System.out.println("출금 계좌가 정지 또는 해지된 상태입니다.");
            return false;
        }

        // 3. [비즈니스 로직 예시 2] 잔액이 충분한지 검사 (BigDecimal 연산)
        // fromAccount.getBalance() < amount 인지 확인
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            System.out.println("잔액이 부족합니다.");
            return false;
        }

        // 4. [비즈니스 로직 예시 3] 실제 이체 금액 계산 및 데이터 변경
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount)); // 돈 차감
        toAccount.setBalance(toAccount.getBalance().add(amount));       // 돈 추가

        // 5. 변경된 최종 결과를 각각 DB에 저장하라고 명령 (Repository 사용)
        boolean result1 = accountRepository.update(fromAccount);
        boolean result2 = accountRepository.update(toAccount);

        // 둘 다 성공해야 이체 성공
        return result1 && result2;
    }
}