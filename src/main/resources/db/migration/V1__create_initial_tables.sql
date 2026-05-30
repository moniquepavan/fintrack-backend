CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name          VARCHAR(100)        NOT NULL,
                       email         VARCHAR(150)        NOT NULL UNIQUE,
                       password_hash VARCHAR(255)        NOT NULL,
                       created_at    TIMESTAMP           NOT NULL DEFAULT NOW(),
                       updated_at    TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
                            id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            user_id    UUID         REFERENCES users(id) ON DELETE CASCADE,
                            name       VARCHAR(50)  NOT NULL,
                            color      VARCHAR(7)   NOT NULL DEFAULT '#6366f1',
                            icon       VARCHAR(30)  NOT NULL DEFAULT 'tag',
                            is_default BOOLEAN      NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
                            updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE transactions (
                              id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              user_id          UUID           NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              category_id      UUID           REFERENCES categories(id) ON DELETE SET NULL,
                              description      VARCHAR(200)   NOT NULL,
                              amount           NUMERIC(15, 2) NOT NULL,
                              type             VARCHAR(10)    NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                              transaction_date DATE           NOT NULL,
                              is_recurring     BOOLEAN        NOT NULL DEFAULT FALSE,
                              recurrence_rule  VARCHAR(100),
                              created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
                              updated_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_date    ON transactions(transaction_date);
CREATE INDEX idx_categories_user_id   ON categories(user_id);