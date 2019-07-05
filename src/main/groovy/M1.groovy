import org.yaml.snakeyaml.Yaml

import javax.wsdl.extensions.soap.SOAPOperation
import javax.wsdl.extensions.soap12.SOAP12Operation
import javax.wsdl.factory.WSDLFactory

class M1 {
    def buildModel(options, model) {
        model.kvms = []

        def apiProxy = [:]

        apiProxy.template = options.template
        apiProxy.name = options.name
        apiProxy.displayName = options.displayName
        apiProxy.manifests = []
        apiProxy.proxies = []
        apiProxy.policies = []
        apiProxy.resources = [jsc: []]

        def defaultProxy = [
                name: 'default',
                preFlow: [request: [steps: []], response: [steps: []]],
                postFlow: [request: [steps: []], response: [steps: []]],
                flows: []
        ]
        apiProxy.proxies << defaultProxy

        def defaultTarget = [
                name: options.target?.name ?: "default",
                templateName: options.target?.templateName,
                baseUrl: options.target?.baseUrl,
                preFlow: [request: [steps: []], response: [steps: []]],
                postFlow: [request: [steps: []], response: [steps: []]],
        ]

        if (options.swaggerPath) {
            def parser = new Yaml()
            def sw = parser.load new URL(options.swaggerPath).openStream()

            defaultProxy.flows = sw.paths.collectMany {
                path, operations ->
                    operations.collect {
                        verb, operation -> [path: path, verb: verb, operation: operation]
                    }
            }

            apiProxy.spec = options.swaggerPath
            apiProxy.basePaths = options.basePath
            apiProxy.resources.openapi = [[name: "association", ext: "json", swaggerPath: options.swaggerPath]]

            defaultTarget.baseUrl = options.target?.baseUrl ?: "${sw.schemes[0]}://${sw.host}/${sw.basePath}"
        } else if (options.wsdlPath) {
            def factory = WSDLFactory.newInstance()
            def reader = factory.newWSDLReader()
            def definition = reader.readWSDL options.wsdlPath

            //take(1) because there will be bindings for different soap versions/protocols
            definition.bindings.take(1).collectMany {
                qName, b -> b.bindingOperations
            }.each {
                bindingOperation ->
                    def op = bindingOperation.operation

                    def soapAction = bindingOperation.extensibilityElements.find { it instanceof SOAPOperation }?.soapActionURI ?:
                            bindingOperation.extensibilityElements.find { it instanceof SOAP12Operation }?.soapActionURI

                    def flow = [path: "/${op.name}", verb: "post", operation: op, request: [steps: []], response: [steps: []]]

                    defaultProxy.flows << flow

                    flow.request.steps << [name: "SOAP-${op.name}-JSON-Request"]
                    apiProxy.policies << [name: "SOAP-${op.name}-JSON-Request", templateName: "SOAP-JSON-Request", operation: op]
                    apiProxy.resources.jsc << [name: "SOAP-${op.name}-JSON-Request", ext: "js", templateName: "SOAP-JSON-Request", operation: op]

                    flow.request.steps << [name: "SOAP-${op.name}-JSON-To-XML"]
                    apiProxy.policies << [name: "SOAP-${op.name}-JSON-To-XML", templateName: "SOAP-JSON-To-XML", operation: op]

                    flow.request.steps << [name: "SOAP-${op.name}-Request"]
                    apiProxy.policies << [name: "SOAP-${op.name}-Request", templateName: "SOAP-Request", operation: op, soapAction: soapAction, verb: "post"]

                    flow.response.steps << [name: "SOAP-${op.name}-Response"]
                    apiProxy.policies << [name: "SOAP-${op.name}-Response", templateName: "SOAP-Response", operation: op]
                    apiProxy.resources.jsc << [name: "SOAP-${op.name}-Response", ext: "js", templateName: "SOAP-Response", operation: op]
            }

            defaultTarget.preFlow.request.steps << [name: "Disable-Path-Copy"]
            apiProxy.policies << [name: "Disable-Path-Copy"]

            defaultTarget.postFlow.response.steps << [name: "XML-To-JSON"]
            apiProxy.policies << [name: "XML-To-JSON"]
        }

        defaultProxy.basePath = options.basePath
        defaultProxy.virtualHosts = options.virtualHosts

        apiProxy.targets = [defaultTarget]

        defaultProxy.routes = [[name: defaultTarget.name, target: defaultTarget.name]]

        if (options.extensions?."spike-arrest"?.enabled) {
            defaultProxy.preFlow.request.steps << [name: "KVM-Validation-Parameters"]
            apiProxy.policies << [name: "KVM-Validation-Parameters"]
            model.kvms << [name: "${options.name}-config"]
            defaultProxy.preFlow.request.steps << [name: "Spike-Arrest"]
            apiProxy.policies << [name: "Spike-Arrest"]
        }

        if (options.securityModel ==~ /(?i)pki/) {
            defaultProxy.preFlow.request.steps << [name: "Verify-SPID"]
            apiProxy.policies << [name: "Verify-SPID"]
        } else if (options.securityModel ==~ /(?i)api(-?)key/) {
            defaultProxy.preFlow.request.steps << [name: "Verify-API-Key"]
            apiProxy.policies << [name: "Verify-API-Key"]
        }

        if (options.extensions?."check-quota"?.enabled) {
            defaultProxy.preFlow.request.steps << [name: "Check-Quota"]
            apiProxy.policies << [name: "Check-Quota"]
        }

        if (options.extensions?."x-request-id-header"?.enabled) {
            defaultProxy.preFlow.request.steps << [name: "Save-Request-ID"]
            apiProxy.policies << [name: "Save-Request-ID"]
            apiProxy.resources.jsc << [name: "Save-Request-ID", ext: "js"]
        }

        defaultProxy.preFlow.request.steps << [name: "Save-Request-Verb"]
        apiProxy.policies << [name: "Save-Request-Verb"]
        defaultProxy.preFlow.request.steps << [name: "Save-Request-Host"]
        apiProxy.policies << [name: "Save-Request-Host"]

        if (options.extensions?."x-request-id-header"?.enabled) {
            defaultProxy.postFlow.response.steps << [name: "Set-Request-ID"]
            apiProxy.policies << [name: "Set-Request-ID"]
            apiProxy.resources.jsc << [name: "Set-Request-ID", ext: "js"]
        }

        if (options.extensions?."statistics-collector"?.enabled) {
            defaultProxy.postFlow.response.steps << [name: "Statistics-Collector"]
            def statistics = []
            if (options.extensions?."x-request-id-header"?.enabled)
                statistics << [type: "String", name: "request-id", ref: "request-id"]
            if (options.securityModel ==~ /(?i)pki/)
                statistics << [type: "String", name: "pid", ref: "spid"]
            apiProxy.policies << [name: "Statistics-Collector", statistics: statistics]
        }

        apiProxy.policies << [name: "RaiseFault.UnknownResource"]

        model.apiProxy = apiProxy

        model.sharedFlows = []

        def product = [
                name: options.name,
                displayName: options.name,
                apiResources: [],
                approvalType: options.approvalType,
                attributes: [["name": "access", "value": "public"]],
                environments: [options.environment],
                proxies: [options.name],
                quota: options.extensions?."quota-limit"?.quota,
                quotaInterval: options.extensions?."quota-limit"?.quotaInterval,
                quotaTimeUnit: options.extensions?."quota-limit"?.quotaTimeUnit,
        ]

        model.products = [product]

        model.targetServers  = options.target?.servers.collect { name, value ->
            value + [sSLInfo: value.sSLInfo ?: value.sslInfo, sslInfo: null]
        }
    }
}
