-- bookmark Soft Delete 지원
ALTER TABLE bookmark
    ADD COLUMN deleted_at TIMESTAMPTZ;

ALTER TABLE bookmark
    DROP CONSTRAINT uk_bookmark_member_place;

CREATE UNIQUE INDEX uk_bookmark_active_member_place
    ON bookmark (member_id, place_id)
    WHERE deleted_at IS NULL;
