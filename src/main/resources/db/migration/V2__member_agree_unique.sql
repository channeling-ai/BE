ALTER TABLE member_agree
    ADD CONSTRAINT uc_memberagree_member UNIQUE (member_id);

DROP TABLE content_chunk CASCADE;

ALTER TABLE member
    ALTER COLUMN plan SET NOT NULL;