package de.persosim.simulator;

public class CommandParserResult
{
	private boolean ok;
	private String message;
	private String resultAsHex;
	private Exception exception;

	public CommandParserResult(boolean ok, String message)
	{
		this.ok = ok;
		this.message = message;
	}

	public CommandParserResult(boolean ok, String message, String resultAsHex)
	{
		this.ok = ok;
		this.message = message;
		this.resultAsHex = resultAsHex;
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

	public String getResultAsHex()
	{
		return resultAsHex;
	}

	public Exception getException()
	{
		return exception;
	}
}
