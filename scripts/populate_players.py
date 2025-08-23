#!/usr/bin/env python3
"""
ESPN NFL Player Data Population Script

This script fetches all NFL players from ESPN's API by:
1. Getting all NFL teams
2. Getting each team's roster
3. Extracting player data and inserting into the database

Usage: python populate_players.py
"""

import requests
import psycopg2
from psycopg2.extras import RealDictCursor
import json
import time
import os
from typing import List, Dict, Optional

# Database configuration
DB_CONFIG = {
    'host': os.getenv('DB_HOST', 'localhost'),
    'database': os.getenv('DB_NAME', 'fantasy_football'),
    'user': os.getenv('DB_USER', 'peterlialios'),
    'password': os.getenv('DB_PASSWORD', 'admin')
}

# API configuration
ESPN_TEAMS_URL = "https://site.api.espn.com/apis/site/v2/sports/football/nfl/teams"
ESPN_ROSTER_URL = "https://site.api.espn.com/apis/site/v2/sports/football/nfl/teams/{team_id}/roster"

def get_database_connection():
    """Establish database connection"""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"Error connecting to database: {e}")
        return None

def fetch_nfl_teams() -> List[Dict]:
    """Fetch all NFL teams from ESPN API"""
    print("Fetching NFL teams...")
    try:
        response = requests.get(ESPN_TEAMS_URL)
        response.raise_for_status()
        data = response.json()
        
        teams = []
        for team_wrapper in data.get('sports', [{}])[0].get('leagues', [{}])[0].get('teams', []):
            team_info = team_wrapper.get('team', {})
            teams.append({
                'id': team_info.get('id'),
                'name': team_info.get('displayName'),
                'abbreviation': team_info.get('abbreviation'),
                'slug': team_info.get('slug')
            })
        
        print(f"Found {len(teams)} NFL teams")
        return teams
    except Exception as e:
        print(f"Error fetching teams: {e}")
        return []

def fetch_team_roster(team_id: str) -> List[Dict]:
    """Fetch roster for a specific team"""
    try:
        url = ESPN_ROSTER_URL.format(team_id=team_id)
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        
        players = []
        
        # ESPN groups athletes by position
        athletes_by_position = data.get('athletes', [])
        
        for position_group in athletes_by_position:
            # Debug: Check if position_group is a dict
            if not isinstance(position_group, dict):
                print(f"Warning: position_group is not a dict: {type(position_group)} - {position_group}")
                continue
                
            position_info = position_group.get('position', {})
            if isinstance(position_info, dict):
                position_abbreviation = position_info.get('abbreviation')
                position_name = position_info.get('displayName')
            else:
                # Sometimes position is just a string
                position_abbreviation = str(position_info) if position_info else None
                position_name = str(position_info) if position_info else None
            
            # Each position group has 'items' containing the actual players
            for athlete in position_group.get('items', []):
                # Debug: Check if athlete is a dict
                if not isinstance(athlete, dict):
                    print(f"Warning: athlete is not a dict: {type(athlete)} - {athlete}")
                    continue
                
                # Extract individual player position (more specific than group position)
                player_position = athlete.get('position', {})
                if isinstance(player_position, dict):
                    individual_position = player_position.get('abbreviation')
                    individual_position_name = player_position.get('displayName')
                else:
                    individual_position = str(player_position) if player_position else position_abbreviation
                    individual_position_name = str(player_position) if player_position else position_name
                
                # Extract player information
                player_data = {
                    'espn_id': athlete.get('id'),
                    'full_name': athlete.get('fullName'),
                    'display_name': athlete.get('displayName'),
                    'first_name': athlete.get('firstName'),
                    'last_name': athlete.get('lastName'),
                    'jersey': athlete.get('jersey'),
                    'position': individual_position or position_abbreviation,
                    'position_name': individual_position_name or position_name,
                    'height': athlete.get('height'),
                    'weight': athlete.get('weight'),
                    'age': athlete.get('age'),
                    'status': athlete.get('status', {}).get('type') if athlete.get('status') else None,
                    'team_id': team_id,
                    'headshot_url': athlete.get('headshot', {}).get('href') if athlete.get('headshot') else None
                }
                
                # Handle experience data
                experience = athlete.get('experience', {})
                if experience:
                    player_data['years_pro'] = experience.get('years')
                
                players.append(player_data)
        
        return players
    except Exception as e:
        print(f"Error fetching roster for team {team_id}: {e}")
        import traceback
        traceback.print_exc()
        return []

def get_nfl_team_id_by_espn_id(cursor, espn_id: str) -> Optional[int]:
    """Get our internal NFL team ID by ESPN ID"""
    try:
        cursor.execute("SELECT id FROM nfl_teams WHERE espn_id = %s", (espn_id,))
        result = cursor.fetchone()
        return result['id'] if result else None
    except Exception as e:
        print(f"Error getting NFL team ID for ESPN ID {espn_id}: {e}")
        return None

