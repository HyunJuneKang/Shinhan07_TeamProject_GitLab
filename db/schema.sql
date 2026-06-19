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
    created_at    DATE          DEFAULT SYSDATE NOT NULL
);

CREATE SEQUENCE seq_customer_id START WITH 1;
CREATE SEQUENCE seq_account_no  START WITH 1001;
