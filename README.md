# Shinhan07_TeamProject_GitLab
팀 프로젝트 Git 협업 실습

---

딱 좋은 타이밍이에요. ERD와 기능이 정해졌으니, 이제 두 사람이 **서로를 막지 않고 병렬로** 개발하게 만드는 게 관건입니다. 그 비결은 "계약 먼저(contract-first)"예요. 코드를 짜기 전에 **모델 클래스와 인터페이스(메서드 시그니처)를 먼저 합의해서 develop에 올리는 것.** 그러면 강현준은 그 인터페이스를 *구현*하고, 너는 그 인터페이스에 *의존*해서 동시에 작업할 수 있어요. 서로의 구현이 끝나기를 기다릴 필요가 없습니다.핵심은 인터페이스를 가운데 두고 양쪽이 그 계약에 맞춰 동시에 구현하는 구조예요.## 패키지 구조

```
bank-system/
├── db/
│   └── schema.sql                  ← 너희가 만든 DDL (둘 다 로컬 Oracle에 실행)
├── src/
│   ├── model/                      Customer, Account, Transaction
│   ├── repository/                 인터페이스 + ...Impl (JDBC)
│   ├── service/                    AccountService(인터페이스) + Impl
│   ├── controller/                 AccountController
│   ├── view/                       AccountView
│   ├── exception/                  커스텀 예외
│   ├── util/                       DBConnection
│   └── Main.java                   조립 (공유 파일 → 충돌 지점)
└── .gitignore
```

## 공유 계약 — develop에 먼저 올릴 것

모델은 ERD를 그대로 옮긴 데이터 그릇이에요. 돈은 `double`이 아니라 `BigDecimal`을 쓰세요(부동소수점 오차 방지, `NUMBER(18,2)`와 짝). 상태·거래종류는 `enum`으로 두면 DB의 CHECK 제약과 같은 안전장치가 코드에도 생깁니다.

```java
// model/Account.java
public class Account {
    public enum Status { ACTIVE, CLOSED }
    private String accountNo;
    private Long customerId;
    private BigDecimal balance;
    private Status status;
    private LocalDate createdAt;
    // 생성자 + getter/setter
}

// model/Customer.java     : customerId, name, phone, createdAt
// model/Transaction.java  : txId, accountNo,
//   enum Type { DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN },
//   amount, balanceAfter, counterpartNo, createdAt
```

다음이 진짜 계약의 핵심인 인터페이스예요. 저장소 메서드가 `Connection`을 인자로 받는 점에 주목하세요 — 트랜잭션 경계를 Service가 쥐기 위해서입니다. (계좌개설·이체처럼 여러 INSERT/UPDATE를 한 묶음으로 처리해야 하니까요.)

```java
// repository/AccountRepository.java
public interface AccountRepository {
    void save(Connection con, Account account);
    Optional<Account> findByNo(Connection con, String accountNo);
    List<Account> findAll(Connection con);
    List<Account> findByCustomerId(Connection con, long customerId);
    void updateBalance(Connection con, String accountNo, BigDecimal newBalance);
    void updateStatus(Connection con, String accountNo, Account.Status status);
}

// repository/CustomerRepository.java
public interface CustomerRepository {
    long save(Connection con, Customer customer);   // 생성된 customer_id 반환
    Optional<Customer> findById(Connection con, long customerId);
}

// repository/TxHistoryRepository.java
public interface TxHistoryRepository {
    void save(Connection con, Transaction tx);
    List<Transaction> findByAccountNo(Connection con, String accountNo);
}
```

```java
// service/AccountService.java  — 7가지 기능이 곧 계약
public interface AccountService {
    Account openAccount(String customerName, String phone, BigDecimal initialBalance);
    List<Account> findAllAccounts();
    Account findAccount(String accountNo);                  // 없으면 예외
    void deposit(String accountNo, BigDecimal amount);
    void withdraw(String accountNo, BigDecimal amount);     // 잔액 부족 시 예외
    void transfer(String fromNo, String toNo, BigDecimal amount);
    void closeAccount(String accountNo);                    // 잔액 0 확인 후
}
```

이 시그니처들 + 모델 + `schema.sql` + `DBConnection`(Connection 얻는 유틸) + 커스텀 예외(`AccountNotFoundException`, `InsufficientBalanceException` 정도)까지가 **둘이 합의해서 develop에 먼저 올리는 묶음**입니다.

## 역할과 브랜치

