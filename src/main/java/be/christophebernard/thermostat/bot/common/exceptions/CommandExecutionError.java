package be.christophebernard.thermostat.bot.common.exceptions;

public class CommandExecutionError extends RuntimeException {
	public CommandExecutionError(String message) {
		super(message);
	}

	public CommandExecutionError(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandExecutionError(Throwable cause) {
		super(cause);
	}
}
