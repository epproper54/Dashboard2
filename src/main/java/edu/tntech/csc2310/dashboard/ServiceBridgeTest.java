package edu.tntech.csc2310.dashboard;

import edu.tntech.csc2310.dashboard.data.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.security.Provider;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServiceBridgeTest {

    @Test
    void allcourses() {
        SemesterSchedule bridge = new ServiceBridge().allcourses("202210");

        // Test that the term is correct and courses are generated.
        assertTrue(bridge.getSubjectTerm().getTerm().contentEquals("202210"), "The term is correct.");
        assertEquals(true, bridge.getSchedule().length > 0, "The term gives courses, therefore it's valid. ");
    }

    @Test
    void coursesbysubject() {
        SemesterSchedule bridge = new ServiceBridge().coursesbysubject("CSC","202210");

        // Test that subject and term correct and courses exist.
        assertTrue(bridge.getSubjectTerm().getTerm().contentEquals("202210"), "The term is correct.");
        assertTrue(bridge.getSubjectTerm().getSubject().contentEquals("CSC"), "The subject is correct.");
        assertEquals(true, bridge.getSchedule().length > 0, "The subject and term gives courses, therefore it's valid.");

    }

    @Test
    void coursesbyfaculty() {
        ArrayList<CourseInstance> bridge = new ServiceBridge().coursesbyfaculty("CSC", "202210", "Gannod", "Gerald C");

        // Test that the CourseInstance ArrayList gets generated.
        assertEquals(true, bridge.size() > 0, "This faculty gives courses, so the parameters are valid.");
    }

    @Test
    void coursebysection() {
        CourseInstance bridge = new ServiceBridge().coursebysection("CSC", "202210", "2310", "001");

        // Test if the method outputs the course.
        assertNotNull(bridge);
    }

    @Test
    void CreditHours() {
        SubjectCreditHours bridge = new ServiceBridge().CreditHours("CSC", "202210");

        // Test if credits hours are generated.
        assertNotNull(bridge);
    }

    @Test
    void creditHoursByFaculty() {
        FacultyCreditHours bridge = new ServiceBridge().creditHoursByFaculty("CSC","202210","Gannod","Gerald C");

        // Test if the credit hours are generated.
        assertNotNull(bridge);
    }

    @Test
    void schbydeptandterms() {
        ArrayList<SubjectCreditHours> bridge = new ServiceBridge().schbydeptandterms("CSC","201750","202210");

        // Test if the list of total credit hours is generated.
        assertNotNull(bridge);
    }

    @Test
    void schbydeptandtermlist() {
        ArrayList<SubjectCreditHours> bridge = new ServiceBridge().schbydeptandtermlist("CSC", "201710,201810,201910,202010");

        // Test if a list of total credit hours is generated.
        assertNotNull(bridge);
    }

    @Test
    void schbyfacultyandterms() {
        ArrayList<FacultyCreditHours> bridge = new ServiceBridge().schbyfacultyandterms("CSC", "Gannod", "Gerald C", "201710", "202210");

        // Test if a list of total credit hours per faculty is generated.
        assertNotNull(bridge);
    }

    @Test
    void schbyfacultyandtermlist() {
        ArrayList<FacultyCreditHours> bridge = new ServiceBridge().schbyfacultyandtermlist("CSC","Gannod", "Gerald C", "201710,201810,201910,202010");

        // Test if a list of total credit hours is generated.
        assertNotNull(bridge);
    }

    @Test
    void coursesbycrnlist() {
        ArrayList<CourseInstance> bridge = new ServiceBridge().coursesbycrnlist("202210","13308,12604,12951");

        // Test if a list of courses gets generated.
        assertNotNull(bridge);
    }

    @Test
    void facultybysubject() {
        ArrayList<Faculty> bridge = new ServiceBridge().facultybysubject("CSC", "202210");

        // Test if a list of faculty gets generated.
        assertNotNull(bridge);
    }

    @Test
    void getallsubject() {
        ArrayList<String> bridge = new ServiceBridge().getallsubjects("202210");

        // Test if the list of subjects generates.
        assertNotNull(bridge);
    }
}