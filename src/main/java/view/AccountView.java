package view;

import java.util.Scanner;

import controller.AccountController;
import repository.AccountRepositories;
import repository.AccountRepositoryImpl;

public class AccountView {
  public static void scr_View() {
	  AccountRepositories accountRepository = new AccountRepositoryImpl();
	   AccountController accountController = new AccountController(accountRepository);
	    
	    Scanner scanner = new Scanner(System.in);
	    System.out.println("=================================");
	    System.out.println("    신한 7조 은행 시스템 구동    ");
	    System.out.println("=================================");

	    // 2. 메뉴 반복 구동
	    while (true) {
	        System.out.println("\n[메뉴] 1.계좌개설 | 2.계좌조회 | 3.고객계좌목록조회 |\n"
	        		+ 			"       4.입금   |  5. 출금  |    6. 송금     |0.종료");
	        
	        System.out.print("선택> ");
	        
	        int choice = scanner.nextInt();
	        
	        switch (choice) {
	            case 1:
	                accountController.createAccount();
	                break;
	            case 2:
	                accountController.getAccount();
	                break;
	            case 3:
	                accountController.getCustomerAccounts();
	                break;
	            case 4:
	            	accountController.f_deposit();
	            	break;
	            case 5:
	            	accountController.f_withdraw();
	            	break;
	            case 6:
	            	accountController.f_transfer();
	            	break;
	            case 0:
	                System.out.println("은행 시스템을 종료합니다.");
	                scanner.close();
	                System.exit(0);
	            default:
	                System.out.println("잘못된 선택입니다. 다시 입력해주세요.");
	        }
	    }

}
   
}
