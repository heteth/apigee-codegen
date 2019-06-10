import org.yaml.snakeyaml.Yaml

class M1 {
    def buildModel(options, model) {
        def apiProxy = [:]

        apiProxy.template = options.template
        apiProxy.name = options.name
        apiProxy.displayName = options.displayName
        apiProxy.manifests = []
        apiProxy.proxies = []
        apiProxy.policies = []
        apiProxy.resources = [:]
        apiProxy.targets = []

        def parser = new Yaml()
        def sw = parser.load new URL(options.swaggerPath).openStream()

        def defaultProxy = [name: 'default']
        apiProxy.proxies << defaultProxy

        defaultProxy.flows = sw.paths.collectMany {
            path, operations -> operations.collect {
                verb, operation -> [path: path, verb: verb, operation: operation]
            }
        }

        apiProxy.spec = options.swaggerPath
        apiProxy.basePaths = options.basePath
        apiProxy.resources.openapi = [[name: "association", ext: "json", swaggerPath: options.swaggerPath]]

        defaultProxy.basePath = options.basePath
        defaultProxy.virtualHosts = options.virtualHosts

        model.apiProxy = apiProxy
        model.products = []
        model.targetServers = []
        model.kvms = []
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

        model.products << product

        model.targetServers  = options.target.servers.collect { name, value ->
            value + [name: name, sSLInfo: value.sSLInfo ?: value.sslInfo, sslInfo: null]
        }
    }
}
