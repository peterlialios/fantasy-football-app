const express = require('express');
const axios = require('axios');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;
const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080/api';

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.get('/', (req, res) => {
    res.render('index');
});

app.get('/teams', async (req, res) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/teams`);
        res.render('teams', { teams: response.data });
    } catch (error) {
        console.error('Error fetching teams:', error.message);
        res.render('teams', { teams: [], error: 'Failed to load teams' });
    }
});

app.get('/team/:id', async (req, res) => {
    try {
        const teamId = req.params.id;
        const [teamResponse, rosterResponse] = await Promise.all([
            axios.get(`${API_BASE_URL}/teams/${teamId}`),
            axios.get(`${API_BASE_URL}/teams/${teamId}/roster`)
        ]);
        
        res.render('team-detail', { 
            team: teamResponse.data, 
            roster: rosterResponse.data 
        });
    } catch (error) {
        console.error('Error fetching team details:', error.message);
        res.render('team-detail', { 
            team: null, 
            roster: [], 
            error: 'Failed to load team details' 
        });
    }
});

// Team creation route
app.post('/teams/create', async (req, res) => {
    try {
        const { teamName, ownerId } = req.body;
        
        const response = await axios.post(`${API_BASE_URL}/teams`, {
            name: teamName,
            ownerId: ownerId || 1, // Default to demo user
            budget: 100.00
        });
        
        res.json({ success: true, team: response.data });
    } catch (error) {
        console.error('Error creating team:', error.message);
        res.status(400).json({ success: false, error: 'Failed to create team' });
    }
});

app.get('/players', async (req, res) => {
    try {
        const searchName = req.query.search || '';
        const position = req.query.position || '';
        const teamId = req.query.teamId || '';
        
        let url = `${API_BASE_URL}/players/active`;
        
        if (searchName) {
            url = `${API_BASE_URL}/players/search?name=${encodeURIComponent(searchName)}`;
        } else if (position) {
            url = `${API_BASE_URL}/players/position/${encodeURIComponent(position)}`;
        } else if (teamId) {
            url = `${API_BASE_URL}/players/available/${teamId}`;
        }
        
        const response = await axios.get(url);
        res.render('players', { 
            players: response.data,
            searchName,
            position,
            teamId
        });
    } catch (error) {
        console.error('Error fetching players:', error.message);
        res.render('players', { 
            players: [], 
            searchName: '',
            position: '',
            teamId: '',
            error: 'Failed to load players' 
        });
    }
});

app.post('/api/teams/:teamId/add-player/:playerId', async (req, res) => {
    try {
        const { teamId, playerId } = req.params;
        const { positionOnTeam, cost } = req.body;
        
        const response = await axios.post(
            `${API_BASE_URL}/teams/${teamId}/players/${playerId}?rosterPosition=${positionOnTeam}&cost=${cost || 0}`
        );
        
        res.json({ success: true, data: response.data });
    } catch (error) {
        console.error('Error adding player to team:', error.message);
        res.status(400).json({ success: false, error: 'Failed to add player to team' });
    }
});

app.delete('/api/teams/:teamId/remove-player/:playerId', async (req, res) => {
    try {
        const { teamId, playerId } = req.params;
        
        await axios.delete(`${API_BASE_URL}/teams/${teamId}/players/${playerId}`);
        
        res.json({ success: true });
    } catch (error) {
        console.error('Error removing player from team:', error.message);
        res.status(400).json({ success: false, error: 'Failed to remove player from team' });
    }
});

app.put('/api/teams/:teamId/players/:playerId/starter-status', async (req, res) => {
    try {
        const { teamId, playerId } = req.params;
        const { isStarter } = req.body;
        
        const response = await axios.put(
            `${API_BASE_URL}/teams/${teamId}/players/${playerId}/roster-position?rosterPosition=${isStarter ? 'STARTER' : 'BENCH'}`
        );
        
        res.json({ success: true, data: response.data });
    } catch (error) {
        console.error('Error updating player starter status:', error.message);
        res.status(400).json({ success: false, error: 'Failed to update player status' });
    }
});

app.listen(PORT, () => {
    console.log(`Fantasy Football Frontend running on http://localhost:${PORT}`);
    console.log(`API expected at: ${API_BASE_URL}`);
});