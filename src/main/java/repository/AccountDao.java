package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Account;

/**
 * 설명 : 계좌 테이블(account) 접근 DAO. 순수 JDBC.
 * Connection은 Service가 넘겨주고(트랜잭션 경계는 Service 소유), 여기서는 닫지 않는다.
 */
public class AccountDao {

    /** 계좌를 저장한다. 계좌번호(account_no)는 seq_account_no로 발급해 account에 채워준다. */
    public void insert(Connection con, Account account) {
        if (account.getAccountNo() == null) {
            account.setAccountNo(String.valueOf(nextAccountNo(con)));
        }
        String sql = "INSERT INTO account (account_no, customer_id, balance) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNo());
            ps.setLong(2, account.getCustomerId());
            ps.setBigDecimal(3, account.getBalance());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("계좌 저장 실패", e);
        }
    }

    /** 전체 계좌 목록. */
    public List<Account> findAll(Connection con) {
        String sql = "SELECT account_no, customer_id, balance, created_at FROM account ORDER BY account_no";
        List<Account> result = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("계좌 목록 조회 실패", e);
        }
    }

    /** 계좌번호로 단건 조회. 없으면 null. */
    public Account findByNo(Connection con, String accountNo) {
        String sql = "SELECT account_no, customer_id, balance, created_at FROM account WHERE account_no = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("계좌 조회 실패", e);
        }
    }

    /** 잔액 변경(입금/출금/이체). */
    public void updateBalance(Connection con, String accountNo, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE account_no = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setString(2, accountNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("잔액 변경 실패", e);
        }
    }

    /** 계좌 해지(삭제). */
    public void delete(Connection con, String accountNo) {
        String sql = "DELETE FROM account WHERE account_no = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("계좌 해지 실패", e);
        }
    }

    /** 시퀀스로 다음 계좌번호를 얻는다. */
    private long nextAccountNo(Connection con) {
        String sql = "SELECT seq_account_no.NEXTVAL FROM dual";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("계좌번호 채번 실패", e);
        }
    }

    /** ResultSet 한 행 → Account. */
    private Account map(ResultSet rs) throws SQLException {
        return Account.builder()
                .accountNo(rs.getString("account_no"))
                .customerId(rs.getLong("customer_id"))
                .balance(rs.getBigDecimal("balance"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
