CREATE OR REPLACE FUNCTION fix_cp850_encoding(t TEXT) RETURNS TEXT AS $$
SELECT
  REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
  REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
  REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(t,
  '├á','à'), '├í','á'), '├ó','â'), '├ú','ã'),
  '├º','ç'), '├®','é'), '├¬','ê'), '├¡','í'),
  '├│','ó'), '├┤','ô'), '├Á','õ'), '├║','ú'),
  '├╝','ü'), '├Ç','À'), '├ü','Á'), '├é','Â'),
  '├â','Ã'), '├ç','Ç'), '├ë','É'), '├è','Ê'),
  '├ì','Í'), '├ô','Ó'), '├ö','Ô'), '├ò','Õ'),
  '├Ü','Ú'), '├£','Ü')
$$ LANGUAGE SQL IMMUTABLE;

UPDATE categories      SET name        = fix_cp850_encoding(name)        WHERE name        LIKE '%├%';
UPDATE payment_methods SET name        = fix_cp850_encoding(name)        WHERE name        LIKE '%├%';
UPDATE cards           SET name        = fix_cp850_encoding(name)        WHERE name        LIKE '%├%';
UPDATE transactions    SET description = fix_cp850_encoding(description) WHERE description LIKE '%├%';
UPDATE users           SET name        = fix_cp850_encoding(name)        WHERE name        LIKE '%├%';

DROP FUNCTION fix_cp850_encoding(TEXT);
