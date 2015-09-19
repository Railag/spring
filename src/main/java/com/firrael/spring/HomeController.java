package com.firrael.spring;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private final static String HABR_HOST = "http://habrahabr.ru/rss";


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

		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<String> response = restTemplate
//				.getForEntity("https://data.sparkfun.com/streams/dZ4EVmE8yGCRGx5XRX1W.json", String.class);
		Rss response = restTemplate.getForObject(HABR_HOST, Rss.class);

		model.addAttribute("serverTime", response.toString());

//		logger.info(response.getBody());

		return "home";
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

}
