CREATE OR REPLACE FUNCTION update_country_statistic()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO user_country_statistic (user_id, country_id, count, updated_at)
    VALUES (NEW.user_id, NEW.country_id, 1, CURRENT_DATE)
    ON CONFLICT (user_id, country_id)
    DO UPDATE SET
        count = user_country_statistic.count + 1,
        updated_at = CURRENT_DATE;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_statistic
    AFTER INSERT ON photo
    FOR EACH ROW
    EXECUTE FUNCTION update_country_statistic();

CREATE OR REPLACE FUNCTION decrease_country_statistic()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE user_country_statistic
    SET count = count - 1,
        updated_at = CURRENT_DATE
    WHERE user_id = OLD.user_id AND country_id = OLD.country_id;

    DELETE FROM user_country_statistic
    WHERE user_id = OLD.user_id AND country_id = OLD.country_id AND count <= 0;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_decrease_statistic
    AFTER DELETE ON photo
    FOR EACH ROW
    EXECUTE FUNCTION decrease_country_statistic();

CREATE OR REPLACE FUNCTION update_photo_country_statistic()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.country_id IS DISTINCT FROM NEW.country_id THEN
        UPDATE user_country_statistic
        SET count = GREATEST(0, count - 1),
            updated_at = CURRENT_DATE
        WHERE user_id = OLD.user_id AND country_id = OLD.country_id;

        DELETE FROM user_country_statistic
        WHERE user_id = OLD.user_id AND country_id = OLD.country_id AND count = 0;

        INSERT INTO user_country_statistic (user_id, country_id, count)
        VALUES (NEW.user_id, NEW.country_id, 1)
        ON CONFLICT (user_id, country_id)
        DO UPDATE SET
            count = user_country_statistic.count + 1,
            updated_at = CURRENT_DATE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_photo_country
    AFTER UPDATE ON photo
    FOR EACH ROW
    EXECUTE FUNCTION update_photo_country_statistic();