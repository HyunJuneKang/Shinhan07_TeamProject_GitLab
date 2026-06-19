package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Account {
    private String accountNo;          // 계좌번호
    private Long customerId;           // 고객 ID
    private BigDecimal balance;        // 잔액
    private AccountStatus status;      // ACTIVE / CLOSED
    private LocalDateTime createdAt;   // 생성일
}