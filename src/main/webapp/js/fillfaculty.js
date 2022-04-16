function fillfaculty() {

    var subjectTextField = document.getElementById("subjectfield");
    var termTextField = document.getElementById("termfield");

    var serviceURL = "http://localhost:8080/facultybysubject?subject=" +
        (subjectTextField.value).toUpperCase() +
        "&term=" + termTextField.value;

    let request = new XMLHttpRequest();
    var list = document.getElementById("codemenu");

    request.open("GET", serviceURL);
    request.send();
    request.onload = () => {
        if (request.status == 200) {
            jsondata = JSON.parse(request.response);

            jsondata.forEach(function (item) {
               let opt = document.createElement("option");
               if (item.lastname != null) {
                  opt.innerText = item.lastname + " " + item.firstname;
                  opt.setAttribute("firstname", item.firstname);
                  opt.setAttribute("lastname", item.lastname);
                  list.appendChild(opt);
               }
            });
            changecard();
        }
    }
}

function displayfaculty(){
    var list = document.getElementById("codemenu");
    list.innerHTML = '';
    fillfaculty();
}

function changecard(){
    var cardname = document.getElementById("cardname");

    var inputDropdown = document.getElementById("codemenu");
    var selectedValue = inputDropdown.options[inputDropdown.selectedIndex];
    var lastTextField = selectedValue.getAttribute("lastname");
    var firstTextField = selectedValue.getAttribute("firstname");

    cardname.innerText = firstTextField + " " + lastTextField;
}

