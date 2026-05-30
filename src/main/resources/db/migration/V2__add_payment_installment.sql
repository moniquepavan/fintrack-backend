ALTER TABLE transactions
    ADD COLUMN payment_method VARCHAR(30),
  ADD COLUMN installment_number INTEGER,
  ADD COLUMN installment_total INTEGER,
  ADD COLUMN installment_group_id UUID;

ALTER TABLE categories
    ADD COLUMN budget NUMERIC(15,2);