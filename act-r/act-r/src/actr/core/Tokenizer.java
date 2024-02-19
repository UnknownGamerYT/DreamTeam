package actr.core;

import java.io.*;
import java.net.*;
import java.util.Vector;

class Tokenizer 
{
	private Reader reader = null;
	private int c = 0;
	private int line = 1;
	private String token = "";
	private Vector<String> putbacks = new Vector<String>();

	public Tokenizer (File file) throws FileNotFoundException
	{
		reader = new FileReader (file);
		readChar();
		while (c!=-1 && Character.isWhitespace(c)) readChar();
		advance();
	}

	public Tokenizer (URL url) throws IOException
	{
		reader = new BufferedReader (new InputStreamReader (url.openStream()));
		readChar();
		while (c!=-1 && Character.isWhitespace(c)) readChar();
		advance();
	}

	public Tokenizer (String s)
	{
		reader = new StringReader (s);
		readChar();
		while (c!=-1 && Character.isWhitespace(c)) readChar();
		advance();
	}

	public boolean hasMoreTokens ()
	{
		return (c != -1) || !putbacks.isEmpty();
	}

	public String getToken () { return token; }

	public boolean isLetterToken ()
	{
		return !token.isEmpty() && Character.isLetter(token.charAt(0));
	}

	int getLine ()
	{
		return line;
	}

	void readChar ()
	{
		try { c = reader.read(); }
		catch (IOException exc) { System.err.println ("IOException: " + exc.getMessage()); }
		if (c=='\n' || c=='\r') line++;
	}

	boolean isSpecial (int c2)
	{
		return c2=='(' || c2==')';
	}

	public void advance ()
	{
		if (!putbacks.isEmpty())
		{
			token = putbacks.elementAt(0);
			putbacks.removeElementAt(0);
			return;
		}
		
		StringWriter sr = new StringWriter ();

		while (c!=-1 && (c==';' || c=='#'))
		{
			if (c==';')
			{
				while (c!=-1 && c!='\n' && c!='\r') readChar();
			}
			else if (c=='#')
			{
				if (c!=-1) readChar(); // '#'
				if (c!=-1) readChar(); // '|'
				while (c!=-1 && c!='|') readChar();
				if (c!=-1) readChar(); // '|'
				if (c!=-1) readChar(); // '#'
			}
			while (c!=-1 && Character.isWhitespace(c)) readChar();
		}

		if (isSpecial(c))
		{
			sr.write (c);
			readChar();
		}
		else
		{
			while (c!=-1 && !Character.isWhitespace(c) && !isSpecial(c))
			{
				sr.write (c);
				readChar();
			}
		}
		while (c!=-1 && Character.isWhitespace(c)) readChar();

		token = sr.toString().toLowerCase();
		//System.out.println ("-" + token + "-");
	}
	
	void pushBack (String old)
	{
		putbacks.add (token);
		token = old;
	}
}