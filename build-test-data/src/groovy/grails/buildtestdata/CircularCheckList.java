package grails.buildtestdata;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CircularCheckList extends LinkedHashMap {
    private static final long serialVersionUID = 1L;

    public CircularCheckList() {
        super();
    }

    public CircularCheckList(Map m) {
        super(m);
    }

    public void update(Object domain) {
        update(domain, false);
    }

    // TODO: look to see if we still need to force, if that adds anything
    public void update(Object domain, boolean force) {
        if (force || !containsKey(domain.getClass().getName())) {
            put(domain.getClass().getName(), domain); // should short circuit circular references

            Class clazz = domain.getClass().getSuperclass();
            while (clazz != Object.class) {
                if (DomainUtil.getDomainArtefact(clazz) != null) {
                    put(clazz.getName(), domain);
                }
                clazz = clazz.getSuperclass();
            }
        }
    }
}
