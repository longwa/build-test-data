package hibernate4

import spock.lang.Specification


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
