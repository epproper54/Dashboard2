package edu.tntech.csc2310.dashboard;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import edu.tntech.csc2310.dashboard.data.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.synth.SynthButtonUI;
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

    private static final String apiKey = "B590434D-F21A-4252-9DAD-613F9D88D622";
    private static final String urlString = "https://portapit.tntech.edu/express/api/unprotected/getCourseInfoByAPIKey.php?Key=B590434D-F21A-4252-9DAD-613F9D88D622&CSC=2310&Term=202210\n";

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

    /***
     *
     * @param term Term of the courses.
     * @return SemesterSchedule object of all the courses in the term.
     */

    @GetMapping("/allcourses")
    public SemesterSchedule allcourses(@RequestParam(value = "term", defaultValue = "202210")String term)
    {
        SubjectTerm st = new SubjectTerm("ALL", term); // all courses in the term
        CourseInstance[] courses = null;

        Gson gson = new Gson();

        try { // grabs the api key and url to put the CourseInstances into courses
            String urlFmt = String.format(urlString, term, apiKey);
            URL url = new URL(urlFmt);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = gson.newJsonReader(in);
            courses = gson.fromJson(jr, CourseInstance[].class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (CourseInstance cours : courses) { // getting Faculty and setting the subject term
            cours.getFaculty();
            cours.setSubjectterm(term);
        }

        SemesterSchedule allcourses = new SemesterSchedule(st, courses);
        return allcourses;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param term Term of the courses.
     * @return Semester Schedule object of all courses
     */

    @GetMapping("/coursesbysubject")
    public SemesterSchedule coursesbysubject(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term
    ){
        CourseInstance[] courses = this.courses(subject, term);
        SubjectTerm subjectTerm = new SubjectTerm(subject, term);
        SemesterSchedule schedule = new SemesterSchedule(subjectTerm, courses); // make the semester schedule for the courses by subject
        return schedule;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param term Term of the courses.
     * @param  lastname Last name of faculty
     * @param firstname first name of faculty
     * @return ArrayList of the courses by faculty
     */

    @GetMapping("/coursesbyfaculty")
    public ArrayList<CourseInstance> coursesbyfaculty(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "lastname", defaultValue = "Gannod") String lastname,
            @RequestParam(value = "firstname", defaultValue = "Gerald C") String firstname
    ) {

        CourseInstance[] courses = this.courses(subject, term);

        ArrayList<CourseInstance> list = new ArrayList<>();

        for (CourseInstance c: courses){ // checking the faculty to then grabs the courses from
            Faculty f = c.getFaculty();
            if (f.getLastname() != null && f.getFirstname() != null) {
                if (lastname.toLowerCase().contentEquals(f.getLastname().toLowerCase()) && firstname.toLowerCase().contentEquals(f.getFirstname().toLowerCase()))
                    list.add(c);
            }
        }
        return list;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param term Term of the courses.
     * @param course Course to check for.
     * @param section Section to check for.
     * @return CourseInstance from the course and section.
     */

    @GetMapping("/coursebysection")
    public CourseInstance coursebysection(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "course", defaultValue = "2310") String course,
            @RequestParam(value = "section", defaultValue = "001") String section
    ) {
        CourseInstance[] courses = this.courses(subject, term);

        CourseInstance result = null;
        for (CourseInstance c: courses){ // Finding the course by section
            if (c.getCOURSE().contentEquals(course) && c.getSECTION().contentEquals(section))
                result = c;
        }
        return result;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param term Term of the courses.
     * @return number of subject credit hours by department
     */

    @GetMapping("/schbydepartment")
    public SubjectCreditHours CreditHours(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term
    ) {

        CourseInstance[] gm = this.courses(subject, term);
        int scrh = 0;

        for (CourseInstance i : gm){ // iterate through every course to multiply credit hours together
            scrh += i.getSTUDENTCOUNT() * i.getCREDITS();
        }
        SubjectCreditHours sch = new SubjectCreditHours(subject, term, scrh);
        return sch;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param term Term of the courses.
     * @param lastname Last name of faculty
     * @param firstname First name of faculty
     * @return Number of credit hours per faculty
     */

    @GetMapping("/schbyfaculty")
    public FacultyCreditHours creditHoursByFaculty(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term,
            @RequestParam(value = "lastname", defaultValue = "Gannod") String lastname,
            @RequestParam(value = "firstname", defaultValue = "Gerald C") String firstname
    ) {
        CourseInstance[] courses = this.courses(subject, term);
        int scrh = 0;
        for (CourseInstance c : courses){ // iterate through all courses in the term with the specified subject
            Faculty f = c.getFaculty();
            if (f.getLastname() != null && f.getFirstname() != null) { // check if the first name and last name match before multiplying credit hours
                if (lastname.toLowerCase().contentEquals(f.getLastname().toLowerCase()) && firstname.toLowerCase().contentEquals(f.getFirstname().toLowerCase()))
                    scrh += c.getSTUDENTCOUNT() * c.getCREDITS();
            }
        }
        FacultyCreditHours sch = new FacultyCreditHours(subject, term, lastname, firstname, scrh);
        return sch;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param beginterm beginning term to pull courses from
     * @param endterm end term to pull courses from
     * @return ArrayList of tot subject credit hours by department and term
     */

    @GetMapping("/schbydeptandterms")
    public ArrayList<SubjectCreditHours> schbydeptandterms(
            @RequestParam(value = "subject", defaultValue = "CSC")String subject,
            @RequestParam(value = "beginterm", defaultValue = "201710")String beginterm,
            @RequestParam(value = "endterm", defaultValue = "202210")String endterm
            ){
        ArrayList<SubjectCreditHours> sch = new ArrayList<>();
        ArrayList<Integer> terms = new ArrayList<>();

        int begin = Integer.parseInt(beginterm);
        int end = Integer.parseInt(endterm);
        while (begin < end){
            begin += 30;
            if(begin % 100 == 40){ // checks if term is something like 201740, if so, add 10
                begin += 10;
            }
            terms.add(begin); // add to this array
        }

        for(int D = 0; D < terms.size(); D++){ // going to iterate through terms[] to pull info
            int totalsch = 0;
            CourseInstance[] ci = this.courses(subject, String.valueOf(terms.indexOf(D))); // grab string value of terms arraylist since it is of integer type

            for(CourseInstance I : ci){
                totalsch += I.getSTUDENTCOUNT() * I.getCREDITS(); // adding Subject Credit Hours
            }

            SubjectCreditHours schtoadd = new SubjectCreditHours(subject, String.valueOf(terms.indexOf(D)), totalsch);
            sch.add(schtoadd); // adding to arraylist
        }

        return sch;

    }

    /***
     *
     * @param subject This is the type of course.
     * @param termlist List of terms to pull courses from.
     * @return An ArrayList of total subject credit hours for each term.
     */

    @GetMapping("/schbydeptandtermlist")
    public ArrayList<SubjectCreditHours> schbydeptandtermlist(
            @RequestParam(value = "subject", defaultValue = "CSC")String subject,
            @RequestParam(value = "termlist", defaultValue = "201710,201810,201910,202010")String termlist){

        ArrayList<SubjectCreditHours> sch = new ArrayList<>();
        String[] tl = termlist.split(",");

        for(int i = 0; i < tl.length; i++){ // accessing each term and collecting total credits from each

            int totalsch = 0;
            CourseInstance[] ci = this.courses(subject, tl[i]);

            for(CourseInstance L : ci){
                totalsch += L.getSTUDENTCOUNT() * L.getCREDITS(); // total subject credit hours
            }

            SubjectCreditHours schtoadd = new SubjectCreditHours(subject, tl[i], totalsch);
            sch.add(schtoadd);
        }
        return sch;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param lastname Last name of faculty
     * @param firstname First name of faculty
     * @param beginterm beginning term to pull courses from
     * @param endterm end term to pull courses from
     * @return ArrayList of total faculty credit hours for each term
     */

    @GetMapping("/schbyfacultyandterms")
    public ArrayList<FacultyCreditHours> schbyfacultyandterms(
            @RequestParam(value = "subject", defaultValue = "CSC")String subject,
            @RequestParam(value = "lastname", defaultValue = "Gannod") String lastname,
            @RequestParam(value = "firstname", defaultValue = "Gerald C") String firstname,
            @RequestParam(value = "beginterm", defaultValue = "201710")String beginterm,
            @RequestParam(value = "endterm", defaultValue = "202210")String endterm){

        ArrayList<FacultyCreditHours> fch = new ArrayList<>();
        ArrayList<Integer> terms = new ArrayList<>();

        int begin = Integer.parseInt(beginterm);
        int end = Integer.parseInt(endterm);

        while (begin < end){
            terms.add(begin); // add to this array
            begin += 30;
            if(begin % 100 == 40){ // checks if term is something like 201740, if so, add 10
                begin += 10;
            }

        }

        for(int D = 0; D < terms.size(); D++){ // going to iterate through terms[] to pull info

            int totalfch = 0;
            CourseInstance[] courses = this.courses(subject, String.valueOf(terms.indexOf(D)));

            for (CourseInstance c : courses){ // this for loop gets the Faculty and checks to see if firstname and lastname are valid to then get total credit hours
                Faculty f = c.getFaculty();
                if (f.getLastname() != null && f.getFirstname() != null) {
                    if (lastname.toLowerCase().contentEquals(f.getLastname().toLowerCase()) && firstname.toLowerCase().contentEquals(f.getFirstname().toLowerCase()))
                        totalfch += c.getSTUDENTCOUNT() * c.getCREDITS();
                }
            }

            FacultyCreditHours fchtoadd = new FacultyCreditHours(subject, String.valueOf(terms.indexOf(D)), firstname, lastname, totalfch);
            fch.add(fchtoadd); // adding to arraylist
        }
        return fch;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param lastname last name of faculty
     * @param firstname first name of faculty
     * @param termlist List of terms to add credit hours from
     * @return ArrayList of subject credit hours by faculty and terms.
     */

    @GetMapping("/schbyfacultyandtermlist")
    public ArrayList<FacultyCreditHours> schbyfacultyandtermlist (
            @RequestParam(value = "subject", defaultValue = "CSC")String subject,
            @RequestParam(value = "lastname", defaultValue = "Gannod") String lastname,
            @RequestParam(value = "firstname", defaultValue = "Gerald C") String firstname,
            @RequestParam(value = "termlist", defaultValue = "201710,201810,201910,202010")String termlist) {
        ArrayList<FacultyCreditHours> fch = new ArrayList<>();
        String[] tl = termlist.split(",");

        for(int i = 0; i < tl.length; i++){ // accessing each term and collecting total credits from each

            int totalfch = 0;
            CourseInstance[] courses = this.courses(subject, tl[i]);

            for (CourseInstance c : courses){ // this for loop gets the Faculty and checks to see if firstname and lastname are valid to then get total credit hours
                Faculty f = c.getFaculty();
                if (f.getLastname() != null && f.getFirstname() != null) {
                    if (lastname.toLowerCase().contentEquals(f.getLastname().toLowerCase()) && firstname.toLowerCase().contentEquals(f.getFirstname().toLowerCase()))
                        totalfch += c.getSTUDENTCOUNT() * c.getCREDITS();
                }
            }

            FacultyCreditHours fchtoadd = new FacultyCreditHours(subject, tl[i], firstname, lastname, totalfch);
            fch.add(fchtoadd); // add to arraylist
        }
        return fch;
    }

    /***
     *
     * @param term Term of the courses.
     * @param crnlist List of CRN numbers for courses.
     * @return An ArrayList of course instances by specified crn numbers.
     */

    @GetMapping("/coursesbycrnlist")
    public ArrayList<CourseInstance> coursesbycrnlist(
            @RequestParam(value = "term", defaultValue = "NA")String term,
            @RequestParam(value = "crnlist", defaultValue = "NA")String crnlist){

        CourseInstance[] courses = this.courses("ALL", term);
        ArrayList<CourseInstance> ci = new ArrayList<>();
        String[] CRNs = crnlist.split(",");

        for (String CRN: CRNs) { // Iterating through the list of CRNs and adding the courses into the ArrayList
            for (CourseInstance c : courses){
                if (c.getCRN().contentEquals(CRN))
                ci.add(c);
            }
        }
        return ci;
    }

    /***
     *
     * @param subject This is the type of course.
     * @param term Term of the courses.
     * @return ArrayList of Faculty members by subject.
     */

    @GetMapping("/facultybysubject")
    public ArrayList <Faculty> facultybysubject(
            @RequestParam(value = "subject", defaultValue = "CSC") String subject,
            @RequestParam(value = "term", defaultValue = "202210") String term) {

        TreeSet<Faculty> names = new TreeSet<>(); // TreeSet removes the error of duplicates
        SemesterSchedule schedule = coursesbysubject(subject, term);
        CourseInstance[] course = schedule.getSchedule();

        for (int i = 0; i < schedule.getSchedule().length; i++) { // adding Faculty to names TreeSet
            names.add(course[i].getFaculty());
        }

        ArrayList<Faculty> nameslist = new ArrayList<Faculty>(names); // converting TreeSet to ArrayList to return
        return nameslist;
    }

    /***
     *
     * @param term the term to select the subjects from
     * @return return ArrayList of Strings of all the subjects
     */

    @GetMapping("/getallsubjects")
    public ArrayList <String> getallsubjects(
            @RequestParam(value = "term", defaultValue = "na") String term) {

        SemesterSchedule ss = allcourses(term);
        CourseInstance[] ci = ss.getSchedule();
        TreeSet<String> subjects = new TreeSet<>();

        for (int i=0; i < ci.length; i++) { // getting subjects from each department
            subjects.add(ci[i].getDEPARTMENT());
        }

        ArrayList<String> subjectslist = new ArrayList<String>(subjects);
        return subjectslist;

    }
}



