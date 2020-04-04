browser.commands.onCommand.addListener((command) => {
  browser.tabs.query({
    currentWindow: true,
    active: true
  }).then(doThings);
});


doThings = (tabs) => {
  tab = tabs.pop()
  readTextFile(browser.runtime.getURL("style1.css"));
  browser.tabs.sendMessage(
    tab.id,
    { command: "f9" }
  ).then(response => {

  })
}
function readTextFile(file) {
  var rawFile = new XMLHttpRequest();
  rawFile.open("GET", file, false);
  rawFile.onreadystatechange = function () {
    if (rawFile.readyState === 4) {
      if (rawFile.status === 200 || rawFile.status == 0) {
        var allText = rawFile.responseText;
        var insertingCSS = browser.tabs.insertCSS({ code: allText });
        insertingCSS.then(null, null);


      }
    }
  }
  rawFile.send(null);
}

