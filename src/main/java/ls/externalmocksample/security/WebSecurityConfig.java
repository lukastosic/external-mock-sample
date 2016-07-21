package ls.externalmocksample.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

 
@Configuration
// @EnableWebSecurity = @EnableWebMVCSecurity + Extra features
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  
  
 
   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

   }
 
   @Override
   protected void configure(HttpSecurity http) throws Exception {
       http.csrf().disable();
   }
}
