import groovy.xml.XmlUtil

class Util {
    def escapeXml(s) {
        XmlUtil.escapeXml(s)
    }
}
