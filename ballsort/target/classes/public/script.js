
document.addEventListener('DOMContentLoaded', () => {
    const gameBoard = document.getElementById('game-board');
    const moveCountElement = document.getElementById('move-count');
    const restartButton = document.getElementById('restart-button');
    const gameMessage = document.getElementById('game-message');

    let gameState = null;
    let selectedTubeIndex = null;

    initGame();

    restartButton.addEventListener('click', restartGame);

    function initGame() {
        fetch('/api/game/state')
            .then(response => response.json())
            .then(data => {
                gameState = data;
                renderGameBoard();
                updateMoveCount();
                checkGameCompletion();
            })
            .catch(error => {
                console.error('Erro ao inicializar o jogo:', error);
                gameMessage.textContent = 'Erro ao carregar o jogo. Tente novamente.';
            });
    }
    

    function restartGame() {
        fetch('/api/game/restart', { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                gameState = data;
                selectedTubeIndex = null;
                renderGameBoard();
                updateMoveCount();
                gameMessage.textContent = '';
            })
            .catch(error => {
                console.error('Erro ao reiniciar o jogo:', error);
                gameMessage.textContent = 'Erro ao reiniciar o jogo. Tente novamente.';
            });
    }

    function renderGameBoard() {
        gameBoard.innerHTML = '';

        gameState.tubes.forEach((tube, index) => {
            const tubeElement = document.createElement('div');
            tubeElement.className = 'tube';
            tubeElement.dataset.index = index;
            
            if (index === selectedTubeIndex) {
                tubeElement.classList.add('selected');
            }

            tube.forEach(color => {
                const ballElement = document.createElement('div');
                ballElement.className = `ball ${color}`;
                tubeElement.appendChild(ballElement);
            });

            tubeElement.addEventListener('click', () => handleTubeClick(index));

            gameBoard.appendChild(tubeElement);
        });
    }

    function handleTubeClick(tubeIndex) {
        if (selectedTubeIndex === null) {
            if (gameState.tubes[tubeIndex].length > 0) {
                selectedTubeIndex = tubeIndex;
                renderGameBoard();
            }
        } else {
            if (selectedTubeIndex !== tubeIndex) {
                moveBall(selectedTubeIndex, tubeIndex);
            } else {
                selectedTubeIndex = null;
                renderGameBoard();
            }
        }
    }

    function moveBall(fromIndex, toIndex) {
        const moveRequest = {
            fromTube: fromIndex,
            toTube: toIndex
        };
        
        fetch('/api/game/move', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(moveRequest)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                gameState = data.gameState;
                selectedTubeIndex = null;
                renderGameBoard();
                updateMoveCount();
                checkGameCompletion();
            } else {
                gameMessage.textContent = data.message;
                setTimeout(() => {
                    gameMessage.textContent = '';
                }, 2000);
            }
        })
        .catch(error => {
            console.error('Erro ao mover a bola:', error);
            gameMessage.textContent = 'Erro ao mover a bola. Tente novamente.';
        });
    }

    function updateMoveCount() {
        moveCountElement.textContent = gameState.moveCount;
    }

    function checkGameCompletion() {
        if (gameState.complete) {
            gameMessage.textContent = 'Parabéns! Você completou o jogo!';
        }
    }
});
