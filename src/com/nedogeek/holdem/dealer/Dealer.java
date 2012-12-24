package com.nedogeek.holdem.dealer;

import com.nedogeek.holdem.GameStatus;
import com.nedogeek.holdem.gamingStuff.Desk;

/**
 * User: Konstantin Demishev
 * Date: 05.10.12
 * Time: 22:02
 */
public class Dealer implements Runnable {
    private final Desk desk;

    private final MoveManager moveManager;
    private final NewGameSetter newGameSetter;
    private final PlayersManager playersManager;
    private final GameCycleManager gameCycleManager;
    private final EndGameManager endGameManager;

    Dealer(Desk desk) {
        this.desk = desk;
        moveManager = new MoveManager(desk);
        newGameSetter = new NewGameSetter(desk);
        playersManager = new PlayersManager(desk);
        gameCycleManager = new GameCycleManager(desk);
        endGameManager = new EndGameManager(desk);
    }

    Dealer(Desk deskMock, MoveManager moveManagerMock, NewGameSetter newGameSetterMock,
           PlayersManager playersManagerMock, GameCycleManager gameCycleManagerMock,
           EndGameManager endGameManagerMock) {
        desk = deskMock;
        moveManager = moveManagerMock;
        newGameSetter = newGameSetterMock;
        playersManager = playersManagerMock;
        gameCycleManager = gameCycleManagerMock;
        endGameManager = endGameManagerMock;
    }

    public void run() {
    	while (desk.getGameStatus() != GameStatus.NOT_READY){
    		tick();
    	}
    }

    void tick() {
        switch (desk.getGameStatus()) {
            case READY:
                gameCycleManager.prepareNewGameCycle();
                break;
            case STARTED:
                makeGameAction();
                break;
            case CYCLE_ENDED:
                gameCycleManager.endGameCycle();
                break;
        }
    }

    private void makeGameAction() {
        switch (desk.getGameRound()) {
            case INITIAL:
                newGameSetter.setNewGame();
                break;
            case FINAL:
                endGameManager.endGame();
                break;
            default:
                if (playersManager.hasAvailableMovers()) {
                    moveManager.makeMove(playersManager.getMoverNumber(), playersManager.getPlayerMove());
                } else {
                    desk.setNextGameRound();
                }
                break;
        }
    }
}
