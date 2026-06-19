package view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import model.Account;

/**
 * 설명 : 콘솔 입출력 담당(View). 화면 표시와 입력 수집만 하고 비즈니스 로직은 없다.
 */
public class AccountView {

    private final Scanner scanner = new Scanner(System.in);

    public void printBanner() {
        System.out.println("========================================");
        System.out.println("      신한DS 은행 계좌 관리 시스템");
        System.out.println("========================================");
    }

    public void printMenu() {
        System.out.println();
        System.out.println("---------------- 메뉴 ----------------");
        System.out.println(" 1. 계좌 개설");
        System.out.println(" 2. 전체 계좌 조회");
        System.out.println(" 3. 단건 계좌 조회");
        System.out.println(" 4. 입금");
        System.out.println(" 5. 출금");
        System.out.println(" 6. 이체");
        System.out.println(" 7. 계좌 해지");
        System.out.println(" 0. 종료");
        System.out.println("--------------------------------------");
        System.out.print("선택 > ");
    }

    /** 메뉴 번호 입력. 숫자가 아니면 -1 반환. */
    public int readMenu() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** 금액 입력. 숫자가 아니면 null 반환. */
    public BigDecimal readBigDecimal(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        try {
            return new BigDecimal(line);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void printAccount(Account account) {
        System.out.printf("계좌번호: %-8s | 고객ID: %-4d | 잔액: %,15.2f%n",
                account.getAccountNo(),
                account.getCustomerId(),
                account.getBalance());
    }

    public void printAccounts(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("등록된 계좌가 없습니다.");
            return;
        }
        System.out.println("---------------- 전체 계좌 ----------------");
        for (Account account : accounts) {
            printAccount(account);
        }
        System.out.println("총 " + accounts.size() + "건");
    }

    public void printMessage(String message) {
        System.out.println("[안내] " + message);
    }

    public void printError(String message) {
        System.out.println("[오류] " + message);
    }

    public void printExit() {
        System.out.println("프로그램을 종료합니다. 이용해 주셔서 감사합니다.");
    }
}
