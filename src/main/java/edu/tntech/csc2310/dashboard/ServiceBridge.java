package edu.tntech.csc2310.dashboard;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import edu.tntech.csc2310.dashboard.data.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

@RestController
public class ServiceBridge {

    private static final String apiKey = "F419E6B3-FE95-444F-B5EE-D90BA7C71751";
    private static final String urlString = "https://portapi.tntech.edu/express/api/unprotected/getCourseInfoByAPIKey.php?Subject=%s&Term=%s&Key=%s";

    private CourseInstance[] courses(String subject, String term) {

        String serviceString = String.format(urlString, subject.toUpperCase(), term, apiKey);
        Gson gson = new Gson();
        CourseInstance[] courses = null;

        try {
            URL url = new URL(serviceString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = gson.newJsonReader(in);
            courses = gson.fromJson(jr, CourseInstance[].class);

            for (CourseInstance c: courses){
                c.setSubjectterm(term);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @GetMapping("/allcourses")
    public SemesterSchedule allcourses(
            @RequestParam(value = "term", defaultValue = "na") String term
    ) {

        String urlString = "https://portapi.tntech.edu/express/api/unprotected/getCourseInfoByAPIKey.php?Term=%s&Key=%s";
        String serviceString = String.format(urlString, term, apiKey);
        Gson gson = new Gson();
        CourseInstance[] gm = null;
        SemesterSchedule schedule = null;

        try {
            URL url = new URL(serviceString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            JsonReader jr = gson.newJsonReader(in);
            gm = gson.fromJson(jr, CourseInstance[].class);

            for (CourseInstance c: gm){
                c.setSubjectterm(term);
            }

            SubjectTerm subjectTerm = new SubjectTerm("ALL", term);
            schedule = new SemesterSchedule(subjectTerm, gm);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    @GetMapping("/coursesbysubject")
    public SemesterSchedule coursesbysubject(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term
    ){
        CourseInstance[] courses = this.courses(subject, term);
        SubjectTerm subjectTerm = new SubjectTerm(subject, term);
        SemesterSchedule schedule = new SemesterSchedule(subjectTerm, courses);
        return schedule;
    }

    @GetMapping("/coursesbyfaculty")
    public ArrayList<CourseInstance> coursesbyfaculty(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "lastname", defaultValue = "Gannod") String lastname,
            @RequestParam(value = "firstname", defaultValue = "Gerald C") String firstname
    ) {

        CourseInstance[] courses = this.courses(subject, term);

        ArrayList<CourseInstance> list = new ArrayList<>();

        for (CourseInstance c: courses){
            Faculty f = c.getFaculty();
            if (f.getLastname() != null && f.getFirstname() != null) {
                if (lastname.toLowerCase().contentEquals(f.getLastname().toLowerCase()) && firstname.toLowerCase().contentEquals(f.getFirstname().toLowerCase()))
                    list.add(c);
            }
        }
        return list;
    }

    @GetMapping("/coursebysection")
    public CourseInstance coursebysection(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "course", defaultValue = "2310") String course,
            @RequestParam(value = "section", defaultValue = "001") String section
    ) {
        CourseInstance[] courses = this.courses(subject, term);

        CourseInstance result = null;
        for (CourseInstance c: courses){
            if (c.getCOURSE().contentEquals(course) && c.getSECTION().contentEquals(section))
                result = c;
        }
        return result;
    }

    @GetMapping("/coursesbycrnlist")
    public ArrayList<CourseInstance> coursesbycrnlist(
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "crnlist", defaultValue = "10803") String crnlist
    ) {
        SemesterSchedule schedule = this.allcourses(term);
        CourseInstance[] courses = schedule.getSchedule();

        String[] list = crnlist.split(",");
        ArrayList<CourseInstance> results = new ArrayList<>();

        for(int i = 0; i < list.length; i++) {
            for (CourseInstance c : courses) {
                if (c.getCRN().contentEquals(list[i]))
                    results.add(c);
            }
        }
        return results;
    }

    @GetMapping("/facultybysubject")
    public Collection<Faculty> facultyBySubject(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term
    ) {
        CourseInstance[] courses = this.courses(subject, term);

        Collection<Faculty> collection = new TreeSet<>(); // Uses a TreeSet to ensure uniqueness
        Faculty f;
        for (CourseInstance c: courses){
            f = c.getFaculty();
            collection.add(f);
        }
        return collection;
    }

    @GetMapping("/getallsubjects")
    public Collection<String> subjects(
            @RequestParam(value = "term", defaultValue = "202210") String term
    ) {

        String urlString = "https://portapi.tntech.edu/express/api/unprotected/getCourseInfoByAPIKey.php?Term=%s&Key=%s";
        String serviceString = String.format(urlString, term, apiKey);
        Gson gson = new Gson();
        Collection<String> collection = new TreeSet<>();

        try {
            URL url = new URL(serviceString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            JsonReader jr = gson.newJsonReader(in);
            CourseInstance[] gm = gson.fromJson(jr, CourseInstance[].class);

            for (CourseInstance c: gm){
                c.setSubjectterm(term);
                collection.add(c.getSubjectterm().getSubject());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return collection;
    }

    @GetMapping("/schbydepartment")
    public SubjectCreditHours creditHours(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term
    ) {

        CourseInstance[] gm = this.courses(subject, term);
        int scrh = 0;

        for (CourseInstance i : gm){
            scrh += i.getSTUDENTCOUNT() * i.getCREDITS();
        }
        SubjectCreditHours sch = new SubjectCreditHours(subject, term, scrh);
        return sch;
    }

    @GetMapping("/schbydeptandterms")
    public ArrayList<SubjectCreditHours> creditHourRange(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "beginterm", defaultValue = "201780") String beginTerm,
            @RequestParam(value = "endterm", defaultValue = "202180") String endTerm
    ) {

        ArrayList<SubjectCreditHours> list = new ArrayList();
        CourseInstance[] gm = null;

        int lower = Integer.parseInt(beginTerm.substring(0,4));
        int upper = Integer.parseInt(endTerm.substring(0,4));
        int term = Integer.parseInt(beginTerm.substring(4));

        for (int i = lower; i <= upper; i++) {
            gm = this.courses(subject, i + "" + term );
            int scrh = 0;
            for (CourseInstance c : gm) {
                scrh += c.getSTUDENTCOUNT() * c.getCREDITS();
            }
            SubjectCreditHours sch = new SubjectCreditHours(subject, i + "" + term, scrh);
            list.add(sch);
        }
        return list;
    }

    @GetMapping("/schbyfaculty")
    public FacultyCreditHours creditHoursByFaculty(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "lastname", defaultValue = "Gannod") String lastname,
            @RequestParam(value = "firstname", defaultValue = "Gerald C") String firstname
    ) {
        CourseInstance[] courses = this.courses(subject, term);
        int scrh = 0;
        for (CourseInstance c : courses){
            Faculty f = c.getFaculty();
            if (f.getLastname() != null && f.getFirstname() != null) {
                if (lastname.toLowerCase().contentEquals(f.getLastname().toLowerCase()) && firstname.toLowerCase().contentEquals(f.getFirstname().toLowerCase()))
                    scrh += c.getSTUDENTCOUNT() * c.getCREDITS();
            }
        }
        FacultyCreditHours sch = new FacultyCreditHours(subject, term, lastname, firstname, scrh);
        return sch;
    }

}
