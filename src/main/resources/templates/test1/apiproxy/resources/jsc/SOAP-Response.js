var soapResponse = JSON.parse(context.getVariable("response.content"));

//TODO
var apiResponse = {}

context.setVariable("response.content", JSON.stringify(apiResponse));