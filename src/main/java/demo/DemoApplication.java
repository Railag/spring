package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class DemoApplication {
	
	@RequestMapping("/")
    String home() {
        return "Hello World!";
    }


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        ApplicationContext context = 
                new ClassPathXmlApplicationContext("Beans.xml");

         HelloWorld obj = (HelloWorld) context.getBean("helloWorld");

         obj.getMessage();
    }
   
        
}
