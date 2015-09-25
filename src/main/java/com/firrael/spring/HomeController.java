package com.firrael.spring;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.firrael.spring.xml.Article;
import com.firrael.spring.xml.Channel;
import com.firrael.spring.xml.Rss;
import com.firrael.spring.xml.XmlConverter;
import com.firrael.spring.xml.XmlParser;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private final static String HABR_HOST = "http://habrahabr.ru/rss";

	private static final String XML_FILE_NAME = "rss.xml";

	@Autowired
	private ApplicationContext context;

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info(String.format("Welcome home! The client locale is {}.", locale));

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		// restTemplate.setMessageConverters(messageConverters);
		// ArrayList<Http>

		// ResponseEntity<String> response = restTemplate
		// .getForEntity("https://data.sparkfun.com/streams/dZ4EVmE8yGCRGx5XRX1W.json",
		// String.class);

		// ClientHttpResponse response = null;

		// HttpComponentsClientHttpRequestFactory factory =
		// httpRequestFactory();
		// try {
		RestTemplate template = new RestTemplate();
		template.setMessageConverters(getMessageConverters());
		Rss rss = null;
		try {
			rss = template.getForObject(HABR_HOST, Rss.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String responseString = template.getForObject(HABR_HOST,
		// String.class);

		// ClientHttpRequest request =
		// factory.createRequest(URI.create(HABR_HOST), HttpMethod.GET);
		// response = request.execute();
		// logger.info(response.getBody());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// try {
		model.addAttribute("serverTime", rss.toString());
		// model.addAttribute("serverTime",
		// convertStreamToString(response.getBody()));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// logger.info(response.getBody());

		return "home";
	}

	private List<HttpMessageConverter<?>> getMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
		// XmlParser parser = getParser();
		// XStreamMarshaller marshaller = new XStreamMarshaller();
		// Class[] supportedClasses = new Class[1];
		// supportedClasses[0] = Rss.class;
		// marshaller.setSupportedClasses(supportedClasses);
		// parser.setMarshaller(marshaller);
		// XmlParser parser = context.getBean(XmlParser.class);

		Rss rss = new Rss();
		Channel channel = new Channel();
		ArrayList<Article> item = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			Article article = new Article();
			article.setAuthor("auth " + i);
			article.setCategory(new ArrayList<String>());
			article.setDescription("descr " + i);
			article.setGuid("guid " + i);
			article.setLink("http://google.com");
			article.setPubDate("pubDate " + i);
			article.setTitle("title " + i);
			item.add(article);
		}

		channel.setItem(item);
		rss.setChannel(channel);
		// StreamResult dest = null;
		// try {
		// dest = new StreamResult(new FileWriter("C:\\RAILAG\rss.xml"));
		// dest.getWriter().write("testtt");
		//
		// marshaller.marshal(rss, dest);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		XmlConverter conv = (XmlConverter) context.getBean("XmlConverter");
		CastorMarshaller unmarshaller = (CastorMarshaller) conv.getUnmarshaller();
		unmarshaller.setIgnoreExtraAttributes(true);
		unmarshaller.setIgnoreExtraElements(true);

		System.out.println("Convert Object to XML!");
		// from object to XML file
		try {
			conv.convertFromObjectToXML(rss, XML_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done \n");

		System.out.println("Convert XML back to Object!");
		// from XML to object
		Rss rss2 = null;
		try {
			rss2 = (Rss) conv.convertFromXMLToObject(XML_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(rss2);
		System.out.println("Done");

		converter.setMarshaller(conv.getMarshaller());
		converter.setUnmarshaller(conv.getUnmarshaller());
		converters.add(converter);

		// converters.add(new MappingJackson2XmlHttpMessageConverter())
		return converters;
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private static XmlParser getParser() {
		return new XmlParser();
	}

	// static class MyGsonHttpMessageConverter extends
	// MappingJacksonHttpMessageConverter {
	// public MyGsonHttpMessageConverter() {
	// List<MediaType> types = Arrays.asList(
	// new MediaType("text", "html"),
	// new MediaType("application", "json"),
	// new MediaType("application", "*+json")
	// );
	// super.setSupportedMediaTypes(types);
	// }
	// }
	//
	// @Bean
	// public RestTemplate restTemplate() {
	// RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
	// List<HttpMessageConverter<?>> converters =
	// restTemplate.getMessageConverters();
	// for (HttpMessageConverter<?> converter : converters) {
	// if (converter instanceof MappingJacksonHttpMessageConverter) {
	// MappingJacksonHttpMessageConverter jsonConverter =
	// (MappingJacksonHttpMessageConverter) converter;
	// // jsonConverter.setObjectMapper(new ObjectMapper());
	// jsonConverter.setSupportedMediaType(ImmutableList.of(new
	// MediaType("application", "json",
	// MappingJacksonHttpMessageConverter.DEFAULT_CHARSET), new
	// MediaType("text", "javascript",
	// MappingJacksonHttpMessageConverter.DEFAULT_CHARSET)));
	// }
	// }
	// return restTemplate;
	// }
	//

	// @Bean
	// public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
	// HttpComponentsClientHttpRequestFactory factory = new
	// HttpComponentsClientHttpRequestFactory();
	// //factor.setHttpClient();//factory = new
	// SimpleClientHttpRequestFactory();
	// // HttpClient httpClient = new org.springframework.http.client.
	// return factory;//return factory;
	// }

	// @Bean
	// public HttpClient httpClient() {
	// PoolingHttpClientConnectionManager connectionManager = new
	// PoolingHttpClientConnectionManager();
	// CloseableHttpClient closeableHttpClient =
	// HttpClientBuilder.create().setConnectionManager(connectionManager).build();
	// connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
	// connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
	// connectionManager.setMaxPerRoute(new HttpRoute(new
	// HttpHost("facebook.com")), 20);
	// connectionManager.setMaxPerRoute(new HttpRoute(new
	// HttpHost("twitter.com")), 20);
	// connectionManager.setMaxPerRoute(new HttpRoute(new
	// HttpHost("linkedin.com")), 20);
	// connectionManager.setMaxPerRoute(new HttpRoute(new
	// HttpHost("viadeo.com")), 20);
	// connectionManager.setMaxPerRoute(new HttpRoute(new
	// HttpHost("api.tripadvisor.com")), 20);
	// return closeableHttpClient;
	// }
	//
}
