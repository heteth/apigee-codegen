var apiRequest = JSON.parse(request.content)
//TODO
apiRequest["#namespaces"] = {
    "$default": "${operation.input.message.getPart('parameters').elementName.namespaceURI}"
}
var jsonRequest = {
    "${operation.input.message.getPart('parameters').elementName.localPart}": apiRequest
}
context.setVariable("${operation.name}-json-request", JSON.stringify(jsonRequest));