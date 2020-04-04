


function readTextFile(file) {
    var rawFile = new XMLHttpRequest();
    rawFile.open("GET", file, false);
    rawFile.onreadystatechange = function () {
        if (rawFile.readyState === 4) {
            if (rawFile.status === 200 || rawFile.status == 0) {
                var allText = rawFile.responseText;
                var h = document.getElementsByTagName("html")[0]
                h.insertAdjacentHTML("afterbegin", allText);
                var localjs = document.createElement("script");
                localjs.src = browser.runtime.getURL("kivia.js");
                document.body.appendChild(localjs);


            }
        }
    }
    rawFile.send(null);
}


browser.runtime.onMessage.addListener(request => {
    x=document.getElementsByClassName("overlay")
    if(x.length>0){
        Array.from(x).forEach(x1=>{
            x1.remove()
        })
    }
    else
        readTextFile(browser.runtime.getURL("index.html"))
    // h.insertAdjacentHTML("afterbegin",x);
    // console.log(x);

});