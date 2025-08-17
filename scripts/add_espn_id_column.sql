-- Add ESPN ID column to players table
ALTER TABLE players ADD COLUMN IF NOT EXISTS espn_id VARCHAR(20) UNIQUE;

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_players_espn_id ON players(espn_id);

-- Add other ESPN-related columns that we need
ALTER TABLE players ADD COLUMN IF NOT EXISTS display_name VARCHAR(100);
ALTER TABLE players ADD COLUMN IF NOT EXISTS headshot_url VARCHAR(500);
ALTER TABLE players ADD COLUMN IF NOT EXISTS status VARCHAR(50);
ALTER TABLE players ADD COLUMN IF NOT EXISTS age INTEGER;