package leafFinder.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerDisjointSetTest {
    IntegerDisjointSet set;

    @BeforeEach
    void setUp() {
        set = new IntegerDisjointSet(100);
        for (int i = 0; i < 100; i++) {
            set.add(i, i);
        }
    }

    @AfterEach
    void tearDown() {
        set = null;
    }

    @Test
    void size() {
        assertEquals(100, set.size());
        set = new IntegerDisjointSet(1000);
        assertEquals(1000, set.size());
    }

    @Test
    void hasParent() {
        set.union(0, 1);
        assertTrue(set.hasParent(1));
        assertFalse(set.hasParent(0));
        set.union(0, 2);
        assertFalse(set.hasParent(0));
        assertTrue(set.hasParent(2));
    }

    @Test
    void union() {
        set.union(0, 1);
        assertEquals(0, set.get(1));
    }

    @Test
    void find() {
        set.union(0, 1);
        set.union(1, 2);
        set.union(2, 3);
        assertEquals(0, set.find(3));
    }
}