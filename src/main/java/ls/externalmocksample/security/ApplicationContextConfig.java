package ls.externalmocksample.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ls.externalmocksample.utils.Common;
 
@Configuration
@ComponentScan("ls.externalmocksample.*")
@EnableTransactionManagement
// Load to Environment.
@PropertySource("classpath:env.properties")
public class ApplicationContextConfig {
 
   // Store all paramater load by @PropertySource.
   @Autowired
   private Environment env;
 
   @Bean
   public ResourceBundleMessageSource messageSource() {
       ResourceBundleMessageSource rb = new ResourceBundleMessageSource();
       // Load property in message/validator.properties
       rb.setBasenames(new String[] { "messages/validator" });
       return rb;
   }
   
   @Bean(name="Url-mapper")
   public String ReadExternalApiUrl() {
	   
	   String externalApiUrl = "http://api.externalservice.net";
	   
	   try{		   
		   externalApiUrl = env.getProperty("externalApiUrl");
	   }
	   catch(Exception ex) {
		   System.out.println("## External REST API address not found in properties file, falling back to default");
	   }
	   
	   Common.EXTERNAL_API_ADDRESS = externalApiUrl;
	   
	   System.out.println("## External REST API address: " + Common.EXTERNAL_API_ADDRESS);
	   
	   return externalApiUrl;
	   
   }
   
  
}