계약이 develop에 올라간 뒤엔 이렇게 나눠 동시에 진행해요. 강현준은 `repository` 구현(실제 JDBC 코드)과 `controller`를 `feature/repository`·`feature/controller`에서 맡고, 너는 트랜잭션이 무거운 `service` 구현과 `view`를 `feature/service`·`feature/view`에서 맡습니다. 네 `AccountServiceImpl`은 강현준의 *구현 클래스*가 아니라 *인터페이스*에 의존하니까, 강현준이 JDBC를 다 못 짰어도 너는 막힘없이 로직을 짤 수 있어요. 이게 계약 우선의 힘입니다. (구현은 너희가 직접 채워야 진짜 너희 프로젝트가 되니, 위 코드는 합의용 뼈대로만 쓰세요.)

## 계약을 develop에 올리는 흐름

이 뼈대는 모든 게 의존하는 토대라, 가장 먼저 PR 한 번으로 깔끔하게 올립니다.

```bash
git switch -c feature/contract origin/develop
# (위 모델·인터페이스·schema.sql·DBConnection·예외 작성)
git add .
git commit -m "feat: 도메인 모델과 계약 인터페이스 정의"
git push -u origin feature/contract
# GitHub에서 feature/contract → develop PR, 둘이 함께 리뷰 후 Merge
```

이게 머지되면 둘 다 `git switch develop && git pull`로 받고, 각자 자기 feature 브랜치를 새로 떼어 시작하면 됩니다.

## 발표에서 점수가 되는 설계 포인트

세 가지를 의식적으로 챙기면 결과물의 격이 달라져요. 첫째, **트랜잭션**: `openAccount`와 `transfer`는 Service에서 `con.setAutoCommit(false)`로 시작해 성공 시 `commit()`, 예외 시 `rollback()` 하세요. 중간에 실패해도 절반만 반영되는 일이 없어집니다. 둘째, **이중 방어**: 앱에서 잔액을 검증하고, DB의 `CHECK (balance >= 0)`가 마지막 방어선을 칩니다. 셋째, **인터페이스 분리**: 계층이 인터페이스로 나뉘어 있어 병렬 작업도 되고 유지보수도 쉽다는 점 — 이게 "관심사 분리"를 말로만이 아니라 코드로 보여주는 부분이에요.

먼저 강현준과 위 시그니처(이름·반환형)를 같이 훑어보며 조정하고, `feature/contract` PR로 develop에 올리는 것부터 하세요. 그다음 각자 구현에 들어가다가 — 특히 네 쪽 Service의 트랜잭션 처리에서 막히기 쉬운데 — 그때 코드 가져오면 같이 짚어볼게요.

## 구현된 기능

| 기능 | 설명 |
|------|------|
| 계좌 개설 | 고객명·전화번호·초기 잔액 입력 → 고객/계좌 생성, 계좌번호 자동 발급(`seq_account_no`) |
| 전체 계좌 조회 | 등록된 모든 계좌 목록 출력 |
| 단건 조회 | 계좌번호로 특정 계좌 조회 (없으면 예외) |
| 입금 | 계좌번호 + 금액 입력 → 잔액 증가 |
| 출금 | 잔액 부족 시 예외 처리 |
| 이체 | 출금 계좌 → 입금 계좌 금액 이동 (한 트랜잭션) |
| 계좌 해지 | 잔액 0 확인 후 삭제 |

> 순수 JDBC + MVC 패턴(Controller·Service·DAO·DTO)으로 구현했다.
> 계층은 `model(DTO) → repository(DAO, JDBC) → service(검증·트랜잭션) → controller → view → Main(조립)`.
> Service가 `Connection`을 DAO에 넘기며 개설/입출금/이체/해지를 `setAutoCommit(false)` →
> `commit()`/`rollback()`으로 처리한다(이중 방어: 앱 검증 + DB `CHECK (balance >= 0)`).

## 빌드 & 실행 (Git Bash)

사전 준비: `db/schema.sql`을 대상 Oracle 계정(`util/DBUtil`의 접속 정보)에 실행해 테이블·시퀀스를 생성한다.

```bash
# 라이브러리 (lombok / ojdbc / json)
CP="C:/shinhan7Work/library/lombok.jar;C:/shinhan7Work/library/ojdbc6_g.jar;C:/shinhan7Work/library/json-20260522.jar"

# 컴파일
javac -encoding UTF-8 -cp "$CP" -d build/classes $(find src/main/java -name '*.java')

# 실행
java -cp "build/classes;$CP" Main
```