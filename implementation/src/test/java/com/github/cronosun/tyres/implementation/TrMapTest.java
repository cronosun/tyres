package com.github.cronosun.tyres.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrMapTest {

    @Test
    void runHashMapPerformanceTest() {
        var testable = TestableCreator.generateTestableForHashMap(100);
        runPerformanceTest(testable);
    }

    @Test
    void runTyMapMapPerformanceTest() {
        var testable = TestableCreator.createTestableForTyMap(100);
        runPerformanceTest(testable);
    }

    private void runPerformanceTest(Testable testable) {
        // TODO: Use something more suitable
        var numberOfEntries = (long)testable.numberOfEntries();
                var startMs = System.currentTimeMillis();

        for (int i=0; i<80000000; i++) {
            var longIndex = (long)i;
            var index = (longIndex * 31 + longIndex) % numberOfEntries;
            var key = testable.getKey((int)index);
            var value = testable.getValue(key);
            Assertions.assertEquals(key.getValue(), value.valueForKey);
        }

        var endMs = System.currentTimeMillis();
        System.out.println("Number of ms: " + (endMs-startMs));
    }

}