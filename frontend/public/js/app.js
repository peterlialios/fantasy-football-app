// Modern Toast Notification System
class ToastManager {
    constructor() {
        this.createContainer();
    }
    
    createContainer() {
        if (!document.getElementById('toast-container')) {
            const container = document.createElement('div');
            container.id = 'toast-container';
            container.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                display: flex;
                flex-direction: column;
                gap: 10px;
                pointer-events: none;
            `;
            document.body.appendChild(container);
        }
    }
    
    show(message, type = 'info', duration = 4000) {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.style.cssText = `
            background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
            color: white;
            padding: 12px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transform: translateX(100%);
            transition: transform 0.3s ease;
            pointer-events: auto;
            max-width: 300px;
            display: flex;
            align-items: center;
            gap: 10px;
        `;
        
        const icon = type === 'success' ? '✓' : type === 'error' ? '✗' : 'ℹ';
        toast.innerHTML = `<span style="font-weight: bold;">${icon}</span> ${message}`;
        
        const container = document.getElementById('toast-container');
        container.appendChild(toast);
        
        // Animate in
        setTimeout(() => {
            toast.style.transform = 'translateX(0)';
        }, 10);
        
        // Auto remove
        setTimeout(() => {
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }, duration);
        
        // Click to dismiss
        toast.addEventListener('click', () => {
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        });
    }
}

const toast = new ToastManager();

// Enhanced Player Management Functions
async function addPlayerToTeam(teamId, playerId, position) {
    try {
        const modal = createCostInputModal();
        const cost = await showModal(modal);
        
        if (cost === null) return; // User cancelled
        
        const playerCost = parseFloat(cost) || 0.00;
        
        if (isNaN(playerCost) || playerCost < 0) {
            toast.show('Please enter a valid cost', 'error');
            return;
        }
        
        // Show loading
        const loadingToast = showLoading('Adding player to team...');
        
        const response = await fetch(`/api/teams/${teamId}/add-player/${playerId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                positionOnTeam: position,
                cost: playerCost
            })
        });
        
        hideLoading(loadingToast);
        
        const data = await response.json();
        
        if (data.success || response.ok) {
            toast.show('Player added to team successfully!', 'success');
            setTimeout(() => window.location.reload(), 1000);
        } else {
            toast.show('Failed to add player: ' + (data.error || 'Unknown error'), 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        toast.show('An error occurred while adding the player', 'error');
    }
}

// Modern Modal System
function createCostInputModal() {
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 10000;
        opacity: 0;
        transition: opacity 0.3s ease;
    `;
    
    const content = document.createElement('div');
    content.style.cssText = `
        background: white;
        padding: 2rem;
        border-radius: 12px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        max-width: 400px;
        width: 90%;
        transform: scale(0.9);
        transition: transform 0.3s ease;
    `;
    
    content.innerHTML = `
        <h3 style="margin: 0 0 1rem 0; color: #1e293b; font-size: 1.25rem;">Add Player Cost</h3>
        <div style="margin-bottom: 1rem;">
            <label style="display: block; margin-bottom: 0.5rem; font-weight: 500; color: #374151;">Cost ($)</label>
            <input type="number" id="player-cost-input" step="0.01" min="0" value="0.00" 
                   style="width: 100%; padding: 0.75rem; border: 2px solid #e5e7eb; border-radius: 8px; font-size: 1rem;">
        </div>
        <div style="display: flex; gap: 0.75rem; justify-content: flex-end;">
            <button id="cancel-btn" style="padding: 0.5rem 1rem; border: 2px solid #e5e7eb; background: white; color: #374151; border-radius: 6px; cursor: pointer;">Cancel</button>
            <button id="confirm-btn" style="padding: 0.5rem 1rem; border: none; background: #3b82f6; color: white; border-radius: 6px; cursor: pointer;">Add Player</button>
        </div>
    `;
    
    modal.appendChild(content);
    return modal;
}

function showModal(modal) {
    return new Promise((resolve) => {
        document.body.appendChild(modal);
        
        // Animate in
        setTimeout(() => {
            modal.style.opacity = '1';
            modal.firstElementChild.style.transform = 'scale(1)';
        }, 10);
        
        const input = modal.querySelector('#player-cost-input');
        const cancelBtn = modal.querySelector('#cancel-btn');
        const confirmBtn = modal.querySelector('#confirm-btn');
        
        input.focus();
        input.select();
        
        function cleanup(result) {
            modal.style.opacity = '0';
            modal.firstElementChild.style.transform = 'scale(0.9)';
            setTimeout(() => {
                if (modal.parentNode) {
                    modal.parentNode.removeChild(modal);
                }
                resolve(result);
            }, 300);
        }
        
        cancelBtn.addEventListener('click', () => cleanup(null));
        confirmBtn.addEventListener('click', () => cleanup(input.value));
        
        // Enter key to confirm
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') cleanup(input.value);
        });
        
        // Escape key to cancel
        modal.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') cleanup(null);
        });
        
        // Click outside to cancel
        modal.addEventListener('click', (e) => {
            if (e.target === modal) cleanup(null);
        });
    });
}

// Loading system
function showLoading(message = 'Loading...') {
    const loading = document.createElement('div');
    loading.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #3b82f6;
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        z-index: 10000;
        display: flex;
        align-items: center;
        gap: 10px;
    `;
    
    loading.innerHTML = `
        <div style="width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top: 2px solid white; border-radius: 50%; animation: spin 1s linear infinite;"></div>
        ${message}
    `;
    
    // Add spin animation
    const style = document.createElement('style');
    style.textContent = '@keyframes spin { to { transform: rotate(360deg); } }';
    document.head.appendChild(style);
    
    document.body.appendChild(loading);
    return loading;
}

