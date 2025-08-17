-- PostgreSQL Database Schema for Fantasy Football Management Application

-- Create database (run this separately as superuser)
-- CREATE DATABASE fantasy_football;

-- Connect to fantasy_football database before running the rest

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

-- NFL teams table
CREATE TABLE nfl_teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(5) NOT NULL,
    city VARCHAR(50) NOT NULL,
    conference VARCHAR(3) CHECK (conference IN ('AFC', 'NFC')),
    division VARCHAR(10)
);

-- Players table for NFL players
CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(10) NOT NULL,
    nfl_team_id INTEGER REFERENCES nfl_teams(id),
    jersey_number INTEGER,
    height_inches INTEGER,
    weight_lbs INTEGER,
    birth_date DATE,
    years_experience INTEGER DEFAULT 0,
    salary DECIMAL(12,2),
    fantasy_points DECIMAL(8,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Team players junction table (roster)
CREATE TABLE team_players (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES teams(id) ON DELETE CASCADE,
    player_id INTEGER REFERENCES players(id) ON DELETE CASCADE,
    position_on_team VARCHAR(20),
    acquisition_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cost DECIMAL(8,2) DEFAULT 0.00,
    is_starter BOOLEAN DEFAULT false,
    UNIQUE(team_id, player_id)
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

-- Leagues table (optional for future expansion)
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

-- Indexes for performance
CREATE INDEX idx_players_position ON players(position);
CREATE INDEX idx_players_nfl_team ON players(nfl_team_id);
CREATE INDEX idx_team_players_team ON team_players(team_id);
CREATE INDEX idx_team_players_player ON team_players(player_id);
CREATE INDEX idx_player_stats_player_week ON player_stats(player_id, week, season);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Insert sample NFL teams
INSERT INTO nfl_teams (name, abbreviation, city, conference, division) VALUES
('Patriots', 'NE', 'New England', 'AFC', 'East'),
('Bills', 'BUF', 'Buffalo', 'AFC', 'East'),
('Dolphins', 'MIA', 'Miami', 'AFC', 'East'),
('Jets', 'NYJ', 'New York', 'AFC', 'East'),
('Ravens', 'BAL', 'Baltimore', 'AFC', 'North'),
('Bengals', 'CIN', 'Cincinnati', 'AFC', 'North'),
('Browns', 'CLE', 'Cleveland', 'AFC', 'North'),
('Steelers', 'PIT', 'Pittsburgh', 'AFC', 'North'),
('Texans', 'HOU', 'Houston', 'AFC', 'South'),
('Colts', 'IND', 'Indianapolis', 'AFC', 'South'),
('Jaguars', 'JAX', 'Jacksonville', 'AFC', 'South'),
('Titans', 'TEN', 'Tennessee', 'AFC', 'South'),
('Broncos', 'DEN', 'Denver', 'AFC', 'West'),
('Chiefs', 'KC', 'Kansas City', 'AFC', 'West'),
('Raiders', 'LV', 'Las Vegas', 'AFC', 'West'),
('Chargers', 'LAC', 'Los Angeles', 'AFC', 'West'),
('Cowboys', 'DAL', 'Dallas', 'NFC', 'East'),
('Giants', 'NYG', 'New York', 'NFC', 'East'),
('Eagles', 'PHI', 'Philadelphia', 'NFC', 'East'),
('Commanders', 'WAS', 'Washington', 'NFC', 'East'),
('Bears', 'CHI', 'Chicago', 'NFC', 'North'),
('Lions', 'DET', 'Detroit', 'NFC', 'North'),
('Packers', 'GB', 'Green Bay', 'NFC', 'North'),
('Vikings', 'MIN', 'Minnesota', 'NFC', 'North'),
('Falcons', 'ATL', 'Atlanta', 'NFC', 'South'),
('Panthers', 'CAR', 'Carolina', 'NFC', 'South'),
('Saints', 'NO', 'New Orleans', 'NFC', 'South'),
('Buccaneers', 'TB', 'Tampa Bay', 'NFC', 'South'),
('Cardinals', 'ARI', 'Arizona', 'NFC', 'West'),
('Rams', 'LAR', 'Los Angeles', 'NFC', 'West'),
('49ers', 'SF', 'San Francisco', 'NFC', 'West'),
('Seahawks', 'SEA', 'Seattle', 'NFC', 'West');

-- Insert sample players
INSERT INTO players (first_name, last_name, position, nfl_team_id, jersey_number, height_inches, weight_lbs, years_experience, fantasy_points) VALUES
('Tom', 'Brady', 'QB', 1, 12, 76, 225, 23, 285.4),
('Josh', 'Allen', 'QB', 2, 17, 77, 237, 6, 298.8),
('Stefon', 'Diggs', 'WR', 2, 14, 72, 191, 8, 198.2),
('Derrick', 'Henry', 'RB', 12, 22, 75, 247, 7, 219.8),
('Travis', 'Kelce', 'TE', 14, 87, 77, 260, 11, 168.4),
('Cooper', 'Kupp', 'WR', 30, 10, 70, 208, 6, 231.9),
('Jonathan', 'Taylor', 'RB', 10, 28, 70, 226, 3, 182.3),
('Davante', 'Adams', 'WR', 15, 17, 73, 215, 9, 187.6),
('Aaron', 'Rodgers', 'QB', 23, 12, 74, 225, 18, 241.2),
('Christian', 'McCaffrey', 'RB', 31, 23, 71, 205, 6, 203.7);

-- Insert sample user
INSERT INTO users (username, email, password_hash) VALUES
('demo_user', 'demo@fantasyfootball.com', '$2a$10$dummy.hash.for.demo.purposes.only');

-- Insert sample team
INSERT INTO teams (name, owner_id, budget) VALUES
('Demo Team', 1, 95.50);

-- Insert sample roster
INSERT INTO team_players (team_id, player_id, position_on_team, cost, is_starter) VALUES
(1, 2, 'QB', 15.00, true),
(1, 3, 'WR1', 12.50, true),
(1, 4, 'RB1', 18.00, true),
(1, 5, 'TE', 8.00, true),
(1, 6, 'WR2', 14.00, true),
(1, 7, 'RB2', 10.00, false),
(1, 8, 'FLEX', 12.00, true),
(1, 9, 'BENCH', 6.00, false);