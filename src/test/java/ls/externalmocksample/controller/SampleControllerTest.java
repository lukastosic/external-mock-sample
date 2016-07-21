/**
 * 
 */
package ls.externalmocksample.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import ls.externalmocksample.test.common.AbstractControllerTest;
import ls.externalmocksample.utils.Common;

/**
 * @author lstosic
 *
 */
public class SampleControllerTest extends AbstractControllerTest {

	@Before
	public void setUp() {
		super.setUp();
		populateAuthMockServiceWithPaths();
		setupExternalAPIMockAddress();
	}
	
	@Rule
	public WireMockRule authMockService = new WireMockRule(wireMockConfig().dynamicPort());
	
	// Fill wire mock with stubs
	private void populateAuthMockServiceWithPaths() {		
		
		authMockService.resetMappings();
		
		// Mock request coming to external "verify" endpoint
		// Match any POST request to this url
		// Respond with 403 (FORBIDDEN), because there is no token supplied 
		authMockService.stubFor(post(urlEqualTo("/verify"))	            
	            .willReturn(aResponse()
	                .withStatus(403)));	
		
		// Mock request coming to external "verify" endpoint
		// Match POST requests containing header parameter "Token" that matches string "good-token"
		// Respond with status 200 (OK)
		authMockService.stubFor(post(urlEqualTo("/verify"))	            
	            .withHeader("Token", equalTo("good-token"))
				.willReturn(aResponse()
	                .withStatus(200)));
		
		// Mock request coming to external "verify" endpoint
		// Match POST requests containing header parameter "Token" that matches string "bad-token"
		// Respond with status 403 (FORBIDDEN)
		authMockService.stubFor(post(urlEqualTo("/verify"))	            
	            .withHeader("Token", equalTo("bad-token"))
				.willReturn(aResponse()
	                .withStatus(403)));	
	}
	
	// Because our WireMock rule is using dynamic port we need to get the port that is used on start
	// Now we can "build" url, actually it is just localhost:port
	// Then we store that url to our common variable that will be used by real call
	// This will make sure that real call will use mock url.
	private void setupExternalAPIMockAddress() {				
		Common.EXTERNAL_API_ADDRESS = "http://localhost:"+String.valueOf(authMockService.port());
	}
	
	/**
	 * Test method for {@link ls.externalmocksample.controller.SampleController#HelloWorldAction(javax.servlet.http.HttpServletRequest)}.
	 * @throws Exception 
	 */
	
	// This test is trying out to call /helloworld with good token information
	// When /helloworld is triggered - it will trigger internally /verify action to "external" API
	// But now because we have WireMock running - that "external" API is actually our WireMock instance
	// We are sending "good-token" which means that WireMock will respond with 200, and then helloworld will respond with 200
	@Test
	public final void testHelloWorldActionWithHeaderGoodToken() throws Exception {
		String uri = "/helloworld";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).header("Token", "good-token")).andReturn();
		int status = result.getResponse().getStatus();
		Assert.assertEquals("Expected HTTP status 200", 200, status);
	}

	// This test is trying out to call /helloworld with good token information
	// When /helloworld is triggered - it will trigger internally /verify action to "external" API
	// But now because we have WireMock running - that "external" API is actually our WireMock instance
	// We are sending "bad-token" which means that WireMock will respond with 403, and then helloworld will respond with 403
	@Test
	public final void testHelloWorldActionWithHeaderBadToken() throws Exception {
		String uri = "/helloworld";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).header("Token", "bad-token")).andReturn();
		int status = result.getResponse().getStatus();
		Assert.assertEquals("Expected HTTP status 403", 403, status);
	}
	
	
	// This test is trying out to call /helloworld with good token information
	// When /helloworld is triggered - it should fail because we didn't provide it with header token
	// /helloworld should fail with code 403 (not even reaching /verify call)
	@Test
	public final void testHelloWorldActionWithoutHeader() throws Exception {
		String uri = "/helloworld";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();		
		int status = result.getResponse().getStatus();
		Assert.assertEquals("Expected HTTP status 403", 403, status);
	}

}
