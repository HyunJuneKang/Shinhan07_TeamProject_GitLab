CREATE TABLE customer (
    customer_id   NUMBER        PRIMARY KEY,
    name          VARCHAR2(50)  NOT NULL,
    phone         VARCHAR2(20),
    created_at    DATE          DEFAULT SYSDATE NOT NULL
);

CREATE TABLE account (
    account_no    VARCHAR2(20)  PRIMARY KEY,
    customer_id   NUMBER        NOT NULL REFERENCES customer(customer_id),  -- FK
    balance       NUMBER(18,2)  DEFAULT 0 NOT NULL CHECK (balance >= 0),    -- 음수 잔액 차단
    status        VARCHAR2(10)  DEFAULT 'ACTIVE' NOT NULL
                  CHECK (status IN ('ACTIVE','CLOSED')),
    created_at    DATE          DEFAULT SYSDATE NOT NULL
);

CREATE TABLE tx_history (
    tx_id          NUMBER        PRIMARY KEY,
    account_no     VARCHAR2(20)  NOT NULL REFERENCES account(account_no),
    tx_type        VARCHAR2(15)  NOT NULL
                   CHECK (tx_type IN ('DEPOSIT','WITHDRAW','TRANSFER_OUT','TRANSFER_IN')),
    amount         NUMBER(18,2)  NOT NULL CHECK (amount > 0),
    balance_after  NUMBER(18,2)  NOT NULL,
    counterpart_no VARCHAR2(20),
    created_at     DATE          DEFAULT SYSDATE NOT NULL
);

CREATE SEQUENCE seq_customer_id START WITH 1;
CREATE SEQUENCE seq_account_no  START WITH 1001;
CREATE SEQUENCE seq_tx_id       START WITH 1;