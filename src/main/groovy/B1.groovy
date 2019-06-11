import groovy.json.JsonGenerator
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.entity.mime.MultipartEntityBuilder

import java.nio.file.Files

class B1 {
    def run(cfg, options) {
        def out = Files.createTempDirectory(null)

        def t = new T1(new G1(cfg: cfg, outputPath: out))

        def model = [:]
        new M1().buildModel(options, model)

        println "model = $model"

        new AntBuilder().with {
            delete(includeEmptyDirs: true, quiet: true) {
                fileset dir: "$out/${model.apiProxy.name}"
            }

            t.generateProxy model.apiProxy

            zip destfile: "$out/${model.apiProxy.name}.zip", {
                fileset dir: "$out/${model.apiProxy.name}"
            }
        }

        /*call apis for
     Upload Proxy
     Create Product <- based on approval type
     Deploy Proxy <- based on env
     Create Target Servers (overwrite/skip) <- based on target server
     Create KVMs (overwrite/skip) <- based on spike arrest
     Copy Shared Flows (sync) <- based on security model, schema validation,
        error-handling?, spike-arrest?, quota-limit?, x-request-id-header?, statistics-collector
        */
        options.out = "$out/${model.apiProxy.name}.zip"
/*
        createProxy()
        --createProduct()
        --createTargetServers()
        not needed createKVMs()
        copySharedFlows()
        */

        def g = new JsonGenerator.Options().excludeNulls().build()

        def headers = options.headers

        //todo get from options
        new HTTPBuilder("https://api.enterprise.apigee.com/v1/o/${options.org}/apis?action=import&name=${model.apiProxy.name}").with {
            setHeaders headers
            def builder = MultipartEntityBuilder.create()
            builder.addBinaryBody"file", new File(options.out)
            def response = request(Method.POST) {
                request.entity = builder.build()
            }

            new HTTPBuilder("https://api.enterprise.apigee.com/v1/o/${options.org}/environments/${options.environment}/apis/${response.name}/revisions/${response.revision}/deployments").with {
                setHeaders headers
                request(Method.POST) {}
            }
        }

        model.products.each { product ->
            println "product = ${g.toJson(product)}"
            new HTTPBuilder("https://api.enterprise.apigee.com/v1/o/${options.org}/apiproducts").with {
                setHeaders headers
                request(Method.POST, ContentType.JSON) {
                    body = g.toJson(product)
                }
            }
        }

        model.targetServers.each { targetServer ->
            println "targetServer = ${g.toJson(targetServer)}"
            new HTTPBuilder("https://api.enterprise.apigee.com/v1/o/${options.org}/environments/${options.environment}/targetservers").with {
                setHeaders headers
                request(Method.POST, ContentType.JSON) {
                    body = g.toJson(targetServer)
                }
            }
        }
    }
}
