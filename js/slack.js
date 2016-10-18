var QueryString = function () {
  // This function is anonymous, is executed immediately and
  // the return value is assigned to QueryString!
  var query_string = {};
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
        // If first entry with this name
    if (typeof query_string[pair[0]] === "undefined") {
      query_string[pair[0]] = decodeURIComponent(pair[1]);
        // If second entry with this name
    } else if (typeof query_string[pair[0]] === "string") {
      var arr = [ query_string[pair[0]],decodeURIComponent(pair[1]) ];
      query_string[pair[0]] = arr;
        // If third or later entry with this name
    } else {
      query_string[pair[0]].push(decodeURIComponent(pair[1]));
    }
  }
  return query_string;
}();

function createDownloadElement(filename, text){
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';

    return element;
}

function download(filename, text) {
  var element = createDownloadElement(filename, text);
  document.body.appendChild(element);
  element.click();
  document.body.removeChild(element);
}

function main(){
  fetch("https://slack.com/api/oauth.access?client_id=69648668736.86028092004&client_secret=df8416a4bc8db27e62c46706ee61e3a9&code="+QueryString.code)
    .then(res => res.json())
    .then(j => onSuccess(j))
}

function onSuccess(result){
  var token = result.access_token;
  if (token === undefined) {
    console.log(result);
    onError(result);
    return;
  }

  var downloadElement = createDownloadElement('token', token);
  var outputElement = document.getElementById("output");
  outputElement.appendChild(downloadElement);
  outputElement.style.visibility = 'visible';
  document.getElementById('token').innerHTML = token;
}

function onError(result){
  var errorElement = document.getElementById("error")
  errorElement.style.visibility = 'visible';
  document.getElementById("errorMessage").innerHTML = result.error;

}

main();
