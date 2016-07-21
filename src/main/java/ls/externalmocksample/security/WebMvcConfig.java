package ls.externalmocksample.security;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    
    public void init() {
        requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);
    }


	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		msgConverter.setGson(gson);
		converters.add(msgConverter);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
		registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
		registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.mediaType("json", MediaType.APPLICATION_JSON);
	}

}