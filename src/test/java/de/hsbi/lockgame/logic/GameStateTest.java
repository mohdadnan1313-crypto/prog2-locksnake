package de.hsbi.lockgame.logic;

import static org.junit.jupiter.api.Assertions.*;

import de.hsbi.lockgame.model.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameStateTest {

	private Level level;
	private Snake snake;
	private List<Pin> pins;

	@BeforeEach
	void setUp() {
		CellType[][] cells = new CellType[10][10];
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				cells[x][y] = CellType.EMPTY;
			}
		}
		pins = new ArrayList<>();
		level = new Level(10, 10, cells, pins, new Position(5, 5));
		snake = new Snake(List.of(new Position(5, 5)));
	}

	@Test
	void testInitialStatusIsRunning() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.NONE);
		// when
		GameState.Status status = state.status();
		// then
		assertTrue(status.isRunning());
	}

	@Test
	void testTickWithNoneDirectionDoesNotMove() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.NONE);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(state.snake().head(), newState.snake().head());
	}

	@Test
	void testSnakeMoveRight() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.RIGHT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(new Position(6, 5), newState.snake().head());
	}

	@Test
	void testSnakeMoveLeft() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.LEFT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(new Position(4, 5), newState.snake().head());
	}

	@Test
	void testSnakeMoveUp() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.UP);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(new Position(5, 4), newState.snake().head());
	}

	@Test
	void testSnakeMoveDown() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.DOWN);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(new Position(5, 6), newState.snake().head());
	}

	@Test
	void testOutOfBoundsLosesGame() {
		// given
		Snake edgeSnake = new Snake(List.of(new Position(0, 0)));
		GameState state = new GameState(level, edgeSnake, pins, GameState.Status.RUNNING, Direction.LEFT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, newState.status());
	}

	@Test
	void testWallBlocksSnake() {
		// given
		CellType[][] cells = new CellType[10][10];
		for (int x = 0; x < 10; x++)
			for (int y = 0; y < 10; y++)
				cells[x][y] = CellType.EMPTY;
		cells[6][5] = CellType.WALL;
		Level wallLevel = new Level(10, 10, cells, pins, new Position(5, 5));
		GameState state = new GameState(wallLevel, snake, pins, GameState.Status.RUNNING, Direction.RIGHT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(new Position(5, 5), newState.snake().head());
	}

	@Test
	void testPinActivation() {
		// given
		Pin pin = new Pin(new Position(6, 5), Pin.State.LOW, Direction.RIGHT);
		List<Pin> pinsWithOne = new ArrayList<>();
		pinsWithOne.add(pin);
		GameState state = new GameState(level, snake, pinsWithOne, GameState.Status.RUNNING, Direction.RIGHT);
		// when
		GameState newState = state.tick();
		// then
		assertTrue(newState.pins().get(0).state().isSet());
	}

	@Test
	void testWinWhenAllPinsSet() {
		// given
		Pin pin = new Pin(new Position(6, 5), Pin.State.LOW, Direction.RIGHT);
		List<Pin> pinsWithOne = new ArrayList<>();
		pinsWithOne.add(pin);
		GameState state = new GameState(level, snake, pinsWithOne, GameState.Status.RUNNING, Direction.RIGHT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(GameState.Status.WON, newState.status());
	}

	@Test
	void testGameNotRunningDoesNotTick() {
		// given
		GameState state = new GameState(level, snake, pins, GameState.Status.WON, Direction.RIGHT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(new Position(5, 5), newState.snake().head());
	}

	@Test
	void testSelfCollisionLosesGame() {
		// given
		List<Position> body = new ArrayList<>();
		body.add(new Position(5, 5));
		body.add(new Position(4, 5));
		Snake longSnake = new Snake(body);
		GameState state = new GameState(level, longSnake, pins, GameState.Status.RUNNING, Direction.LEFT);
		// when
		GameState newState = state.tick();
		// then
		assertEquals(GameState.Status.LOST_SELF_COLLISION, newState.status());
	}
}