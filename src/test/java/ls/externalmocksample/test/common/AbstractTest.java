package ls.externalmocksample.test.common;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ls.externalmocksample.security.ApplicationContextConfig;

/**
 * The AbstractTest class is the parent of all JUnit test classes. This class
 * configures the test ApplicationContext and test runner environment.
 * 
 * @author Matt Warman
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)

public abstract class AbstractTest {
    

}
