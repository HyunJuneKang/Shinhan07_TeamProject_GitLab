package main.java.model;

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
public class TxHistory {
    private Long txId;                 // 거래 ID
    private String accountNo;          // 거래 계좌번호
    private TxType txType;             // 거래 유형
    private BigDecimal amount;         // 거래 금액
    private BigDecimal balanceAfter;   // 거래 후 잔액
    private String counterpartNo;      // 이체 상대 계좌번호
    private LocalDateTime createdAt;   // 거래일시
}