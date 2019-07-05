import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import groovy.json.JsonOutput

class B1Test {
    static void main(String[] args) {
        def cfg = new Configuration(Configuration.VERSION_2_3_28)

        cfg.directoryForTemplateLoading = "./src/main/resources/templates/test1" as File
        cfg.logTemplateExceptions = false
        cfg.templateExceptionHandler = TemplateExceptionHandler.IGNORE_HANDLER

        def name = "Swagger-Petstore-33"
        def version = "v1"

        def options = [
                org: "hamedhamedhamedhamed-eval",
                environment: "prod",
                virtualHosts: ["default", "secure"],
                template: "template1",
                name: "${name.toLowerCase()}-$version",
                displayName: "Proxy1",
                basePath: "/${name.toLowerCase()}/$version",
                /*swaggerPath:
                        //("./src/test/resources/test1/petstore-swagger.yaml" as File).toURI().toString()
                        "https://raw.githubusercontent.com/swagger-api/swagger-samples/master/java/inflector-dropwizard/src/main/swagger/swagger.yaml",*/
                wsdlPath: //"https://svn.apache.org/repos/asf/airavata/sandbox/xbaya-web/test/Calculator.wsdl",
                    "http://www.dneonline.com/calculator.asmx?WSDL",
                approvalType: "auto",//mandatory
                //target: [baseUrl: "https://petstore.swagger.io/v2"],
                target: [baseUrl: "http://www.dneonline.com/calculator.asmx"],
                //target: [
                        /*servers: [
                                [name: "$name", "host": "dev", "port": 8080],
                                [name: "$name-preprod", "host": "dev", "port": 443, sslInfo: [enabled: true]],
                                [name: "$name-prod", "host": "prod-server", "port": 443, sSLInfo: [enabled: true]],
                        ],*/
                //],
                headers: ["Authorization": "Basic ${"hamedhamedhamedhamed@yahoo.com:Goodday!23".bytes.encodeBase64()}"],
                extensions: [
                        "x-request-id-header": [enabled: true],
                        "spike-arrest": [enabled: false],
                        "check-quota": [enabled: false],
                        "statistics-collector": [enabled: true],
                ],
                //"securityModel": //"pki"//
                //     "api-key",
        ]

        println "opt() = ${JsonOutput.toJson(opt())}"

        new B1().run cfg, options
    }

    def static opt() {
        [
                org: "org",
                environment: "dev",
                virtualHosts: ["default", "secure"],
                template: "template-name",
                name: "pxy",//used for proxy and product
                displayName: "Display Name",
                swaggerPath: "http://a.com/b.yaml",
                target: [
                        servers: [
                                [name: "petstore", "host": "dev", "port": 443, sslInfo: [enabled: true]],
                                [name: "petstore-preprod", "host": "dev", "port": 443, sslInfo: [enabled: true]],
                                [name: "petstore-prod", "host": "dev", "port": 443, sslInfo: [enabled: true]],
                        ],
                        path: "/abc",
                        sslInfo: [
                                enabled: true,
                                clientAuthEnabled: true,
                                keyStoreRef: "keystoreref",
                                keyAlias: "keyAlias"
                        ]
                ],
                securityModel: "",//comes from Enum : api-key, pki
                approvalType: "",//comes from enum: manual, auto
                extensions: [
                        "schema-validation": [enabled: true, otherAttribute: "value"],
                        "statistics-collector": [:],
                        "error-handling": [:],
                        "spike-arrest": [:],
                        "quota-limit": [:],
                        "x-request-id-header": [:]
                ],

        ]
    }

    /*
    Choose Org
    Template
        REST passthrough
        SOAP passthrough
        REST <- REST transformation
        REST <- SOAP transformation
        SOAP <- SOAP transformation
    Upload API Spec
    Security Model
        API Key
        SPID/Cert based
        OAUTH2 passthrough
        OAUTH2
    Approval type (manual/automatic)
    Target URLs
        Test
        Production
    Platform Capabilities
        Schema validation
        Statistics collector
        Error handling
        Logging
        Spike Arrest
        Quota
        ...
     --------------
     Name
     Display Name
     Environment
     Virtual Hosts
     -------------
     */
}
