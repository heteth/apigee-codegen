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
        options.out = "$out/${model.name}.zip"
/*
        createProxy()
        createProduct()
        createTargetServers()
        createKVMs()
        copySharedFlows()
 */
    }
}
