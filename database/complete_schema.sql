-- Complete PostgreSQL Database Schema for Fantasy Football Management Application
-- Updated with ESPN API integration and all current modifications
-- This represents the current state of the database as of 2025-08-17

-- Create database (run this separately as superuser)
-- CREATE DATABASE fantasy_football;

-- Connect to fantasy_football database before running the rest

-- Drop existing tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS league_teams CASCADE;
DROP TABLE IF EXISTS leagues CASCADE;
DROP TABLE IF EXISTS player_stats CASCADE;
DROP TABLE IF EXISTS team_players CASCADE;
DROP TABLE IF EXISTS roster_positions CASCADE;
DROP TABLE IF EXISTS team_links CASCADE;
DROP TABLE IF EXISTS team_logos CASCADE;
DROP TABLE IF EXISTS players CASCADE;
DROP TABLE IF EXISTS nfl_teams CASCADE;
DROP TABLE IF EXISTS teams CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Enhanced NFL teams table based on ESPN API structure
CREATE TABLE nfl_teams (
    id SERIAL PRIMARY KEY,
    -- ESPN API identifiers
    espn_id INTEGER UNIQUE NOT NULL,
    espn_uid VARCHAR(50) UNIQUE NOT NULL,
    
    -- Basic team information
    name VARCHAR(100) NOT NULL,                    -- "Cardinals"
    nickname VARCHAR(100),                         -- "Cardinals" 
    location VARCHAR(100) NOT NULL,                -- "Arizona"
    display_name VARCHAR(150) NOT NULL,            -- "Arizona Cardinals"
    short_display_name VARCHAR(100),               -- "Cardinals"
    abbreviation VARCHAR(5) NOT NULL,              -- "ARI"
    slug VARCHAR(100) UNIQUE NOT NULL,             -- "arizona-cardinals"
    
    -- Team branding
    primary_color VARCHAR(7),                      -- "#a40227" (hex color)
    alternate_color VARCHAR(7),                    -- "#ffffff" (hex color)
    
    -- Status flags
    is_active BOOLEAN DEFAULT true,
    is_all_star BOOLEAN DEFAULT false,
    
    -- NFL structure (derived/computed fields)
    conference VARCHAR(3) CHECK (conference IN ('AFC', 'NFC')),
    division VARCHAR(10),
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Team logos table (normalized due to multiple logo types)
CREATE TABLE team_logos (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES nfl_teams(id) ON DELETE CASCADE,
    href VARCHAR(500) NOT NULL,                    -- Logo URL
    alt_text VARCHAR(200),                         -- Alt text
    width INTEGER,                                 -- Image width
    height INTEGER,                                -- Image height
    
    -- Logo relationship types (can have multiple values)
    rel_full BOOLEAN DEFAULT false,                -- "full" relationship
    rel_default BOOLEAN DEFAULT false,             -- "default" relationship
    rel_dark BOOLEAN DEFAULT false,                -- "dark" relationship
    rel_scoreboard BOOLEAN DEFAULT false,          -- "scoreboard" relationship
    
    -- Logo flags
    is_vector BOOLEAN DEFAULT false,               -- SVG/vector format
    is_full_color BOOLEAN DEFAULT true,            -- Full color vs monochrome
    is_transparent BOOLEAN DEFAULT false,          -- Transparent background
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Team links table (normalized due to multiple link types)
CREATE TABLE team_links (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES nfl_teams(id) ON DELETE CASCADE,
    
    -- Link properties
    language VARCHAR(10) DEFAULT 'en-US',
    href VARCHAR(500) NOT NULL,                    -- Link URL
    text VARCHAR(100),                             -- Display text
    short_text VARCHAR(50),                        -- Short display text
    
    -- Link relationship types
    rel_clubhouse BOOLEAN DEFAULT false,
    rel_roster BOOLEAN DEFAULT false,
    rel_stats BOOLEAN DEFAULT false,
    rel_schedule BOOLEAN DEFAULT false,
    rel_tickets BOOLEAN DEFAULT false,
    rel_depthchart BOOLEAN DEFAULT false,
    rel_desktop BOOLEAN DEFAULT false,
    rel_team BOOLEAN DEFAULT false,
    
    -- Link flags
    is_external BOOLEAN DEFAULT false,
    is_premium BOOLEAN DEFAULT false,
    is_hidden BOOLEAN DEFAULT false,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table for fantasy football managers
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teams table for fantasy teams
CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    budget DECIMAL(10,2) DEFAULT 100.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roster positions definition table
CREATE TABLE roster_positions (
    id SERIAL PRIMARY KEY,
    position_code VARCHAR(10) UNIQUE NOT NULL,     -- QB, RB, WR, TE, FLEX, K, DST, BENCH
    position_name VARCHAR(50) NOT NULL,            -- "Quarterback", "Running Back", etc.
    max_count INTEGER NOT NULL DEFAULT 1,          -- Max players for this position
    is_starting BOOLEAN DEFAULT true,              -- true for starting lineup, false for bench
    display_order INTEGER NOT NULL DEFAULT 99,     -- Order for display in lineup
    
    -- Position type constraints
    CONSTRAINT chk_position_code CHECK (position_code IN ('QB', 'RB', 'WR', 'TE', 'FLEX', 'K', 'DST', 'BENCH')),
    CONSTRAINT chk_max_count_positive CHECK (max_count > 0)
);

-- Enhanced players table with ESPN integration
CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    espn_id VARCHAR(20) UNIQUE,                    -- ESPN player ID
    first_name VARCHAR(50),                        -- NULL for D/ST
    last_name VARCHAR(50),                         -- NULL for D/ST
    display_name VARCHAR(100),                     -- ESPN display name
    position VARCHAR(10) NOT NULL,                 -- QB, RB, WR, TE, K, DST, etc.
    nfl_team_id INTEGER REFERENCES nfl_teams(id),
    jersey_number INTEGER,                         -- NULL for D/ST
    height_inches INTEGER,                         -- NULL for D/ST
    weight_lbs INTEGER,                            -- NULL for D/ST
    age INTEGER,                                   -- Current age
    birth_date DATE,                              -- NULL for D/ST
    years_experience INTEGER DEFAULT 0,
    headshot_url VARCHAR(500),                     -- ESPN headshot URL
    status VARCHAR(50),                            -- active, injured, etc.
    salary DECIMAL(12,2),
    fantasy_points DECIMAL(8,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT true,
    is_dst BOOLEAN DEFAULT false,                  -- true for team defenses
    dst_team_name VARCHAR(100),                    -- "Cardinals D/ST" for defenses
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Ensure either it's a player with names OR it's a D/ST
    CONSTRAINT chk_player_or_dst CHECK (
        (is_dst = false AND first_name IS NOT NULL AND last_name IS NOT NULL AND dst_team_name IS NULL) OR
        (is_dst = true AND first_name IS NULL AND last_name IS NULL AND dst_team_name IS NOT NULL)
    )
);

-- Enhanced team players junction table with roster position enforcement
CREATE TABLE team_players (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES teams(id) ON DELETE CASCADE,
    player_id INTEGER REFERENCES players(id) ON DELETE CASCADE,
    roster_position VARCHAR(10) NOT NULL REFERENCES roster_positions(position_code),
    acquisition_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cost DECIMAL(8,2) DEFAULT 0.00,
    
    -- Constraints
    UNIQUE(team_id, player_id),                    -- Player can only be on one team
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Player statistics table
CREATE TABLE player_stats (
    id SERIAL PRIMARY KEY,
    player_id INTEGER REFERENCES players(id) ON DELETE CASCADE,
    week INTEGER NOT NULL,
    season INTEGER NOT NULL,
    games_played INTEGER DEFAULT 0,
    points_scored DECIMAL(6,2) DEFAULT 0.00,
    yards_gained INTEGER DEFAULT 0,
    touchdowns INTEGER DEFAULT 0,
    field_goals INTEGER DEFAULT 0,
    interceptions INTEGER DEFAULT 0,
    fumbles INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(player_id, week, season)
);

-- Leagues table
CREATE TABLE leagues (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    max_teams INTEGER DEFAULT 12,
    draft_date DATE,
    season INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- League teams junction table
CREATE TABLE league_teams (
    id SERIAL PRIMARY KEY,
    league_id INTEGER REFERENCES leagues(id) ON DELETE CASCADE,
    team_id INTEGER REFERENCES teams(id) ON DELETE CASCADE,
    UNIQUE(league_id, team_id)
);

-- Enhanced indexes for performance
CREATE INDEX idx_nfl_teams_espn_id ON nfl_teams(espn_id);
CREATE INDEX idx_nfl_teams_abbreviation ON nfl_teams(abbreviation);
CREATE INDEX idx_nfl_teams_slug ON nfl_teams(slug);
CREATE INDEX idx_nfl_teams_conference_division ON nfl_teams(conference, division);

CREATE INDEX idx_team_logos_team_id ON team_logos(team_id);
CREATE INDEX idx_team_logos_rel_default ON team_logos(team_id, rel_default) WHERE rel_default = true;
CREATE INDEX idx_team_logos_rel_scoreboard ON team_logos(team_id, rel_scoreboard) WHERE rel_scoreboard = true;

CREATE INDEX idx_team_links_team_id ON team_links(team_id);
CREATE INDEX idx_team_links_clubhouse ON team_links(team_id, rel_clubhouse) WHERE rel_clubhouse = true;
CREATE INDEX idx_team_links_roster ON team_links(team_id, rel_roster) WHERE rel_roster = true;

CREATE INDEX idx_players_position ON players(position);
CREATE INDEX idx_players_nfl_team ON players(nfl_team_id);
CREATE INDEX idx_players_is_dst ON players(is_dst);
CREATE INDEX idx_players_espn_id ON players(espn_id);

CREATE INDEX idx_team_players_team ON team_players(team_id);
CREATE INDEX idx_team_players_player ON team_players(player_id);
CREATE INDEX idx_team_players_position ON team_players(roster_position);
CREATE INDEX idx_team_players_team_position ON team_players(team_id, roster_position);

CREATE INDEX idx_player_stats_player_week ON player_stats(player_id, week, season);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Add partial unique constraint to prevent multiple players in the same starting position
CREATE UNIQUE INDEX idx_team_players_unique_starting_position 
ON team_players(team_id, roster_position) 
WHERE roster_position NOT IN ('BENCH');

-- Insert roster position definitions
INSERT INTO roster_positions (position_code, position_name, max_count, is_starting, display_order) VALUES
('QB', 'Quarterback', 1, true, 1),
('RB', 'Running Back', 2, true, 2),
('WR', 'Wide Receiver', 2, true, 3),
('TE', 'Tight End', 1, true, 4),
('FLEX', 'Flex (RB/WR/TE)', 1, true, 5),
('K', 'Kicker', 1, true, 6),
('DST', 'Defense/Special Teams', 1, true, 7),
('BENCH', 'Bench', 7, false, 8);

-- Roster validation trigger to enforce position limits
CREATE OR REPLACE FUNCTION validate_roster_composition()
RETURNS TRIGGER AS $$
DECLARE
    current_count INTEGER;
    max_allowed INTEGER;
    position_name_var VARCHAR(50);
BEGIN
    -- Get the maximum allowed count for this position
    SELECT rp.max_count, rp.position_name 
    INTO max_allowed, position_name_var
    FROM roster_positions rp 
    WHERE rp.position_code = NEW.roster_position;
    
    -- Count current players in this position for this team
    SELECT COUNT(*) 
    INTO current_count
    FROM team_players tp 
    WHERE tp.team_id = NEW.team_id 
    AND tp.roster_position = NEW.roster_position
    AND (TG_OP = 'INSERT' OR tp.id != NEW.id); -- Exclude current record for updates
    
    -- Check if adding this player would exceed the limit
    IF current_count >= max_allowed THEN
        RAISE EXCEPTION 'Team already has the maximum number of % players (%). Cannot add more players to this position.', 
                       position_name_var, max_allowed;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for roster validation
CREATE TRIGGER validate_roster_before_insert
    BEFORE INSERT ON team_players
    FOR EACH ROW
    EXECUTE FUNCTION validate_roster_composition();

CREATE TRIGGER validate_roster_before_update
    BEFORE UPDATE ON team_players
    FOR EACH ROW
    EXECUTE FUNCTION validate_roster_composition();

-- Insert sample user for development
INSERT INTO users (username, email, password_hash) VALUES 
('demo_user', 'demo@example.com', 'demo_password_hash_placeholder');

-- Insert enhanced NFL teams data based on ESPN API
INSERT INTO nfl_teams (espn_id, espn_uid, name, nickname, location, display_name, short_display_name, abbreviation, slug, primary_color, alternate_color, is_active, is_all_star, conference, division) VALUES
-- AFC East
(2, 's:20~l:28~t:2', 'Bills', 'Bills', 'Buffalo', 'Buffalo Bills', 'Bills', 'BUF', 'buffalo-bills', '#00338d', '#d50a0a', true, false, 'AFC', 'East'),
(15, 's:20~l:28~t:15', 'Dolphins', 'Dolphins', 'Miami', 'Miami Dolphins', 'Dolphins', 'MIA', 'miami-dolphins', '#008e97', '#fc4c02', true, false, 'AFC', 'East'),
(17, 's:20~l:28~t:17', 'Patriots', 'Patriots', 'New England', 'New England Patriots', 'Patriots', 'NE', 'new-england-patriots', '#002244', '#c60c30', true, false, 'AFC', 'East'),
(20, 's:20~l:28~t:20', 'Jets', 'Jets', 'New York', 'New York Jets', 'Jets', 'NYJ', 'new-york-jets', '#125740', '#ffffff', true, false, 'AFC', 'East'),

-- AFC North
(33, 's:20~l:28~t:33', 'Ravens', 'Ravens', 'Baltimore', 'Baltimore Ravens', 'Ravens', 'BAL', 'baltimore-ravens', '#29126f', '#000000', true, false, 'AFC', 'North'),
(4, 's:20~l:28~t:4', 'Bengals', 'Bengals', 'Cincinnati', 'Cincinnati Bengals', 'Bengals', 'CIN', 'cincinnati-bengals', '#fb4f14', '#000000', true, false, 'AFC', 'North'),
(5, 's:20~l:28~t:5', 'Browns', 'Browns', 'Cleveland', 'Cleveland Browns', 'Browns', 'CLE', 'cleveland-browns', '#472a08', '#ff3c00', true, false, 'AFC', 'North'),
(23, 's:20~l:28~t:23', 'Steelers', 'Steelers', 'Pittsburgh', 'Pittsburgh Steelers', 'Steelers', 'PIT', 'pittsburgh-steelers', '#000000', '#ffb612', true, false, 'AFC', 'North'),

-- AFC South
(34, 's:20~l:28~t:34', 'Texans', 'Texans', 'Houston', 'Houston Texans', 'Texans', 'HOU', 'houston-texans', '#00143f', '#c41230', true, false, 'AFC', 'South'),
(11, 's:20~l:28~t:11', 'Colts', 'Colts', 'Indianapolis', 'Indianapolis Colts', 'Colts', 'IND', 'indianapolis-colts', '#003b75', '#ffffff', true, false, 'AFC', 'South'),
(30, 's:20~l:28~t:30', 'Jaguars', 'Jaguars', 'Jacksonville', 'Jacksonville Jaguars', 'Jaguars', 'JAX', 'jacksonville-jaguars', '#006778', '#d7a22a', true, false, 'AFC', 'South'),
(10, 's:20~l:28~t:10', 'Titans', 'Titans', 'Tennessee', 'Tennessee Titans', 'Titans', 'TEN', 'tennessee-titans', '#0c2340', '#4b92db', true, false, 'AFC', 'South'),

-- AFC West
(7, 's:20~l:28~t:7', 'Broncos', 'Broncos', 'Denver', 'Denver Broncos', 'Broncos', 'DEN', 'denver-broncos', '#0a2343', '#fc4c02', true, false, 'AFC', 'West'),
(12, 's:20~l:28~t:12', 'Chiefs', 'Chiefs', 'Kansas City', 'Kansas City Chiefs', 'Chiefs', 'KC', 'kansas-city-chiefs', '#e31837', '#ffb612', true, false, 'AFC', 'West'),
(13, 's:20~l:28~t:13', 'Raiders', 'Raiders', 'Las Vegas', 'Las Vegas Raiders', 'Raiders', 'LV', 'las-vegas-raiders', '#000000', '#a5acaf', true, false, 'AFC', 'West'),
(24, 's:20~l:28~t:24', 'Chargers', 'Chargers', 'Los Angeles', 'Los Angeles Chargers', 'Chargers', 'LAC', 'los-angeles-chargers', '#0080c6', '#ffc20e', true, false, 'AFC', 'West'),

-- NFC East
(6, 's:20~l:28~t:6', 'Cowboys', 'Cowboys', 'Dallas', 'Dallas Cowboys', 'Cowboys', 'DAL', 'dallas-cowboys', '#002a5c', '#b0b7bc', true, false, 'NFC', 'East'),
(19, 's:20~l:28~t:19', 'Giants', 'Giants', 'New York', 'New York Giants', 'Giants', 'NYG', 'new-york-giants', '#0b2265', '#a71930', true, false, 'NFC', 'East'),
(21, 's:20~l:28~t:21', 'Eagles', 'Eagles', 'Philadelphia', 'Philadelphia Eagles', 'Eagles', 'PHI', 'philadelphia-eagles', '#004c54', '#a5acaf', true, false, 'NFC', 'East'),
(28, 's:20~l:28~t:28', 'Commanders', 'Commanders', 'Washington', 'Washington Commanders', 'Commanders', 'WAS', 'washington-commanders', '#5a1414', '#ffb612', true, false, 'NFC', 'East'),

-- NFC North
(3, 's:20~l:28~t:3', 'Bears', 'Bears', 'Chicago', 'Chicago Bears', 'Bears', 'CHI', 'chicago-bears', '#0b1c3a', '#e64100', true, false, 'NFC', 'North'),
(8, 's:20~l:28~t:8', 'Lions', 'Lions', 'Detroit', 'Detroit Lions', 'Lions', 'DET', 'detroit-lions', '#0076b6', '#bbbbbb', true, false, 'NFC', 'North'),
(9, 's:20~l:28~t:9', 'Packers', 'Packers', 'Green Bay', 'Green Bay Packers', 'Packers', 'GB', 'green-bay-packers', '#204e32', '#ffb612', true, false, 'NFC', 'North'),
(16, 's:20~l:28~t:16', 'Vikings', 'Vikings', 'Minnesota', 'Minnesota Vikings', 'Vikings', 'MIN', 'minnesota-vikings', '#4f2683', '#ffc62f', true, false, 'NFC', 'North'),

-- NFC South
(1, 's:20~l:28~t:1', 'Falcons', 'Falcons', 'Atlanta', 'Atlanta Falcons', 'Falcons', 'ATL', 'atlanta-falcons', '#a71930', '#000000', true, false, 'NFC', 'South'),
(29, 's:20~l:28~t:29', 'Panthers', 'Panthers', 'Carolina', 'Carolina Panthers', 'Panthers', 'CAR', 'carolina-panthers', '#0085ca', '#000000', true, false, 'NFC', 'South'),
(18, 's:20~l:28~t:18', 'Saints', 'Saints', 'New Orleans', 'New Orleans Saints', 'Saints', 'NO', 'new-orleans-saints', '#9f8958', '#000000', true, false, 'NFC', 'South'),
(27, 's:20~l:28~t:27', 'Buccaneers', 'Buccaneers', 'Tampa Bay', 'Tampa Bay Buccaneers', 'Buccaneers', 'TB', 'tampa-bay-buccaneers', '#d50a0a', '#ff7900', true, false, 'NFC', 'South'),

-- NFC West
(22, 's:20~l:28~t:22', 'Cardinals', 'Cardinals', 'Arizona', 'Arizona Cardinals', 'Cardinals', 'ARI', 'arizona-cardinals', '#a40227', '#ffffff', true, false, 'NFC', 'West'),
(14, 's:20~l:28~t:14', 'Rams', 'Rams', 'Los Angeles', 'Los Angeles Rams', 'Rams', 'LAR', 'los-angeles-rams', '#003594', '#ffa300', true, false, 'NFC', 'West'),
(25, 's:20~l:28~t:25', '49ers', '49ers', 'San Francisco', 'San Francisco 49ers', '49ers', 'SF', 'san-francisco-49ers', '#aa0000', '#b3995d', true, false, 'NFC', 'West'),
(26, 's:20~l:28~t:26', 'Seahawks', 'Seahawks', 'Seattle', 'Seattle Seahawks', 'Seahawks', 'SEA', 'seattle-seahawks', '#002244', '#69be28', true, false, 'NFC', 'West');

-- Insert sample Defense/Special Teams units for each NFL team
INSERT INTO players (position, nfl_team_id, is_dst, dst_team_name, fantasy_points, is_active)
SELECT 'DST', id, true, display_name || ' D/ST', 0.00, true
FROM nfl_teams;
