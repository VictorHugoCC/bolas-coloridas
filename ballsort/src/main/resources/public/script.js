
document.addEventListener('DOMContentLoaded', () => {
    // Elementos do DOM
    const gameBoard = document.getElementById('game-board');
    const moveCountElement = document.getElementById('move-count');
    const restartButton = document.getElementById('restart-button');
    const gameMessage = document.getElementById('game-message');
    
    // Estado do jogo
    let gameState = null;
    let selectedTubeIndex = null;
    
    // Inicializa o jogo
    initGame();
    
    // Adiciona evento de clique ao botão de reiniciar
    restartButton.addEventListener('click', restartGame);
    
    /**
     * Inicializa o jogo obtendo o estado inicial do servidor.
     */
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
    
    /**
     * Reinicia o jogo.
     */
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
    
    /**
     * Renderiza o tabuleiro do jogo com base no estado atual.
     */
    function renderGameBoard() {
        // Limpa o tabuleiro
        gameBoard.innerHTML = '';
        
        // Cria os tubos
        gameState.tubes.forEach((tube, index) => {
            const tubeElement = document.createElement('div');
            tubeElement.className = 'tube';
            tubeElement.dataset.index = index;
            
            if (index === selectedTubeIndex) {
                tubeElement.classList.add('selected');
            }
            
            // Adiciona as bolas ao tubo
            tube.forEach(color => {
                const ballElement = document.createElement('div');
                ballElement.className = `ball ${color}`;
                tubeElement.appendChild(ballElement);
            });
            
            // Adiciona evento de clique ao tubo
            tubeElement.addEventListener('click', () => handleTubeClick(index));
            
            // Adiciona o tubo ao tabuleiro
            gameBoard.appendChild(tubeElement);
        });
    }
    
    /**
     * Manipula o clique em um tubo.
     * @param {number} tubeIndex - Índice do tubo clicado
     */
    function handleTubeClick(tubeIndex) {
        // Se nenhum tubo estiver selecionado, seleciona o tubo clicado
        if (selectedTubeIndex === null) {
            // Verifica se o tubo não está vazio
            if (gameState.tubes[tubeIndex].length > 0) {
                selectedTubeIndex = tubeIndex;
                renderGameBoard();
            }
        } else {
            // Se um tubo já estiver selecionado, tenta mover a bola
            if (selectedTubeIndex !== tubeIndex) {
                moveBall(selectedTubeIndex, tubeIndex);
            } else {
                // Se o mesmo tubo for clicado novamente, cancela a seleção
                selectedTubeIndex = null;
                renderGameBoard();
            }
        }
    }
    
    /**
     * Tenta mover uma bola do tubo de origem para o tubo de destino.
     * @param {number} fromIndex - Índice do tubo de origem
     * @param {number} toIndex - Índice do tubo de destino
     */
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
    
    /**
     * Atualiza o contador de movimentos.
     */
    function updateMoveCount() {
        moveCountElement.textContent = gameState.moveCount;
    }
    
    /**
     * Verifica se o jogo foi concluído.
     */
    function checkGameCompletion() {
        if (gameState.complete) {
            gameMessage.textContent = 'Parabéns! Você completou o jogo!';
        }
    }
});
