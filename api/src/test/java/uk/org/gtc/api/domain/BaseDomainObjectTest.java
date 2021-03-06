package uk.org.gtc.api.domain;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class BaseDomainObjectTest extends TestCase
{
    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(BaseDomainObjectTest.class);
    }

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public BaseDomainObjectTest(final String testName)
    {
        super(testName);
    }

    public void testBlankConstructor() throws Exception
    {
        final BaseDomainObject bdo = new BaseDomainObject();
        assertEquals(new BaseDomainObject(), bdo);
    }

    public void testSettersGetters() throws Exception
    {
        final BaseDomainObject bdo = new BaseDomainObject();
        bdo.setId("1234");
        assertEquals("1234", bdo.getId());
    }

}
