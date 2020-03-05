const interval = 10000 ;
const offset = 5000;
let counter = 0;
var runningApp;
var runningRead;
var runningWrite;


async function emulateRead() {
    let item_id = 'airport:test_'+Math.floor(Math.random()*counter);
    let response = await fetch('/simulator/airports/'+item_id);
    let item = await response.json();

    insert_row(counter, item);
}

async function emulateWrite() {
    let input = {
        id: 'airport:test_'+(counter%100),
        airportname: 'test',
        city: 'test'
    };
    let response = await fetch('/simulator/airports/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(input)
    });

    let data = await response.json();
    insert_row(counter, data);
}

function setSimulationStatusRunning(status) {
    let startButton = document.getElementById("start-button");
    let stopButton = document.getElementById("stop-button");
    startButton.hidden = !status;
    stopButton.hidden = status;
}

function startAppSimulation() {
    console.log("starting...");
    counter = 0;
    setSimulationStatusRunning(false);
    emulateWrite().then(()=> //TODO await/then
    runningApp = setInterval(emulateApp, interval));
}

function emulateApp() {
    counter += 1;
    let offsetRead = Math.floor(Math.random() * offset);
    let offsetWrite = Math.floor(Math.random() * offset);
    runningRead = setTimeout(emulateRead, offsetRead);
    runningWrite = setTimeout(emulateWrite, offsetWrite);
}

function insert_row(nrow, item) {
    insert_row_values(nrow, item.type, item.activeClusters, item.latency, item.value, item.status);
}

function insert_row_values(nrow, operation, clustername, latency, value,success)
{
    draw_operation(operation, clustername, success === 'SUCCESS');
    var x=document.getElementById('table').insertRow(1);
    var y = x.insertCell(0);
    var z = x.insertCell(1);
    var a = x.insertCell(2);
    var b = x.insertCell(3);
    var c = x.insertCell(4);
    var d = x.insertCell(5);
    y.innerHTML=nrow;
    z.innerHTML=operation;
    a.innerHTML=clustername;
    b.innerHTML=latency;
    c.innerHTML=JSON.stringify(value);
    d.innerHTML=success === 'SUCCESS' ? "<span class=\"alert alert-success\">Sucess </span>" :
                          "<span class=\"alert alert-danger\">Failure </span>";

    if(table.rows.length >10) {
        table.deleteRow(10);
    }

}

function draw_operation(type, clusters, success) {
    var svg=document.getElementById('hideMe');
    //TODO draw lines
    //svg.innerHTML = '';

}


function stopAppSimulation() {
    console.log("stopped");
    setSimulationStatusRunning(true);
    clearInterval(runningApp);
    clearTimeout(runningRead);
    clearTimeout(runningWrite);
}