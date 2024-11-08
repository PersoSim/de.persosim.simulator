package de.persosim.simulator.perso.export;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class ProfilePrettyPrinter extends DefaultPrettyPrinter
{

	private static final long serialVersionUID = 8527365018553627762L;

	public ProfilePrettyPrinter()
	{
		super();
	}

	@Override
	public DefaultPrettyPrinter createInstance()
	{
		return new ProfilePrettyPrinter();
	}


	@Override
	public void beforeObjectEntries(JsonGenerator g) throws IOException
	{
		_objectIndenter.writeIndentation(g, _nesting);
	}


	@Override
	public void writeObjectEntrySeparator(JsonGenerator g) throws IOException
	{
		// g.writeRaw(_objectEntrySeparator);
		g.writeRaw(", ");
		// _objectIndenter.writeIndentation(g, _nesting);
	}


	// @Override
	// public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
	// jg.writeRaw(": XXXXXXXXXXXXXXXXX");
	// }

	//
	// @Override
	// public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
	// g.writeRaw("WWWWWWWWWWW");//_arrayValueSeparator);
	// _arrayIndenter.writeIndentation(g, _nesting);
	// }

}
