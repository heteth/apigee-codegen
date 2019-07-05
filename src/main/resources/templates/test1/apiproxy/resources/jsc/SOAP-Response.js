var soapResponse = JSON.parse(context.getVariable("response.content"));

//TODO
var apiResponse = soapResponse

context.setVariable("response.content", JSON.stringify(apiResponse));