package ru.nsu.tokarev;

import ru.nsu.tokarev.CreditBook.GradeValue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class GradeValueTest {

    @Test
    void testGradeValueNumericValues() {
        assertEquals(5, GradeValue.EXCELLENT.getNumericValue());
        assertEquals(4, GradeValue.GOOD.getNumericValue());
        assertEquals(3, GradeValue.SATISFACTORY.getNumericValue());
        assertEquals(2, GradeValue.UNSATISFACTORY.getNumericValue());
    }

    @Test
    void testGradeValueDescriptions() {
        assertEquals("excellent", GradeValue.EXCELLENT.getDescription());
        assertEquals("good", GradeValue.GOOD.getDescription());
        assertEquals("satisfactory", GradeValue.SATISFACTORY.getDescription());
        assertEquals("unsatisfactory", GradeValue.UNSATISFACTORY.getDescription());
    }

    @Test
    void testFromNumericValidValues() {
        assertEquals(GradeValue.EXCELLENT, GradeValue.fromNumeric(5));
        assertEquals(GradeValue.GOOD, GradeValue.fromNumeric(4));
        assertEquals(GradeValue.SATISFACTORY, GradeValue.fromNumeric(3));
        assertEquals(GradeValue.UNSATISFACTORY, GradeValue.fromNumeric(2));
    }

    @Test
    void testFromNumericInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> GradeValue.fromNumeric(1));
        assertThrows(IllegalArgumentException.class, () -> GradeValue.fromNumeric(6));
        assertThrows(IllegalArgumentException.class, () -> GradeValue.fromNumeric(0));
        assertThrows(IllegalArgumentException.class, () -> GradeValue.fromNumeric(-1));
        assertThrows(IllegalArgumentException.class, () -> GradeValue.fromNumeric(10));
    }

    @Test
    void testFromNumericErrorMessage() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> GradeValue.fromNumeric(7)
        );
        assertEquals("Invalid grade value: 7", exception.getMessage());
    }

    @Test
    void testEnumValues() {
        GradeValue[] values = GradeValue.values();
        assertEquals(4, values.length);
        assertArrayEquals(new GradeValue[]{
            GradeValue.EXCELLENT,
            GradeValue.GOOD,
            GradeValue.SATISFACTORY,
            GradeValue.UNSATISFACTORY
        }, values);
    }

    @Test
    void testValueOf() {
        assertEquals(GradeValue.EXCELLENT, GradeValue.valueOf("EXCELLENT"));
        assertEquals(GradeValue.GOOD, GradeValue.valueOf("GOOD"));
        assertEquals(GradeValue.SATISFACTORY, GradeValue.valueOf("SATISFACTORY"));
        assertEquals(GradeValue.UNSATISFACTORY, GradeValue.valueOf("UNSATISFACTORY"));
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> GradeValue.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> GradeValue.valueOf("excellent"));
        assertThrows(NullPointerException.class, () -> GradeValue.valueOf(null));
    }

    @Test
    void testEnumEquality() {
        GradeValue excellent1 = GradeValue.EXCELLENT;
        GradeValue excellent2 = GradeValue.valueOf("EXCELLENT");
        GradeValue excellent3 = GradeValue.fromNumeric(5);

        assertEquals(excellent1, excellent2);
        assertEquals(excellent2, excellent3);
        assertEquals(excellent1, excellent3);

        assertNotEquals(GradeValue.EXCELLENT, GradeValue.GOOD);
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, GradeValue.EXCELLENT.ordinal());
        assertEquals(1, GradeValue.GOOD.ordinal());
        assertEquals(2, GradeValue.SATISFACTORY.ordinal());
        assertEquals(3, GradeValue.UNSATISFACTORY.ordinal());
    }

    @Test
    void testEnumName() {
        assertEquals("EXCELLENT", GradeValue.EXCELLENT.name());
        assertEquals("GOOD", GradeValue.GOOD.name());
        assertEquals("SATISFACTORY", GradeValue.SATISFACTORY.name());
        assertEquals("UNSATISFACTORY", GradeValue.UNSATISFACTORY.name());
    }

    @Test
    void testEnumToString() {
        assertEquals("EXCELLENT", GradeValue.EXCELLENT.toString());
        assertEquals("GOOD", GradeValue.GOOD.toString());
        assertEquals("SATISFACTORY", GradeValue.SATISFACTORY.toString());
        assertEquals("UNSATISFACTORY", GradeValue.UNSATISFACTORY.toString());
    }
}
