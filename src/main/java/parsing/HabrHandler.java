package parsing;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.firrael.spring.data.Article;

public class HabrHandler extends DefaultHandler2 {

	private final static String ITEM = "item";
	private final static String ITEM_START_TAG = "<item>";
	private final static String ITEM_END_TAG = "</item>";

	private ArrayList<Article> articles;

	private ArticleHandler articleHandler;

	// basic
	private boolean isItem;

	public HabrHandler() {
		articles = new ArrayList<>();
		articleHandler = new ArticleHandler();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		isItem = checkElement(ITEM, qName);
	}

	private boolean checkElement(String itemName, String currentName) {
		return currentName.equalsIgnoreCase(itemName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (isItem) {
			int end = new String(ch).indexOf(ITEM_END_TAG, start);
			if (end == -1)
				return;

			String articleXml = new String(ch, start, end - start);

			articleXml = wrapXml(articleXml);
			
			System.out.println(articleXml);

			try {
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				parser.parse(new InputSource(new StringReader(articleXml)), articleHandler);
				articles.add(articleHandler.getArticle());
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			isItem = false;
		}
	}

	private String wrapXml(String xml) {
		xml = ITEM_START_TAG + xml + ITEM_END_TAG;
		xml = xml.replace("\n", "").replace("\t", "") // for escape chars
				.replace("/</", "</"); // for http://example.com/</link> case
		return xml;
	}

	@Override
	public void startCDATA() throws SAXException {
		super.startCDATA();
	}

	@Override
	public void endCDATA() throws SAXException {
		super.endCDATA();
	}

	public List<Article> getArticles() {
		return articles;
	}

}
