import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler

import java.nio.file.Path

import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        //port 6000
    }

    handlers {
        post {
            def cfg = new Configuration(Configuration.VERSION_2_3_28)

            println(("." as File).absolutePath)

            cfg.directoryForTemplateLoading = "../../../src/main/resources/templates/test1" as File
            cfg.logTemplateExceptions = false
            cfg.templateExceptionHandler = TemplateExceptionHandler.IGNORE_HANDLER

            parse(Map.class).then { options ->
                println "options = $options"

                new B1().run cfg, options

                render(Path.of(options.out))
            }
        }
        get(":name") {
            render "Hello $pathTokens.name!"
        }
    }
}