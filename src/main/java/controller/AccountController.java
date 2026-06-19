package controller;

import java.math.BigDecimal;

import model.Account;
import service.AccountService;
import view.AccountView;

/**
 * 설명 : 메뉴 루프를 돌며 입력을 받아 Service를 호출하고 결과를 View로 출력한다(Controller).
 * DB/트랜잭션은 모르고, Service와 View만 사용한다.
 */
public class AccountController {

    private final AccountService accountService;
    private final AccountView view;

    public AccountController(AccountService accountService, AccountView view) {
        this.accountService = accountService;
        this.view = view;
    }

    public void run() {
        view.printBanner();
        boolean running = true;
        while (running) {
            view.printMenu();
            int menu = view.readMenu();
            try {
                switch (menu) {
                    case 1 -> openAccount();
                    case 2 -> findAllAccounts();
                    case 3 -> findAccount();
                    case 4 -> deposit();
                    case 5 -> withdraw();
                    case 6 -> transfer();
                    case 7 -> closeAccount();
                    case 0 -> running = false;
                    default -> view.printError("잘못된 메뉴 선택입니다. 0~7 중에서 선택하세요.");
                }
            } catch (RuntimeException e) {
                // 잘못된 계좌/잔액 부족/금액 오류 등을 잡아 메시지를 보여주고 루프를 유지한다.
                view.printError(e.getMessage());
            }
        }
        view.printExit();
    }

    private void openAccount() {
        String name = view.readString("고객명 > ");
        String phone = view.readString("전화번호 > ");
        BigDecimal initial = view.readBigDecimal("초기 잔액 > ");
        if (initial == null) {
            view.printError("초기 잔액은 숫자로 입력하세요.");
            return;
        }
        Account account = accountService.openAccount(name, phone, initial);
        view.printMessage("계좌가 개설되었습니다. 계좌번호: " + account.getAccountNo());
        view.printAccount(account);
    }

    private void findAllAccounts() {
        view.printAccounts(accountService.findAllAccounts());
    }

    private void findAccount() {
        String accountNo = view.readString("조회할 계좌번호 > ");
        view.printAccount(accountService.findAccount(accountNo));
    }

    private void deposit() {
        String accountNo = view.readString("입금 계좌번호 > ");
        BigDecimal amount = view.readBigDecimal("입금 금액 > ");
        if (amount == null) {
            view.printError("금액은 숫자로 입력하세요.");
            return;
        }
        accountService.deposit(accountNo, amount);
        view.printMessage("입금이 완료되었습니다.");
        view.printAccount(accountService.findAccount(accountNo));
    }

    private void withdraw() {
        String accountNo = view.readString("출금 계좌번호 > ");
        BigDecimal amount = view.readBigDecimal("출금 금액 > ");
        if (amount == null) {
            view.printError("금액은 숫자로 입력하세요.");
            return;
        }
        accountService.withdraw(accountNo, amount);
        view.printMessage("출금이 완료되었습니다.");
        view.printAccount(accountService.findAccount(accountNo));
    }

    private void transfer() {
        String fromNo = view.readString("출금(보내는) 계좌번호 > ");
        String toNo = view.readString("입금(받는) 계좌번호 > ");
        BigDecimal amount = view.readBigDecimal("이체 금액 > ");
        if (amount == null) {
            view.printError("금액은 숫자로 입력하세요.");
            return;
        }
        accountService.transfer(fromNo, toNo, amount);
        view.printMessage("이체가 완료되었습니다.");
        view.printAccount(accountService.findAccount(fromNo));
        view.printAccount(accountService.findAccount(toNo));
    }

    private void closeAccount() {
        String accountNo = view.readString("해지할 계좌번호 > ");
        accountService.closeAccount(accountNo);
        view.printMessage("계좌가 해지되었습니다: " + accountNo);
    }
}
