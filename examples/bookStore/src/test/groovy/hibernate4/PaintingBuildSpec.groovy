package hibernate4

import grails.buildtestdata.BuildDataTest
import spock.lang.Specification

class PaintingBuildSpec extends Specification implements BuildDataTest {
    @Override
    Class[] getDomainClassesToMock() {
        [Painting]
    }

    void "building a Painting builds Gallery and Painter"() {
        when: 
        Painting painting = build(Painting, [title: "The Hunters in the Snow"])

        then:
        painting != null
        painting.id != null
        painting.title == "The Hunters in the Snow"
        painting.painter.name != null
        painting.gallery.name != null
    }
}
