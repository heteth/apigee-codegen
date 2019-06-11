class T1 {

    G1 g

    T1(G1 g) {
        this.g = g
    }

    def generateProxy(Map model) {
        generateApiProxy(model)
        generateManifests(model.manifests, model)
        generateProxies(model.proxies, model)
        generatePolicies(model.policies, model)
        generateResources(model.resources, model)
        generateTargets(model.targets, model)
    }

    def generateManifests(manifests, model) {
        manifests.each {
            generateManifest("apiproxy/manifests", it, model)
        }
    }

    def generateManifest(path, manifest, model) {
        generate(path, "${manifest.name ?: 'manifest'}.xml", "${manifest.name ?: 'manifest'}.xml", manifest, model)
    }

    def generate(path, templateName, fileName, it, model) {
        g.generate(path, templateName, fileName, it, model)
    }

    def generateProxies(proxies, model) {
        proxies.each {
            generateProxy("apiproxy/proxies", it, model)
        }
    }

    def generateProxy(path, proxy, model) {
        generate(path, "${proxy.name}.xml", "${proxy.name}.xml", proxy, model)
    }

    def generatePolicies(policies, model) {
        policies.each {
            generatePolicy("apiproxy/policies", it, model)
        }
    }

    def generatePolicy(path, policy, model) {
        generate(path, "${policy.templateName ?: policy.name}.xml", "${policy.name}.xml", policy, model)
    }

    def generateResources(resources, model) {
        resources.each {
            group, list -> list.each {
                generateResource("apiproxy/resources/$group", it, model)
            }
        }
    }

    def generateResource(path, resource, model) {
        generate(path, "${resource.templateName ?: resource.name}.$resource.ext", "${resource.name}.$resource.ext", resource, model)
    }

    def generateTargets(targets, model) {
        targets.each {
            generateTarget("apiproxy/targets", it, model)
        }
    }

    def generateTarget(path, target, model) {
        generate(path, "${target.templateName ?: target.name}.xml", "${target.name}.xml", target, model)
    }

    def generateApiProxy(model) {
        generateApiProxyXml("apiproxy", model)
    }

    def generateApiProxyXml(path, model) {
        generate(path, "${model.template}.xml", "${model.name}.xml", model, model)
    }}
