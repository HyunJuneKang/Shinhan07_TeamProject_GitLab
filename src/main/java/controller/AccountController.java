package controller;


import java.util.Scanner;

import repository.AccountRepositories;
import repository.AccountRepositoryImpl;
import service.AccountService;
import view.AccountView;

public class AccountController {
	
    public static void ac_Menu() {
    	AccountRepositories accountRepository = new AccountRepositoryImpl();
		AccountService accountService = new AccountService(accountRepository);
		
		Scanner scanner = new Scanner(System.in);
		AccountView.scr_View();
		
    	while (true) {
            System.out.println("\n[메뉴] 1.계좌개설 | 2.계좌조회 | 3.고객계좌목록조회 |\n"
            		+ 			"       4.입금   |  5. 출금  |    6. 송금     |0.종료");
            
            System.out.print("선택> ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    accountService.createAccount();
                    break;
                case 2:
                    accountService.getAccount();
                    break;
                case 3:
                    accountService.getCustomerAccounts();;
                    break;
                case 4:
                	accountService.f_deposit();
                	break;
                case 5:
                	accountService.f_withdraw();
                	break;
                case 6:
                	accountService.f_transfer();
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