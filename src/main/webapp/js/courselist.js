google.charts.load('current', {'packages':['table']});
google.charts.setOnLoadCallback(drawTable);

function drawTable() {

    var term = findGetParameter("term");
    var subject = findGetParameter("subject");
    var serviceURL = "http://localhost:8080/coursesbysubject?subject=" + subject + "&term=" + term;
    let request = new XMLHttpRequest();
    var jsondata;

    request.open("GET", serviceURL);
    request.send();
    request.onload = () => {
        if (request.status == 200){
            jsondata = JSON.parse(request.response);

            var data = new google.visualization.DataTable();
            data.addColumn('string', 'CRN');
            data.addColumn('string', 'Subject');
            data.addColumn('string', 'Course');
            data.addColumn('string', 'Section');
            data.addColumn('number', 'Credits');
            data.addColumn('string', 'Time');
            data.addColumn('string', 'Where');
            data.addColumn('string', 'Instructor');
            data.addColumn('string', 'Enrollment');
            data.addColumn('string', 'Title');

            if (jsondata.schedule.length > 0){
                for (let i = 0; i < jsondata.schedule.length; i++){
                    var rowdata = jsondata.schedule[i];

                    var faculty = rowdata.faculty.firstname + " " + rowdata.faculty.lastname;
                    if (rowdata.faculty.firstname == null)
                        faculty = "STAFF";
                    var building = rowdata.building + rowdata.room;
                    if (building == 0)
                        building = "N/A"
                    var classTime = rowdata.classdays + " " + rowdata.starttime + rowdata.startam_PM + " - " + rowdata.endtime + rowdata.endam_PM;
                    if (rowdata.classdays == null){
                      classTime = 'N/A'
                    }
                    var enrollment = rowdata.studentcount + "/" + rowdata.maximumstudents;
                    row = [ rowdata.crn, rowdata.subjectterm.subject, rowdata.course, rowdata.section, rowdata.credits,
                        classTime, building, faculty.toString(), enrollment, rowdata.title];

                    console.log(row);
                    data.addRow(row);
                }
            }
        }
        var table = new google.visualization.Table(document.getElementById('table_div'));
        table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});

    }
}

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    location.search
        .substr(1)
        .split("&")
        .forEach(function (item) {
            tmp = item.split("=");
            if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
        });
    return result;
}