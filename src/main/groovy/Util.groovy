import groovy.xml.XmlUtil

class Util {
    def escapeXml(s) {
        XmlUtil.escapeXml(s)
    }

    def toUpperCase(s) {
        "$s".toUpperCase()
    }
}
