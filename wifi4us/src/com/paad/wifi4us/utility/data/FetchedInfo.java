package com.paad.wifi4us.utility.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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
						// another element afer this one
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
	
	public static void main(String[] args) throws Exception {
	    String  str= "<results>\n<record>\n<state>0</state>\n<account>7.68</account>\n</record>\n<results>\n"
	        +"<results>\n<record>\n<abc>0</abc>\n<account>7.68</account>\n</record>\n<results>";
	    ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());

            XmlPullParserFactory factory = XmlPullParserFactory
                            .newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(in, null);
            FetchedInfo result = new FetchedInfo();
            while(result.addElementFromXml(xpp));
            in.close();
            System.out.println(result.get(0).containsKey("state"));
            System.out.println(result.get(0).containsKey("account"));
            System.out.println(result.get(1).containsKey("abc"));
            System.out.println(result.get(1).containsKey("account"));
        }
}
