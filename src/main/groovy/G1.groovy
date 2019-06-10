class G1 {
    def cfg

    def outputPath

    def generate(path, templateName, fileName, it, model) {
        def t = cfg.getTemplate("$path/$templateName")

        def file = new File("$outputPath/${model.name}/$path/$fileName")

        println "file = $file.absolutePath"

        file.parentFile.mkdirs()

        file.withOutputStream {
            out -> t.process(it + [model: model, util: new Util()], new OutputStreamWriter(out))
        }
    }
}
