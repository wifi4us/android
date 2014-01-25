package com.paad.wifi4us.utility.data;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.support.v4.util.SimpleArrayMap;


/**
 * a list of roots of xml file. the file is get from server.
 * this class aim to support multi-root xml file.
 * @author ys
 *
 */
public class FetchedInfo extends ArrayList<SimpleArrayMap<String, String>> {
	private static final long serialVersionUID = 1L;

	/**
	 * try add current element,and return whether there is another one after current one
	 * @param xpp
	 * @return if there is another element after current one
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public boolean addElementFromXml(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		SimpleArrayMap<String, String> element = new SimpleArrayMap<String, String>();
		int eventType = xpp.getEventType();
		boolean meetHead = false;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equals("result")) {
					if (meetHead) {
						add(element);
						// another element after this one
						return true;
					}
					meetHead = true;
					eventType = xpp.next();
					continue;
				}
				element.put(xpp.getName(), xpp.nextText());

			}
			eventType = xpp.next();
		}
		// no more root element
		return false;

	}
	
}