def insert_player(cursor, player_data: Dict, nfl_team_id: int) -> bool:
    """Insert a single player into the database"""
    try:
        # Check if player already exists
        cursor.execute("SELECT id FROM players WHERE espn_id = %s", (player_data['espn_id'],))
        if cursor.fetchone():
            print(f"Player {player_data['full_name']} already exists, skipping...")
            return True
        
        # Insert player
        insert_query = """
        INSERT INTO players (
            espn_id, first_name, last_name, display_name, position, 
            jersey_number, height_inches, weight_lbs, age, years_experience, 
            nfl_team_id, status, headshot_url, is_active, is_dst
        ) VALUES (
            %s, %s, %s, %s, %s, 
            %s, %s, %s, %s, %s, 
            %s, %s, %s, %s, %s
        )
        """
        
        # Parse height (ESPN format: "6' 2\"" -> 74 inches)
        height_inches = None
        if player_data.get('height'):
            try:
                height_str = player_data['height'].replace('"', '').replace("'", ' ')
                parts = height_str.split()
                if len(parts) == 2:
                    feet, inches = int(parts[0]), int(parts[1])
                    height_inches = feet * 12 + inches
            except:
                pass
        
        # Determine if player is active
        is_active = player_data.get('status') in [None, 'Active', 'active']
        
        cursor.execute(insert_query, (
            player_data['espn_id'],
            player_data.get('first_name'),
            player_data.get('last_name'),
            player_data.get('display_name') or player_data.get('full_name'),
            player_data.get('position'),
            player_data.get('jersey'),
            height_inches,
            player_data.get('weight'),
            player_data.get('age'),
            player_data.get('years_pro'),
            nfl_team_id,
            player_data.get('status'),
            player_data.get('headshot_url'),
            is_active,
            False  # is_dst - regular players are not D/ST
        ))
        
        return True
    except Exception as e:
        print(f"Error inserting player {player_data.get('full_name', 'Unknown')}: {e}")
        return False

def populate_players():
    """Main function to populate players table"""
    print("Starting NFL player population process...")
    
    # Get database connection
    conn = get_database_connection()
    if not conn:
        print("Failed to connect to database. Exiting.")
        return
    
    try:
        cursor = conn.cursor(cursor_factory=RealDictCursor)
        
        # Step 1: Get all NFL teams
        teams = fetch_nfl_teams()
        if not teams:
            print("No teams found. Exiting.")
            return
        
        total_players = 0
        successful_inserts = 0
        
        # Step 2: Process each team's roster
        for i, team in enumerate(teams, 1):
            team_espn_id = team['id']
            team_name = team['name']
            
            print(f"\n[{i}/{len(teams)}] Processing {team_name} (ESPN ID: {team_espn_id})...")
            
            # Get our internal team ID
            nfl_team_id = get_nfl_team_id_by_espn_id(cursor, team_espn_id)
            if not nfl_team_id:
                print(f"Warning: Could not find internal team ID for ESPN ID {team_espn_id}")
                continue
            
            # Get team roster
            players = fetch_team_roster(team_espn_id)
            print(f"Found {len(players)} players for {team_name}")
            
            # Step 3: Insert each player
            for player in players:
                if insert_player(cursor, player, nfl_team_id):
                    successful_inserts += 1
                total_players += 1
            
            # Commit after each team to avoid losing data
            conn.commit()
            
            # Be nice to ESPN's servers
            time.sleep(0.5)
        
        print(f"\n✅ Population complete!")
        print(f"📊 Total players processed: {total_players}")
        print(f"✅ Successful inserts: {successful_inserts}")
        print(f"⚠️  Skipped/Failed: {total_players - successful_inserts}")
        
    except Exception as e:
        print(f"Error during population process: {e}")
        conn.rollback()
    finally:
        cursor.close()
        conn.close()

def check_database_setup():
    """Verify database tables exist"""
    conn = get_database_connection()
    if not conn:
        return False
    
    try:
        cursor = conn.cursor()
        
        # Check if required tables exist
        cursor.execute("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = 'players'
            )
        """)
        players_exists = cursor.fetchone()[0]
        
        cursor.execute("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = 'nfl_teams'
            )
        """)
        nfl_teams_exists = cursor.fetchone()[0]
        
        if not players_exists:
            print("❌ Error: 'players' table does not exist")
            return False
            
        if not nfl_teams_exists:
            print("❌ Error: 'nfl_teams' table does not exist")
            return False
            
        # Check if nfl_teams has data
        cursor.execute("SELECT COUNT(*) FROM nfl_teams")
        team_count = cursor.fetchone()[0]
        
        if team_count == 0:
            print("❌ Error: 'nfl_teams' table is empty. Please populate it first.")
            return False
        
        print(f"✅ Database setup verified. Found {team_count} NFL teams.")
        return True
        
    except Exception as e:
        print(f"Error checking database setup: {e}")
        return False
    finally:
        cursor.close()
        conn.close()

if __name__ == "__main__":
    print("🏈 ESPN NFL Player Population Script")
    print("=" * 50)
    
    # Verify database setup
    if not check_database_setup():
        print("\n❌ Database setup check failed. Please ensure:")
        print("1. PostgreSQL is running")
        print("2. Database 'fantasy_football' exists")
        print("3. Tables 'players' and 'nfl_teams' exist")
        print("4. nfl_teams table is populated")
        exit(1)
    
    # Run population
    populate_players()
    
    print("\n🎉 Script completed!")