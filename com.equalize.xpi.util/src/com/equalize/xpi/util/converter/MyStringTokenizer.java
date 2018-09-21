package com.equalize.xpi.util.converter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

//Modified copy of com.sap.aii.af.sdk.xi.adapter.MyStringTokenizer
public class MyStringTokenizer {
	private boolean isStandard;
	StringTokenizer st;
	ArrayList<String> arr;
	int index;
	String enclBegin;
	String enclEnd;
	String enclBeginEsc;
	String enclEndEsc;

	public MyStringTokenizer(String myLine, String columnSeparatorChar, String enclBegin, String enclEnd, String enclBeginEsc, String enclEndEsc, boolean returnSeparator)
	{
		this.enclBegin = enclBegin;
		this.enclEnd = enclEnd;
		this.enclBeginEsc = enclBeginEsc;
		this.enclEndEsc = enclEndEsc;
		this.isStandard = false;
		if (this.isStandard)
		{
			this.st = new StringTokenizer(myLine, columnSeparatorChar, returnSeparator);
			this.arr = null;
		}
		else
		{
			this.st = null;
			this.arr = new ArrayList<String>();
			this.index = 0;
			boolean cont = true;
			String line = myLine;
			//boolean inEnclosure = false;
			int fromIndex = 0;
			while (cont)
			{
				int sepInd = 0;
				if ((sepInd = line.indexOf(columnSeparatorChar, fromIndex)) != -1)
				{
					if (!(SepInsideEncl(sepInd, line)))
					{
						this.arr.add(line.substring(0, sepInd));
						this.arr.add(line.substring(sepInd, sepInd + columnSeparatorChar.length()));
						line = line.substring(sepInd + columnSeparatorChar.length());
						fromIndex = 0;
					}
					else
					{
						fromIndex = sepInd + 1;
					}
				}
				else
				{
					if (fromIndex < line.length()) {
						this.arr.add(line);
					}
					cont = false;
				}
			}
		}
	}

	public int countTokens()
	{
		if (this.isStandard) {
			return this.st.countTokens();
		}
		return this.arr.size();
	}

	public Object nextElement()
	{
		if (this.isStandard) {
			return this.st.nextElement();
		}
		this.index += 1;
		return this.arr.get(this.index - 1);
	}

	private boolean SepInsideEncl(int pos, String str)
	{
		int i = 0;
		int level = 0;
		boolean inBegin = false;
		while (i < pos) {
			if (!inBegin)
			{
				if (str.startsWith(this.enclBegin, i))
				{
					i += this.enclBegin.length();
					inBegin = true;
					level++;
				}
				else
				{
					i++;
				}
			}
			else
			{
				int ii = i;
				if ((this.enclBeginEsc.length() > 0) && (str.startsWith(this.enclBeginEsc, i))) {
					i += this.enclBeginEsc.length();
				}
				if ((this.enclEndEsc.length() > 0) && (str.startsWith(this.enclEndEsc, i))) {
					i += this.enclEndEsc.length();
				}
				if (str.startsWith(this.enclEnd, i))
				{
					i += this.enclEnd.length();
					inBegin = false;
					level--;
				}
				if (ii == i) {
					i++;
				}
			}
		}
		return level != 0;
	}

	public String convertEncls(String str)
	{
		StringWriter swr = new StringWriter();
		int i = 0;
		int level = 0;
		boolean inBegin = false;
		int startInd = 0;
		while (i < str.length()) {
			if (!inBegin)
			{
				if (str.startsWith(this.enclBegin, i))
				{
					swr.write(str.substring(startInd, i));
					i += this.enclBegin.length();
					startInd = i;
					inBegin = true;
					level++;
				}
				else
				{
					i++;
				}
			}
			else
			{
				int ii = i;
				if ((this.enclBeginEsc.length() > 0) && (str.startsWith(this.enclBeginEsc, i)))
				{
					swr.write(str.substring(startInd, i));
					swr.write(this.enclBegin);
					i += this.enclBeginEsc.length();
					startInd = i;
				}
				if ((this.enclEndEsc.length() > 0) && (str.startsWith(this.enclEndEsc, i)))
				{
					swr.write(str.substring(startInd, i));
					swr.write(this.enclEnd);
					i += this.enclEndEsc.length();
					startInd = i;
				}
				if (str.startsWith(this.enclEnd, i))
				{
					swr.write(str.substring(startInd, i));
					i += this.enclEnd.length();
					startInd = i;
					inBegin = false;
					level--;
				}
				if (ii == i) {
					i++;
				}
			}
		}
		swr.write(str.substring(startInd, i));
		return swr.toString();
	}
}