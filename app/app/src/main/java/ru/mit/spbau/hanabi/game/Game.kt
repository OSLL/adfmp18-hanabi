package ru.mit.spbau.hanabi.game

class Game(private val players: List<Player>) {
    fun run() {
        val gameState = GameState(players.size)
        gameState.printState()
        while (gameState.getState() == GameState.State.IN_PROGRESS) {
            val move = players[gameState.currentPlayer].makeMove(gameState)
            gameState.applyMove(move)
            for (player in players) {
                player.sendMoveNotification(/*player.getInfo, */ move)
            }

            gameState.printState()
        }
        for (player in players) {
            player.gameEnd(gameState)
        }
    }
}


fun main(args: Array<String>) {
    val game = Game(listOf(StupidAIPlayer(), StupidAIPlayer(), StupidAIPlayer()))
    game.run()
}
