package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Customer;

/**
 * 설명 : 고객 테이블(customer) 접근 DAO. 순수 JDBC.
 * Connection은 Service가 넘겨주고(트랜잭션 경계는 Service 소유), 여기서는 닫지 않는다.
 */
public class CustomerDao {

    /** 고객을 저장하고 발급된 customer_id를 반환한다. */
    public long insert(Connection con, Customer customer) {
        long customerId = nextId(con);
        String sql = "INSERT INTO customer (customer_id, name, phone) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getPhone());
            ps.executeUpdate();
            customer.setCustomerId(customerId);
            return customerId;
        } catch (SQLException e) {
            throw new RuntimeException("고객 저장 실패", e);
        }
    }

    /** 고객 단건 조회. 없으면 null. */
    public Customer findById(Connection con, long customerId) {
        String sql = "SELECT customer_id, name, phone, created_at FROM customer WHERE customer_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Customer.builder()
                            .customerId(rs.getLong("customer_id"))
                            .name(rs.getString("name"))
                            .phone(rs.getString("phone"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("고객 조회 실패", e);
        }
    }

    /** 시퀀스로 다음 customer_id를 얻는다. */
    private long nextId(Connection con) {
        String sql = "SELECT seq_customer_id.NEXTVAL FROM dual";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("고객 ID 채번 실패", e);
        }
    }
}
