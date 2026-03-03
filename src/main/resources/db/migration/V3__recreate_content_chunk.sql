CREATE EXTENSION IF NOT EXISTS vector;

DO $$ BEGIN
    CREATE TYPE source_type_enum AS ENUM (
        'VIDEO_EVALUATION',
        'VIDEO_SUMMARY',
        'COMMENT_REACTION',
        'VIEWER_ESCAPE_ANALYSIS',
        'ALGORITHM_OPTIMIZATION',
        'PERSONALIZED_KEYWORDS',
        'IDEA_RECOMMENDATION'
    );
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

CREATE TABLE IF NOT EXISTS content_chunk (
    id          SERIAL PRIMARY KEY,
    source_type source_type_enum NOT NULL,
    source_id   INT NOT NULL,
    content     TEXT NOT NULL,
    chunk_index INT NOT NULL,
    embedding   vector(1536) NOT NULL,
    meta        JSONB,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_source ON content_chunk(source_type, source_id);
CREATE INDEX IF NOT EXISTS idx_embedding ON content_chunk USING ivfflat (embedding vector_cosine_ops);
