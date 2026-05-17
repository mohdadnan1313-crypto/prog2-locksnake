package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import java.util.List;

public final class GameState {
	private final Level level;
	private final Snake snake;
	private final List<Pin> pins;
	private final Status status;
	private final Direction pendingDirection;

	public GameState(Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
		this.level = level;
		this.snake = snake;
		this.pins = pins;
		this.status = status;
		this.pendingDirection = pendingDirection;
	}

	public Level level() {
		return level;
	}

	public Snake snake() {
		return snake;
	}

	public List<Pin> pins() {
		return pins;
	}

	public Status status() {
		return status;
	}

	public Direction pendingDirection() {
		return pendingDirection;
	}

	public GameState tick() {
		if (!status.isRunning() || pendingDirection == Direction.NONE) {
			return this;
		}

		Position nextHead = snake.nextHead(pendingDirection);
		int x = nextHead.x();
		int y = nextHead.y();

		// (a) out of bounds
		if (!level.isInside(nextHead)) {
			return new GameState(level, snake, pins, Status.LOST_OUT_OF_BOUNDS, Direction.NONE);
		}

		// (b) wall: blocked
		if (level.cellAt(nextHead) == CellType.WALL) {
			return new GameState(level, snake, pins, status, Direction.NONE);
		}

		// (c) self collision
		if (snake.occupies(nextHead)) {
			return new GameState(level, snake, pins, Status.LOST_SELF_COLLISION, Direction.NONE);
		}

		// check pins
		for (int i = 0; i < pins.size(); i++) {
			Pin pin = pins.get(i);
			if (pin.position().equals(nextHead)) {
				if (pin.state().isSet() || pin.activationDirection() != pendingDirection) {
					return new GameState(level, snake, pins, status, Direction.NONE);
				}
				List<Pin> newPins = new java.util.ArrayList<>(pins);
				newPins.set(i, pin.withState(Pin.State.HIGH));
				boolean allSet = newPins.stream().allMatch(p -> p.state().isSet());
				Status newStatus = allSet ? Status.WON : status;
				return new GameState(level, snake, newPins, newStatus, Direction.NONE);
			}
		}

		// move snake
		Snake newSnake = snake.grow(pendingDirection);
		List<Position> newBody = new java.util.ArrayList<>(newSnake.body());
		newBody.remove(newBody.size() - 1);
		Snake movedSnake = new Snake(newBody);

		return new GameState(level, movedSnake, pins, status, pendingDirection);
	}

	public enum Status {
		RUNNING, WON, LOST_SELF_COLLISION, LOST_OUT_OF_BOUNDS;

		public boolean isRunning() {
			return this == RUNNING;
		}
	}
}