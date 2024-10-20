async function ajaxCall(url, method, data, _hearders) {

    console.log("url",url)
    // const _host = "http://127.0.0.1:8000"
    const _host = ""

    url = _host + url

    const response = await fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json",
            ..._hearders
        },
        body: method == "POST" ? JSON.stringify(data) : null
    });

    return response;
}