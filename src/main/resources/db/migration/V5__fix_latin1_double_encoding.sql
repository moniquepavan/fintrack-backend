DO $$
DECLARE
    r RECORD;
    fixed TEXT;
BEGIN
    FOR r IN SELECT id, name FROM categories WHERE octet_length(name) > length(name) LOOP
        BEGIN
            fixed := convert_from(convert_to(r.name, 'LATIN1'), 'UTF8');
            UPDATE categories SET name = fixed WHERE id = r.id;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;

    FOR r IN SELECT id, name FROM payment_methods WHERE octet_length(name) > length(name) LOOP
        BEGIN
            fixed := convert_from(convert_to(r.name, 'LATIN1'), 'UTF8');
            UPDATE payment_methods SET name = fixed WHERE id = r.id;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;

    FOR r IN SELECT id, name FROM cards WHERE octet_length(name) > length(name) LOOP
        BEGIN
            fixed := convert_from(convert_to(r.name, 'LATIN1'), 'UTF8');
            UPDATE cards SET name = fixed WHERE id = r.id;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;

    FOR r IN SELECT id, description FROM transactions WHERE octet_length(description) > length(description) LOOP
        BEGIN
            fixed := convert_from(convert_to(r.description, 'LATIN1'), 'UTF8');
            UPDATE transactions SET description = fixed WHERE id = r.id;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;

    FOR r IN SELECT id, name FROM users WHERE octet_length(name) > length(name) LOOP
        BEGIN
            fixed := convert_from(convert_to(r.name, 'LATIN1'), 'UTF8');
            UPDATE users SET name = fixed WHERE id = r.id;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
$$;
