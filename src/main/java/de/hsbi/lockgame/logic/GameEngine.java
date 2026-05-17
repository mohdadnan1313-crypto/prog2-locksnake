package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.model.Position;
import de.hsbi.lockgame.model.Snake;
import de.hsbi.lockgame.ui.GamePanel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class GameEngine {
	private GameState state;
	private final List<Consumer<GameState>> observers = new ArrayList<>();

	public GameEngine(Level level) {
		List<Position> startBody = new ArrayList<>();
		startBody.add(level.snakeStart());
		Snake startSnake = new Snake(startBody);
		this.state = new GameState(level, startSnake, level.pins(), GameState.Status.RUNNING, Direction.NONE);
	}

	public GameState state() {
		return state;
	}

	public void setGamePanel(GamePanel panel) {
		observers.add(panel::update);
	}

	public void update(Direction d) {
		state = new GameState(state.level(), state.snake(), state.pins(), state.status(), d);
		notifyObservers();
	}

	public void tick() {
		state = state.tick();
		notifyObservers();
	}

	private void notifyObservers() {
		observers.forEach(o -> o.accept(state));
	}
}