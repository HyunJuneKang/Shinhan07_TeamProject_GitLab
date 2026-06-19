package repository;

import model.Account;

import java.util.List;

public interface AccountRepositories {
    // 1. 새로운 계좌 개설 (시퀀스 사용)
    boolean save(Account account);

    // 2. 계좌번호로 특정 계좌 정보 단건 조회
    Account findByAccountNo(String accountNo);

    // 3. 특정 고객(customer_id)이 가진 모든 계좌 목록 조회
    List<Account> findByCustomerId(int customerId);

    // 4. 계좌 잔액 및 상태 업데이트 (입금, 출금, 송금 시 사용)
    boolean update(Account account);

    // 5. 계좌 폐쇄 (상태를 'CLOSED'로 변경)
    boolean delete(String accountNo);
}