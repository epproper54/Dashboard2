google.charts.load('current', {'packages':['table']});

function drawCoursesBySubject() {

    var subjectTextField = document.getElementById("subjectfield");
    var termTextField = document.getElementById("termfield");

    var term = findGetParameter("term");
    var subject = findGetParameter("subject");
    var serviceURL = "http://localhost:8080/coursesbysubject?subject=" +
        (subjectTextField.value).toUpperCase() +
        "&term=" + termTextField.value;

    let request = new XMLHttpRequest();

    request.open("GET", serviceURL);
    request.send();
    request.onload = () => {
        if (request.status == 200) {
            jsondata = JSON.parse(request.response).schedule;
            var data = drawScheduleTable(jsondata);
            var table = new google.visualization.Table(document.getElementById('table_div'));
            table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});
        }
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