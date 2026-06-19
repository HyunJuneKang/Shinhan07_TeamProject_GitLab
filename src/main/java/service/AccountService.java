package service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.Account;
import model.Customer;
import repository.AccountDao;
import repository.CustomerDao;
import util.DBUtil;

/**
 * 설명 : 비즈니스 로직과 트랜잭션을 담당하는 Service (단일 클래스).
 * - DAO에 Connection을 넘겨주며, 개설/이체처럼 여러 SQL이 묶이는 작업은
 *   setAutoCommit(false) → commit() / 예외 시 rollback() 으로 원자성을 보장한다.
 * - 잘못된 입력은 표준 예외(IllegalArgumentException / IllegalStateException)로 알린다.
 */
public class AccountService {

    private final AccountDao accountDao;
    private final CustomerDao customerDao;

    public AccountService(AccountDao accountDao, CustomerDao customerDao) {
        this.accountDao = accountDao;
        this.customerDao = customerDao;
    }

    /** 계좌 개설 : 고객 등록 + 계좌 생성(초기 잔액)을 한 트랜잭션으로 처리. */
    public Account openAccount(String customerName, String phone, BigDecimal initialBalance) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("고객명을 입력해야 합니다.");
        }
        BigDecimal initial = (initialBalance == null) ? BigDecimal.ZERO : initialBalance;
        if (initial.signum() < 0) {
            throw new IllegalArgumentException("초기 잔액은 0 이상이어야 합니다.");
        }

        Connection con = connect();
        try {
            con.setAutoCommit(false);

            Customer customer = Customer.builder().name(customerName).phone(phone).build();
            long customerId = customerDao.insert(con, customer);

            Account account = Account.builder()
                    .customerId(customerId)
                    .balance(initial)
                    .build();
            accountDao.insert(con, account); // 계좌번호 자동 발급

            con.commit();
            return account;
        } catch (RuntimeException e) {
            rollback(con);
            throw e;
        } catch (SQLException e) {
            rollback(con);
            throw new RuntimeException("계좌 개설 실패", e);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    /** 전체 계좌 조회. */
    public List<Account> findAllAccounts() {
        Connection con = connect();
        try {
            return accountDao.findAll(con);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    /** 단건 조회. 없으면 예외. */
    public Account findAccount(String accountNo) {
        Connection con = connect();
        try {
            return getOrThrow(con, accountNo);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    /** 입금 : 잔액 증가. */
    public void deposit(String accountNo, BigDecimal amount) {
        validateAmount(amount);
        Connection con = connect();
        try {
            con.setAutoCommit(false);
            Account account = getOrThrow(con, accountNo);
            BigDecimal newBalance = account.getBalance().add(amount);
            accountDao.updateBalance(con, accountNo, newBalance);
            con.commit();
        } catch (RuntimeException e) {
            rollback(con);
            throw e;
        } catch (SQLException e) {
            rollback(con);
            throw new RuntimeException("입금 실패", e);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    /** 출금 : 잔액 부족 시 예외. */
    public void withdraw(String accountNo, BigDecimal amount) {
        validateAmount(amount);
        Connection con = connect();
        try {
            con.setAutoCommit(false);
            Account account = getOrThrow(con, accountNo);
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("잔액이 부족합니다. (잔액: " + account.getBalance()
                        + ", 요청: " + amount + ")");
            }
            BigDecimal newBalance = account.getBalance().subtract(amount);
            accountDao.updateBalance(con, accountNo, newBalance);
            con.commit();
        } catch (RuntimeException e) {
            rollback(con);
            throw e;
        } catch (SQLException e) {
            rollback(con);
            throw new RuntimeException("출금 실패", e);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    /** 이체 : 출금 계좌 → 입금 계좌. 두 계좌 갱신을 한 트랜잭션으로. */
    public void transfer(String fromNo, String toNo, BigDecimal amount) {
        validateAmount(amount);
        if (fromNo != null && fromNo.equals(toNo)) {
            throw new IllegalArgumentException("같은 계좌로는 이체할 수 없습니다.");
        }
        Connection con = connect();
        try {
            con.setAutoCommit(false);
            Account from = getOrThrow(con, fromNo);
            Account to = getOrThrow(con, toNo);
            if (from.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("잔액이 부족합니다. (잔액: " + from.getBalance()
                        + ", 요청: " + amount + ")");
            }
            accountDao.updateBalance(con, fromNo, from.getBalance().subtract(amount));
            accountDao.updateBalance(con, toNo, to.getBalance().add(amount));
            con.commit();
        } catch (RuntimeException e) {
            rollback(con);
            throw e;
        } catch (SQLException e) {
            rollback(con);
            throw new RuntimeException("이체 실패", e);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    /** 계좌 해지 : 잔액 0 확인 후 삭제. */
    public void closeAccount(String accountNo) {
        Connection con = connect();
        try {
            con.setAutoCommit(false);
            Account account = getOrThrow(con, accountNo);
            if (account.getBalance().signum() != 0) {
                throw new IllegalStateException("잔액이 남아 있어 해지할 수 없습니다. (잔액: "
                        + account.getBalance() + ")");
            }
            accountDao.delete(con, accountNo);
            con.commit();
        } catch (RuntimeException e) {
            rollback(con);
            throw e;
        } catch (SQLException e) {
            rollback(con);
            throw new RuntimeException("계좌 해지 실패", e);
        } finally {
            DBUtil.dbDisconnect(con, null, null);
        }
    }

    // ── 내부 헬퍼 ──────────────────────────────────────────────

    private Connection connect() {
        Connection con = DBUtil.dbConnect();
        if (con == null) {
            throw new RuntimeException("DB 연결에 실패했습니다.");
        }
        return con;
    }

    private Account getOrThrow(Connection con, String accountNo) {
        Account account = accountDao.findByNo(con, accountNo);
        if (account == null) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountNo);
        }
        return account;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }

    private void rollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
