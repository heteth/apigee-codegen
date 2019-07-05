if (context.getVariable("request-id")) {
    context.setVariable("message.header.x-request-id", context.getVariable("request-id"));
}