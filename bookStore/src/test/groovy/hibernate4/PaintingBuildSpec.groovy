package hibernate4

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification

@Build([Painting])
class PaintingBuildSpec extends Specification {

    void "building a Painting builds Gallery and Painter"() {
        when: 
        Painting painting = Painting.build(title: "The Hunters in the Snow")

        then:
        assert painting != null
        assert painting.id != null
        assert painting.title == "The Hunters in the Snow"
        assert painting.painter.name != null
        assert painting.gallery.name != null
    }
}
