package service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import model.Account;
import model.AccountStatus;
import repository.AccountRepositories;

public class AccountService {
    
    // DB와 통신하기 위해 팀원 A가 만든 Repository를 가져옴
	 private final AccountRepositories accountRepository;
	    private final Scanner scanner;

	    // 의존성 주입 (팀원 A가 만든 Repository를 주입받음)
	    public AccountService(AccountRepositories accountRepository) {
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
	        System.out.print("입금할 계좌번호 입력: "); // 💡 안내 문구 수정 (고객 ID -> 계좌번호)
	        String accountNo = scanner.nextLine();
	        
	        // 1. 입력받은 계좌번호로 기존 계좌 정보 조회
	        Account account = accountRepository.findByAccountNo(accountNo);
	        
	        if (account != null) {
	            System.out.print("입금할 금액 입력: ");
	            BigDecimal depositAmount = scanner.nextBigDecimal();
	            scanner.nextLine(); // Scanner 버그 방지용 (엔터키 소모)

	            if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
	                System.out.println("금액은 0원보다 커야 합니다.");
	                return;
	            }

	            // 2. 기존 잔액에 입금액을 더해서 새로운 잔액 설정
	          
	            account.setBalance(account.getBalance().add(depositAmount));

	            // 3. 💡 핵심: 수정된 account 객체를 통째로 넘겨서 DB 업데이트
	            boolean isSuccess = accountRepository.update(account);
	            
	            if (isSuccess) {
	                System.out.println("====== 입금 완료 ======");
	                System.out.println(account.toString()); // 롬복 @ToString 활용
	            } else {
	                System.out.println("입금 처리 중 오류가 발생했습니다.");
	            }
	        } else {
	            System.out.println("해당 계좌를 찾을 수 없습니다.");
	        }
	    }
	    public void f_withdraw() {
	        System.out.println("\n--- 계좌 입금 ---");
	        System.out.print("출금할 계좌번호 입력: "); // 💡 안내 문구 수정 (고객 ID -> 계좌번호)
	        String accountNo = scanner.nextLine();
	        
	        // 1. 입력받은 계좌번호로 기존 계좌 정보 조회
	        Account account = accountRepository.findByAccountNo(accountNo);
	        
	        if (account != null) {
	            System.out.print("출금할 금액 입력: ");
	            BigDecimal depositAmount = scanner.nextBigDecimal();
	            scanner.nextLine(); // Scanner 버그 방지용 (엔터키 소모)

	            if (depositAmount.compareTo(account.getBalance()) > 0 ) {
	                System.out.println("출금할 금액이 부족합니다.");
	                return;
	            }

	            // 2. 기존 잔액에 입금액을 빼서 새로운 잔액 설정
	          
	            account.setBalance(account.getBalance().subtract(depositAmount));

	            // 3. 💡 핵심: 수정된 account 객체를 통째로 넘겨서 DB 업데이트
	            boolean isSuccess = accountRepository.update(account);
	            
	            if (isSuccess) {
	                System.out.println("====== 출금 완료 ======");
	                System.out.println(account.toString()); // 롬복 @ToString 활용
	            } else {
	                System.out.println("출금 처리 중 오류가 발생했습니다.");
	            }
	        } else {
	            System.out.println("해당 계좌를 찾을 수 없습니다.");
	        }
	    }
	    
	    public void f_transfer() {
	        System.out.println("\n--- 송금 서비스 ---");
	        System.out.print("보내시는 분 계좌번호 입력: "); // 💡 안내 문구 명확하게 수정
	        String fromAccNo = scanner.nextLine();
	        
	        System.out.print("받으실 분 계좌번호 입력: ");
	        String toAccNo = scanner.nextLine();
	        
	        // 자기 자신에게 송금하는 것 방지
	        if (fromAccNo.equals(toAccNo)) {
	            System.out.println("동일한 계좌로는 송금할 수 없습니다.");
	            return;
	        }

	        // 1. 발신 계좌와 수신 계좌를 각각 DB에서 조회
	        Account fromAccount = accountRepository.findByAccountNo(fromAccNo);
	        Account toAccount = accountRepository.findByAccountNo(toAccNo);

	        // 2. 계좌 존재 여부 검증
	        if (fromAccount == null) {
	            System.out.println("보내시는 분의 계좌를 찾을 수 없습니다.");
	            return;
	        }
	        if (toAccount == null) {
	            System.out.println("받으실 분의 계좌를 찾을 수 없습니다.");
	            return;
	        }

	        // 3. 송금 금액 입력 및 검증
	        System.out.print("송금할 금액 입력: ");
	        BigDecimal transferAmount = scanner.nextBigDecimal();
	        scanner.nextLine(); // Scanner 버그 방지용 (엔터키 소모)

	        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
	            System.out.println("송금 금액은 0원보다 커야 합니다.");
	            return;
	        }

	        // 4. 발신 계좌 잔액 부족 검증 (발신잔액 < 송금액)
	        if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
	            System.out.println("잔액이 부족합니다. 현재 잔액: " + fromAccount.getBalance());
	            return;
	        }

	        // 5. 💡 금액 연산 (한 줄 축약 적용!)
	        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount)); // 발신인은 차감
	        toAccount.setBalance(toAccount.getBalance().add(transferAmount));       // 수신인은 가산

	        // 6. DB에 두 계좌의 변경 사항을 각각 업데이트
	        boolean isFromUpdateSuccess = accountRepository.update(fromAccount);
	        boolean isToUpdateSuccess = accountRepository.update(toAccount);

	        // 7. 결과 확인
	        if (isFromUpdateSuccess && isToUpdateSuccess) {
	            System.out.println("====== 송금 완료 ======");
	            System.out.println("[보낸 계좌] " + fromAccount.getAccountNo() + " | 남은 잔액: " + fromAccount.getBalance());
	            System.out.println("[받은 계좌] " + toAccount.getAccountNo() + " | 현재 잔액: " + toAccount.getBalance());
	        } else {
	            // 실제 금융 프로그램에서는 이 부분 처리가 매우 중요합니다 (트랜잭션 개념)
	            System.out.println("송금 처리 중 데이터베이스 오류가 발생했습니다. 시스템 관리자에게 문의하세요.");
	        }
	    }
}