import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler

class B1Test {
    static void main(String[] args) {
        def cfg = new Configuration(Configuration.VERSION_2_3_28)

        cfg.directoryForTemplateLoading = "./src/test/resources/test1" as File
        cfg.logTemplateExceptions = false
        cfg.templateExceptionHandler = TemplateExceptionHandler.IGNORE_HANDLER

        def options = [
                template: "template1",
                name: "Swagger-Petstore",
                displayName: "Proxy1",
                basePath: "/swagger-petstore/v1",
                swaggerPath:
                        //("./src/test/resources/test1/petstore-swagger.yaml" as File).toURI().toString()
                        "https://raw.githubusercontent.com/swagger-api/swagger-samples/master/java/inflector-dropwizard/src/main/swagger/swagger.yaml"
        ]

        println "opt() = ${groovy.json.JsonOutput.toJson(opt())}"

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
                targetServer: [
                        name: "pxy-ts",
                        server: [
                                "dev": "dev:1234",
                                "pre-prod": "pre-prod:2345",
                                "prod": "prod:3456"
                        ],
                        path: "/abc",
                        ssl: [
                                enabled: true,
                                clientAuthEnabled: true,
                                keyStoreRef: "keystoreref",
                                keyAlias: "keyAlias"
                        ]
                ],
                securityModel: "",//comes from Enum
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
