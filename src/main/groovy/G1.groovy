class G1 {
    def cfg

    def outputPath

    def generate(path, templateName, fileName, it, model) {
        def t = cfg.getTemplate("$path/$templateName")

        it.model = model
        it.util = new Util()

        def file = new File("$outputPath/${model.name}/$path/$fileName")

        println "file = $file.absolutePath"

        file.parentFile.mkdirs()

        file.withOutputStream {
            out -> t.process(it, new OutputStreamWriter(out))
        }
    }
}
