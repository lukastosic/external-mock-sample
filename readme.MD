# Sample REST API project with mock test

## Intro

Developing microservices can be difficult task because of lots of potential dependencies among services. 

One of the most common dependency sample is between authentication service and some other service.

Usually authentication service is "in the middle" of all other services and they use it all the time to verify login credentials (or login token).

This is sample REST API project with one endpoint that is dependent on external service.

Purpose of this project is to demonstrate mocking of external dependencies (external REST API).

## How to build and use the code

Clone this repository and browse the code.

Find the Test class to see how test works.

If you want to run this code, you can simply execute standard maven command to run tests:

```
mvn clean test
```

## Scenario

This REST API has endpoint called `/helloworld`. In order for code to be executed this endpoint must connect to external REST API endpoint and verify token from header.

If response from external API is good - code of `/helloworld` will continue and return `200 (OK)`, otherwise it will return `403 (FORBIDDEN)` 

## Where is the problem ?

Because of related external REST API, it is difficult to perform unit test of `/helloworld`. In order for successful unit test, developer would need to make sure that dependent API is also available.

## How to solve this ?

Solution is in using **MOCK** of the external REST API. Mock of external REST API is created before tests are executed. Configuration of external REST API is changed to point to mock server.

When tests are run:

* Mock server will be configured with mock endpoints (stubs)
* Mock server will run locally on the same machine that is performing test of local `/helloworld` logic
* Configuration of external REST API url will be changed with mock server url
* When test starts `/helloworld`, `/helloworld` will trigger external REST API, but this time, it will actually be executed on Mock server

This will ensure that unit tests can always run and developer (or Jenkins) don't care if dependent REST API is running or not.

_Sidenote_: There are also lots of good cloud offered mock services (for example http://getsandbox.com), and they do really good job and offer lots of ways to create your mocks. But still, you encounter issues like: service not available, can I do real stress testing, etc.

## Details

### Properties file

There is `env.properties` file located in `src/main/resources`. This file contains url that points to the external REST API.

Contents of `env.properties`:

```
externalApiUrl=http://api.externalservice.net
```

### Setting up environment

In the class `ApplicationContextConfig.java` there is added Bean that reads `env.properties` and loads parameter to common variable `EXTERNAL_API_ADDRESS`

Piece of code from `ApplicationContextConfig.java`:

```
@Bean(name="Url-mapper")
public String ReadExternalApiUrl() {
	String externalApiUrl = "http://api.externalservice.net";
	try{		   
		externalApiUrl = env.getProperty("externalApiUrl");
	}	   
	...	  
	Common.EXTERNAL_API_ADDRESS = externalApiUrl;	  
	...	  
	return externalApiUrl;	   
}	  
```

### Controller /helloworld setup

If you look in the code of `/helloworld` endpoint code, you will see several things:

Check for token at the beginning:

```
// Accept login token
String token = request.getHeader("Token");
		
// Make sure that token is even provided
if(token == null){
    response.setMessage("Authorization token is not provided");
    return new ResponseEntity<ResponseReturn<String>>(response, HttpStatus.FORBIDDEN);
}
```

If token is there - prepare REST client for external call, build up url from common variable + specific endpoint address (_This can be done in different way, there are lots of different REST client implementations, you can use whatever you want, this is just proof of concept_ ) :

```
// Prepare call to Authentication server
ClientConfig config = new DefaultClientConfig();
Client client = Client.create(config);
String verifyEndpoint = "/verify";
		
// URL is comprised from Common url that came from config file and from endpoint address
// In case when Unit test is running Common url will be rewritten with WireMock address
WebResource service = client.resource(UriBuilder.fromUri(Common.`EXTERNAL_API_ADDRESS` + verifyEndpoint).build());
```

When REST client is prepared, it will execute POST method to external API with token in header (_Again, keep in mind that this is just simple example, you can do a lot more with response information_)

```
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
```

## Unit test setup

Now we need to test our own REST API (in this case endpoint /helloworld), but we need to mock that external API.

### Initialize WireMock
In this case we are using `WireMock`. We need to setup `@Rule` part to initialize `WireMock`. We will use `dynamicPort` option, that will guarantee that WireMock will always start (on random port) and avoid any potential conflicts that might happen on machine that will run the test (developer or Jenkins).

```
@Rule
public WireMockRule externalMockService= new WireMockRule(wireMockConfig().dynamicPort());
```

Note how we named our rule with name `externalMockService`. This means that we can have many other mock services. So it doesn't matter if our project have only one external REST API dependency or many, we can just start up multiple mock services.

### Get the address of WireMock and replace external REST API address

Now we need to replace external REST API (variable `EXTERNAL_API_ADDRESS` from `Common`) with our `WireMock` address.

`WireMock` will always run on `http://localhost` but in our case port is dynamically set.

```
private void setupExternalAPIMockAddress() {				
   Common.EXTERNAL_API_ADDRESS = "http://localhost:"+String.valueOf(externalMockService.port());
}
```

### Setup WireMock stubs

So, now we have initialized `WireMock` and we replaced address, but still `WireMock` doesn't have any stubs/mocks defined.

We will create subs for:

* `POST` to endpoint `/verify` without token - this will respond with `403 (FORBIDDEN)`
* `POST` to endpoint `/verify` with token `good-token` - this will respond with `200 (OK)` 
* `POST` to endpoint `/verify` with token `bad-token` - this will respond with `403 (FORBIDDEN`)

