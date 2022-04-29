
## Project Description

Deans, Associate Deans, Department chairs and others across the university are constantly in need of the data necessary to help drive decision making. In order to use that data, effective visualization is needed to facilitate interpretation of the vast amount of information available to these administrators. In this project we created a number of webservices suitable for supporting the creation of dashboards that can be used to access valuable institutional data. 

## src/main
Go to java 

 - resources: This folder has all the html and javascript files that request the data and display data onto the website.

Go to java -> edu.tntech.csc2310.dashboard

 - data: Holds classes used for the methods within the Service Bridge.
 - DashboardApplication: This class is what runs the configuration for running the Service Bridge in IntelliJ.
 - ServiceBridge: This class holds all of the methods needed for the web service.
 - ServiceBridgeTest: These are the JUnit tests for all the methods used in the web service.

Go to java -> edu.tntech.csc2310.dashboard -> data

 - CourseInstance: This is the class that data is formatted into, with every attribute of courses coming from the API.
 - Faculty: This is the class that will house the professors associated with the CourseInstance.
 - FacultyCreditHours: This is the class used for dealing with the total credit hours associated with a professor.
 - SubjectTerm: This class holds a subject and a term.
 - SemesterSchedule: This class is holds an array of course instances and a subject term associated with it.
 - SubjectCreditHours: This class holds the subject, term, and the total credit hours of the course.

## Compilation Instructions

When you want to compile using Maven in IntelliJ, click "Maven" on the right taskbar of the window and then click Dashboard -> Lifecycle. Double click clean, then compile. It's that simple. 

## How to Clone 
Login to GitLab and search for "@jgannod". Go to Contributed projects -> [csc2310-sp22-students / yourusername / yourusername-dashboard_2](https://gitlab.csc.tntech.edu/csc2310-sp22-students/enparker42/enparker42-dashboard_2).
Click on Clone, then copy the link under "Clone with HTTPS". Next, go to IntelliJ and exit out of any project you are currently working in. Click "Get from VCS" and insert the clone link into the "URL" box. Log into GitLab here if you need to. The project should then load up in a new window. 

## Setting Up the Project in IntelliJ
When you're in the project, click on a gear that's in the top right area of the window. Click Project Structure, then make sure that something that looks like "11 Eclipse Temurin version 11.0.13" is selected for the "SDK".

You will need to make a new configuration. Click on the configurations box next to the run button. Click Edit Configurations then click the plus and select Application. Name this configuration something relevant and go to the long box under the SDK box and click the paper-like icon and select the Dashboard Application in the new window here. Go ahead and click Apply then OK. You should be ready to go now.
