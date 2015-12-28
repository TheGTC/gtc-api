package uk.org.gtc.api.domain;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class LocationTypeTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public LocationTypeTest(String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(LocationTypeTest.class);
	}
	
	public void testValues() throws Exception
	{
		assertEquals("HOME", LocationType.HOME.toString());
		assertEquals("WORK", LocationType.WORK.toString());
		assertEquals("MOBILE", LocationType.MOBILE.toString());
		
		LocationType[] locationTypes = LocationType.values();
		assertEquals(3, locationTypes.length);
		
		assertEquals(LocationType.HOME, LocationType.valueOf("HOME"));
		assertEquals(LocationType.WORK, LocationType.valueOf("WORK"));
		assertEquals(LocationType.MOBILE, LocationType.valueOf("MOBILE"));
	}
	
}