First stub without token is actually not necessary, because `/helloworld` execute check for token before it even calls external REST API.

Code for making stubs:

```
private void populateAuthMockServiceWithPaths() {		
    externalMockService.resetMappings();    
    // stub for POST request without token
    externalMockService.stubFor(post(urlEqualTo("/verify"))	            
        .willReturn(aResponse()
            .withStatus(403)));    
    // stub for POST request with good token
    externalMockService.stubFor(post(urlEqualTo("/verify"))	            
        .withHeader("Token", equalTo("good-token"))
            .willReturn(aResponse()
                .withStatus(200)));
    // stub for POST request with bad token
    externalMockService.stubFor(post(urlEqualTo("/verify"))	            
        .withHeader("Token", equalTo("bad-token"))
	    .willReturn(aResponse()
                .withStatus(403)));	
}
```

### Prepare test - `@Before` part

In the `@Before` part we have spring initialization and calling 2 methods for preparing `WireMock` address and stubs.

```
@Before
public void setUp() {
    super.setUp();
    populateAuthMockServiceWithPaths();
    setupExternalAPIMockAddress();
}
```

### Actual test case (one of)

This is sample test that sends "good-token" to `/helloworld`. 

```
@Test
public final void testHelloWorldActionWithHeaderGoodToken() throws Exception {
	String uri = "/helloworld";
	MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).header("Token", "good-token")).andReturn();
	int status = result.getResponse().getStatus();
	Assert.assertEquals("Expected HTTP status 200", 200, status);
}
```

## Conclusion

This project sample show how our rest api service is dependent on external service. 

We are encountering the problem if we want to perform unit test, because we don't have proper "influence" on external service (it can be down, it is maybe not developed/deployed at all), so our unit tests of our internal service can fail only because external service might not be available.

To solve this, we create mock service that we initialize on unit tests and we override external service access url with our own mock service.

This will ensure that we always have dependant service up and running and we can always execute our unit tests.

## Point to think about

You could have your environment setup in a way where you have to develop multiple micro services and they all depend on at least one "central" service (authentication / login).

In this case, it might be good idea to develop actual project that will serve as "envelop" around `WireMock` and have it populated with that "central" service mock.

This project can be published on public/private maven repository and then it can be used as dependency in your project. That way when you are building unit test you just initiate you "enveloped" `WireMock` project and it will already have all stubs defined.

This will remove need not only to copy-paste stub definitions across multiple projects, but it will also help with updates. If real "central" service is updated, then you only need to update "envelop" project and update dependency. Otherwise you would have to change your stub definitions multiple times.

## Used for development

Code is created using:

* Eclipse
* Java JDK 1.8
* Maven 3.3
* SPRING framework
* SureFire for UnitTests
* WireMock for mocking external service