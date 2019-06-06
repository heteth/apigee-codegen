import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler

import java.nio.file.Path

import static ratpack.groovy.Groovy.ratpack

ratpack {
    handlers {
        get {
            def cfg = new Configuration(Configuration.VERSION_2_3_28)

            cfg.directoryForTemplateLoading = "../../../src/test/resources/test1" as File
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

            new B1().run cfg, options

            render(Path.of(options.out))
        }
        get(":name") {
            render "Hello $pathTokens.name!"
        }
    }
}