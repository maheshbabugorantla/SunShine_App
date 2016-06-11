package com.example.mahes_000.sunshine_app.data;

/**
 * Created by mahes_000 on 6/5/2016.
 */

import android.test.AndroidTestCase;

public class TestPractice extends AndroidTestCase
{
    /*
        This gets run before every test.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testThatDemonstratesAssertions() throws Throwable
    {
        int a = 5;
        int b = 3;
        int c = 5;
        int d = 10;
        int e = 23;

        assertEquals("X should be equal", a, c);
        assertTrue("Y should be true", d > a);
        assertFalse("Z should be false", a == b);
        assertFalse("Gamma should be false",e < a);
        assertTrue("Alpha should be false",e > a);

        if (b > d)
        {
            fail("XX should never happen");
        }

        else if(d > e)
        {
            fail("d should never be greather than e");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}