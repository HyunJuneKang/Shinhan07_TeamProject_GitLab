package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 설명 : 계좌 정보를 담는 DTO.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Account {
    private String accountNo;          // 계좌번호 (seq_account_no로 자동 발급)
    private Long customerId;           // 고객 ID
    private BigDecimal balance;        // 잔액
    private LocalDateTime createdAt;   // 생성일
}
