package ru.nsu.tokarev;

import ru.nsu.tokarev.CreditBook.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;


public class CreditBookTest {
    private CreditBook creditBook;

    @BeforeEach
    void setUp() {
        creditBook = new CreditBook("Test Testovich", "12345", StudyType.PAID);
    }

    @Test
    void testCalculateGPA() {
        creditBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        
        double expectedGPA = (5.0 + 4.0) / 2.0;
        assertEquals(expectedGPA, creditBook.calculateGPA(), 0.01);
    }

    @Test
    void testCalculateGPAWithRepeatedSubject() {
        creditBook.addGrade(new Grade("Mathematics", GradeValue.SATISFACTORY,
                AssessmentType.EXAM, LocalDate.of(2023, 1, 1), 1));
        creditBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.of(2023, 6, 1), 2));
        
        assertEquals(5.0, creditBook.calculateGPA(), 0.01);
    }

    @Test
    void testCanTransferToBudget() {
        // Student is already on a budget
        CreditBook budgetBook = new CreditBook("Student", "123", StudyType.BUDGET);
        assertFalse(budgetBook.canTransferToBudget());
        
        // Adding a satisfactory grade in the latest semesters
        creditBook.addGrade(new Grade("Mathematics", GradeValue.SATISFACTORY,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertFalse(creditBook.canTransferToBudget());
        
        // Adding only good and excellent grades in exams
        CreditBook goodBook = new CreditBook("Good Student", "456", StudyType.PAID);
        goodBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        goodBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 2));
        assertTrue(goodBook.canTransferToBudget());
    }

    @Test
    void testCanTransferToBudgetWithDifferentiatedCredit() {
        creditBook.addGrade(new Grade("Physical Education", GradeValue.SATISFACTORY,
                AssessmentType.DIFFERENTIATED_CREDIT, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 2));
        assertTrue(creditBook.canTransferToBudget());
    }

    @Test
    void testCanGetRedDiploma() {
        // Not enough excellent grades (less than 75%)
        creditBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("Chemistry", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("History", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertFalse(creditBook.canGetRedDiploma());
        
        // 75% excellent grades, but there is a satisfactory one
        CreditBook bookWithSatisfactory = new CreditBook("Student", "123", StudyType.BUDGET);
        bookWithSatisfactory.addGrade(new Grade("Subject1", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        bookWithSatisfactory.addGrade(new Grade("Subject2", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        bookWithSatisfactory.addGrade(new Grade("Subject3", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        bookWithSatisfactory.addGrade(new Grade("Subject4", GradeValue.SATISFACTORY,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertFalse(bookWithSatisfactory.canGetRedDiploma());
        
        // All requirements are done
        CreditBook excellentBook = new CreditBook("Excellent Student", "789", StudyType.BUDGET);
        excellentBook.addGrade(new Grade("Subject1", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        excellentBook.addGrade(new Grade("Subject2", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        excellentBook.addGrade(new Grade("Subject3", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        excellentBook.addGrade(new Grade("Subject4", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        excellentBook.setQualificationWorkGrade(GradeValue.EXCELLENT);
        assertTrue(excellentBook.canGetRedDiploma());
    }

    @Test
    void testCanGetIncreasedScholarship() {
        // No grades
        assertFalse(creditBook.canGetIncreasedScholarship());
        
        // There is a satisfactory grade in the last semester
        creditBook.addGrade(new Grade("Mathematics", GradeValue.SATISFACTORY,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertFalse(creditBook.canGetIncreasedScholarship());
        
        // Only good grades (no excellent ones)
        CreditBook goodOnlyBook = new CreditBook("Student", "123", StudyType.BUDGET);
        goodOnlyBook.addGrade(new Grade("Mathematics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        goodOnlyBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertFalse(goodOnlyBook.canGetIncreasedScholarship());
        
        // There is at least one excellent grade and the rest are good
        CreditBook scholarshipBook = new CreditBook("Good Student", "456", StudyType.BUDGET);
        scholarshipBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        scholarshipBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertTrue(scholarshipBook.canGetIncreasedScholarship());
    }

    @Test
    void testTransferToBudget() {
        // Cannot transfer
        creditBook.addGrade(new Grade("Mathematics", GradeValue.SATISFACTORY,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertFalse(creditBook.transferToBudget());
        assertEquals(StudyType.PAID, creditBook.getStudyType());
        
        // Can transfer
        CreditBook transferableBook = new CreditBook("Student", "123", StudyType.PAID);
        transferableBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        assertTrue(transferableBook.transferToBudget());
        assertEquals(StudyType.BUDGET, transferableBook.getStudyType());
    }

    @Test
    void testGetGPAForSemester() {
        creditBook.addGrade(new Grade("Mathematics", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("Physics", GradeValue.GOOD,
                AssessmentType.EXAM, LocalDate.now(), 1));
        creditBook.addGrade(new Grade("Chemistry", GradeValue.EXCELLENT,
                AssessmentType.EXAM, LocalDate.now(), 2));
        
        assertEquals(4.5, creditBook.getGPAForSemester(1), 0.01);
        assertEquals(5.0, creditBook.getGPAForSemester(2), 0.01);
        assertEquals(0.0, creditBook.getGPAForSemester(3), 0.01);
    }
}
