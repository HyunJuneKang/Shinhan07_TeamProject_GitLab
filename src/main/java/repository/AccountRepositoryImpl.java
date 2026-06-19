package repository;

import model.Account;
import model.AccountStatus; // 패키지 경로 확인 필요
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepositoryImpl implements AccountRepositories {

    @Override
    public boolean save(Account account) {
        String sql = "INSERT INTO account (account_no, customer_id, balance, status) " +
                     "VALUES (TO_CHAR(seq_account_no.NEXTVAL), ?, ?, ?)";
        
        try (Connection conn = DBUtil.dbConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, account.getCustomerId());
            pstmt.setBigDecimal(2, account.getBalance());
            // Enum 타입을 DB에 문자열(ACTIVE 등)로 넣기 위해 .name() 사용
            pstmt.setString(3, account.getStatus() != null ? account.getStatus().name() : "ACTIVE");
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Account findByAccountNo(String accountNo) {
        String sql = "SELECT * FROM account WHERE account_no = ?";
        
        try (Connection conn = DBUtil.dbConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 롬복의 @Builder 패턴을 활용하여 깔끔하게 객체 생성
                    return Account.builder()
                            .accountNo(rs.getString("account_no"))
                            .customerId(rs.getLong("customer_id"))
                            .balance(rs.getBigDecimal("balance"))
                            // DB의 문자열을 다시 자바 Enum(AccountStatus)으로 변환
                            .status(AccountStatus.valueOf(rs.getString("status")))
                            // SQL Timestamp를 java.time.LocalDateTime으로 변환
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM account WHERE customer_id = ?";
        List<Account> list = new ArrayList<>();
        
        try (Connection conn = DBUtil.dbConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Account account = Account.builder()
                            .accountNo(rs.getString("account_no"))
                            .customerId(rs.getLong("customer_id"))
                            .balance(rs.getBigDecimal("balance"))
                            .status(AccountStatus.valueOf(rs.getString("status")))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
                    list.add(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(Account account) {
        String sql = "UPDATE account SET balance = ?, status = ? WHERE account_no = ?";
        
        try (Connection conn = DBUtil.dbConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, account.getBalance());
            pstmt.setString(2, account.getStatus().name());
            pstmt.setString(3, account.getAccountNo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String accountNo) {
        String sql = "UPDATE account SET status = 'CLOSED' WHERE account_no = ?";
        
        try (Connection conn = DBUtil.dbConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}