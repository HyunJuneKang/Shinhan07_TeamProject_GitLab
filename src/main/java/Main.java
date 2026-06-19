import controller.AccountController;
import repository.AccountDao;
import repository.CustomerDao;
import service.AccountService;
import view.AccountView;

/**
 * 설명 : 조립(Main). MVC 계층을 연결해 실행한다.
 * DAO(JDBC) → Service(트랜잭션) → Controller + View 순으로 의존성을 주입한다.
 *
 * @author yslee
 * @since 2026. 6. 19.
 */
public class Main {

    public static void main(String[] args) {
        // 1) DAO 계층 (순수 JDBC)
        AccountDao accountDao = new AccountDao();
        CustomerDao customerDao = new CustomerDao();

        // 2) Service 계층 (검증 + 트랜잭션)
        AccountService accountService = new AccountService(accountDao, customerDao);

        // 3) View + Controller 계층
        AccountView view = new AccountView();
        AccountController controller = new AccountController(accountService, view);

        // 4) 실행
        controller.run();
    }
}