function hideLoading(loadingElement) {
    if (loadingElement && loadingElement.parentNode) {
        loadingElement.parentNode.removeChild(loadingElement);
    }
}

async function removePlayer(teamId, playerId) {
    const confirmed = await showConfirmDialog('Are you sure you want to remove this player from the team?');
    if (!confirmed) return;
    
    try {
        const loadingToast = showLoading('Removing player...');
        
        const response = await fetch(`/api/teams/${teamId}/remove-player/${playerId}`, {
            method: 'DELETE'
        });
        
        hideLoading(loadingToast);
        
        const data = await response.json();
        
        if (data.success || response.ok) {
            toast.show('Player removed from team successfully!', 'success');
            setTimeout(() => window.location.reload(), 1000);
        } else {
            toast.show('Failed to remove player: ' + (data.error || 'Unknown error'), 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        toast.show('An error occurred while removing the player', 'error');
    }
}

async function updatePlayerStatus(teamId, playerId, isStarter) {
    const action = isStarter ? 'start' : 'bench';
    
    try {
        const loadingToast = showLoading(`${isStarter ? 'Starting' : 'Benching'} player...`);
        
        const response = await fetch(`/api/teams/${teamId}/players/${playerId}/starter-status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                isStarter: isStarter
            })
        });
        
        hideLoading(loadingToast);
        
        const data = await response.json();
        
        if (data.success || response.ok) {
            toast.show(`Player ${action}ed successfully!`, 'success');
            setTimeout(() => window.location.reload(), 1000);
        } else {
            toast.show(`Failed to ${action} player: ` + (data.error || 'Unknown error'), 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        toast.show('An error occurred while updating player status', 'error');
    }
}

// Confirm dialog
function showConfirmDialog(message) {
    return new Promise((resolve) => {
        const modal = document.createElement('div');
        modal.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 10000;
            opacity: 0;
            transition: opacity 0.3s ease;
        `;
        
        const content = document.createElement('div');
        content.style.cssText = `
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            max-width: 400px;
            width: 90%;
            transform: scale(0.9);
            transition: transform 0.3s ease;
        `;
        
        content.innerHTML = `
            <h3 style="margin: 0 0 1rem 0; color: #1e293b; font-size: 1.25rem;">Confirm Action</h3>
            <p style="margin: 0 0 1.5rem 0; color: #6b7280;">${message}</p>
            <div style="display: flex; gap: 0.75rem; justify-content: flex-end;">
                <button id="cancel-btn" style="padding: 0.5rem 1rem; border: 2px solid #e5e7eb; background: white; color: #374151; border-radius: 6px; cursor: pointer;">Cancel</button>
                <button id="confirm-btn" style="padding: 0.5rem 1rem; border: none; background: #ef4444; color: white; border-radius: 6px; cursor: pointer;">Confirm</button>
            </div>
        `;
        
        modal.appendChild(content);
        document.body.appendChild(modal);
        
        // Animate in
        setTimeout(() => {
            modal.style.opacity = '1';
            content.style.transform = 'scale(1)';
        }, 10);
        
        function cleanup(result) {
            modal.style.opacity = '0';
            content.style.transform = 'scale(0.9)';
            setTimeout(() => {
                if (modal.parentNode) {
                    modal.parentNode.removeChild(modal);
                }
                resolve(result);
            }, 300);
        }
        
        modal.querySelector('#cancel-btn').addEventListener('click', () => cleanup(false));
        modal.querySelector('#confirm-btn').addEventListener('click', () => cleanup(true));
        
        // Escape key to cancel
        modal.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') cleanup(false);
        });
        
        // Click outside to cancel
        modal.addEventListener('click', (e) => {
            if (e.target === modal) cleanup(false);
        });
    });
}

// Enhanced Search Functionality
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    // Add fade-in animation to content
    document.body.classList.add('fade-in');
    
    // Enhance search inputs with debounced functionality
    const searchInputs = document.querySelectorAll('input[type="text"], input[type="search"]');
    searchInputs.forEach(input => {
        const debouncedSearch = debounce((value) => {
            // Add loading state
            input.style.backgroundImage = 'url("data:image/svg+xml;charset=utf8,<svg xmlns=\\"http://www.w3.org/2000/svg\\" viewBox=\\"0 0 24 24\\" fill=\\"none\\" stroke=\\"currentColor\\"><circle cx=\\"12\\" cy=\\"12\\" r=\\"10\\"/><path d=\\"M12 6v6l4 2\\"/></svg>")';
            input.style.backgroundRepeat = 'no-repeat';
            input.style.backgroundPosition = 'right 10px center';
            input.style.backgroundSize = '16px';
            
            // Clear loading after delay
            setTimeout(() => {
                input.style.backgroundImage = '';
            }, 500);
        }, 300);
        
        input.addEventListener('input', (e) => {
            debouncedSearch(e.target.value);
        });
    });
    
    // Add click animations to buttons
    const buttons = document.querySelectorAll('.btn, button');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Create ripple effect
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.cssText = `
                position: absolute;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.5);
                width: ${size}px;
                height: ${size}px;
                left: ${x}px;
                top: ${y}px;
                transform: scale(0);
                animation: ripple 0.6s linear;
                pointer-events: none;
            `;
            
            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);
            
            setTimeout(() => {
                if (ripple.parentNode) {
                    ripple.parentNode.removeChild(ripple);
                }
            }, 600);
        });
    });
    
    // Add ripple animation
    const rippleStyle = document.createElement('style');
    rippleStyle.textContent = `
        @keyframes ripple {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(rippleStyle);
    
    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
});