package de.persosim.simulator;

public class CommandParserResult
{
	private boolean ok;
	private String message;
	private Exception exception;

	public CommandParserResult(boolean ok, String message)
	{
		this.ok = ok;
		this.message = message;
	}

	public CommandParserResult(boolean ok, String message, Exception exception)
	{
		this.ok = ok;
		this.message = message;
		this.exception = exception;
	}

	public boolean isOk()
	{
		return ok;
	}

	public String getMessage()
	{
		return message;
	}

	public Exception getException()
	{
		return exception;
	}
}
