package ls.externalmocksample.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import ls.externalmocksample.model.ResponseReturn;
import ls.externalmocksample.utils.Common;

@Controller
public class SampleController {
	
	@RequestMapping(value = "/helloworld", method = RequestMethod.POST)
	public ResponseEntity<ResponseReturn<String>> HelloWorldAction(HttpServletRequest request) {
		ResponseReturn<String> response = new ResponseReturn<String>();
		
		// Accept login token
		String token = request.getHeader("Token");
		
		// Make sure that token is even provided
		if(token == null){
			response.setMessage("Authorization token is not provided");
			return new ResponseEntity<ResponseReturn<String>>(response, HttpStatus.FORBIDDEN);
		}
		
		// Prepare call to Authentication server
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		String verifyEndpoint = "/verify";
		
		// URL is comprised from Common url that came from config file and from endpoint address
		// In case when Unit test is running Common url will be rewritten with WireMock address
		WebResource service = client.resource(UriBuilder.fromUri(Common.EXTERNAL_API_ADDRESS+verifyEndpoint).build());
		
		// Execute call to Authentication server with token
		try {
			ClientResponse authResponse = service.header("Token", token).post(ClientResponse.class);
			
			// Check response and do some business logic
			if(authResponse.getStatus() == 200)
			{
				// Here we can do some logic for /helloworld
				// We received response from external REST API with code 200 which means that token is recognized
				// For example, we can additionally check content for specific role
				
				response.setMessage("Call to external REST API server successful, good roken confirmation");
				return new ResponseEntity<ResponseReturn<String>>(response, HttpStatus.OK);
			}
			// Response from authentication server received, but token is not good
			else{
				response.setMessage("Call to external REST API server successful, but bad token confirmation");
				return new ResponseEntity<ResponseReturn<String>>(response, HttpStatus.FORBIDDEN);
			}
		}
		// Call to authentication server failed
		catch(Exception ex)
		{
			response.setMessage("Call to external REST API server not succeded");
			return new ResponseEntity<ResponseReturn<String>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
