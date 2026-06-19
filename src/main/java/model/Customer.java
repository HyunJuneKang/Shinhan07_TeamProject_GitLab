package model;

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
public class Customer {
    private Long customerId;           // 고객 ID
    private String name;               // 고객명
    private String phone;              // 전화번호
    private LocalDateTime createdAt;   // 생성일
}