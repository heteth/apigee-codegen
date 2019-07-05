context.setVariable("request-id", context.getVariable("request.header.x-request-id"))
context.setVariable("uuid", context.getVariable("request-id") || Date.now().toString());
context.setVariable("requestor", context.getVariable("request.header.x-requestor"));