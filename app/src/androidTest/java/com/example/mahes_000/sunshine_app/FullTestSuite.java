package com.example.mahes_000.sunshine_app;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by mahes_000 on 6/5/2016.
 */
public class FullTestSuite extends TestSuite
{
    public static Test suite()
    {
        return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }

    public FullTestSuite()
    {
        super();
    }
}
