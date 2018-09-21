package com.equalize.xpi.util.converter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

// Modified copy of com.sap.aii.af.sdk.xi.adapter.trans.Separator
public class Separator
{
	boolean endsWithNewLine;
	String errorString;
	String sepStr;
	public static String newLine = System.getProperty("line.separator");

	public Separator(String sepStr)
	{
		this(sepStr, null);
	}

	public Separator(String sepStr, String encoding)
	{
		int ind = 0;

		this.errorString = null;
		this.sepStr = sepStr;
		if (sepStr.endsWith("'nl'"))
		{
			sepStr = sepStr.substring(0, sepStr.length() - 4);
			this.endsWithNewLine = true;
		}
		else
		{
			this.endsWithNewLine = false;
		}
		if (sepStr.length() > 0) {
			if (sepStr.indexOf("'0x") != -1)
			{
				String helper = new String("");
				while ((ind = sepStr.indexOf("'0x")) != -1)
				{
					String sVal = sepStr.substring(ind + 3, ind + 5);

					char specialChar = (char)Integer.parseInt(sVal, 16);
					String sSpecialChar = String.valueOf(specialChar);
					String s;
					if (encoding != null) {
						try
						{
							CharBuffer data = CharBuffer.wrap(new char[] { specialChar });
							Charset charset = Charset.forName(encoding);
							ByteBuffer bb = charset.encode(data);
							sSpecialChar = new String(bb.array(), encoding);
						}
						catch (Exception e)
						{
							s = e.getMessage();
						}
					}
					helper = new String(helper + sepStr.substring(0, ind) + sSpecialChar);
					if (sepStr.length() > ind + 6) {
						sepStr = sepStr.substring(ind + 6);
					} else {
						sepStr = "";
					}
				}
				this.sepStr = new String(helper + sepStr + (this.endsWithNewLine ? newLine : ""));
			}
			else
			{
				this.sepStr = new String(sepStr + (this.endsWithNewLine ? newLine : ""));
			}
		}
	}

	String getError()
	{
		return this.errorString;
	}

	public String toString()
	{
		return this.sepStr;
	}
}
