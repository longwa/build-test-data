package grails.buildtestdata

class CircularCheckList extends LinkedHashMap {
    // TODO: look to see if we still need to force, if that adds anything
    def update(domain, force = false) {
        if (force || !this[domain.class.name]) {
            this[domain.class.name] = domain // should short circuit circular references

            Class clazz = domain.class.superclass
            while (clazz != Object) {
                if (DomainUtil.getDomainArtefact(clazz) != null) {
                    this[clazz.name] = domain
                }
                clazz = clazz.superclass
            }
        }
    }
}
