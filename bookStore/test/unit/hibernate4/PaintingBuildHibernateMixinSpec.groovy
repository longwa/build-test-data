package hibernate4

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import spock.lang.Ignore
import spock.lang.Specification

@Build([Painting, Gallery, Painter])
@Domain([Painting, Gallery, Painter])
@TestMixin(HibernateTestMixin)
class PaintingBuildHibernateMixinSpec extends Specification {

    void "manually making a painting works"() {
        given:
        Painter painter = new Painter(name: "Pieter Bruegel").save(failOnError: true)
        Gallery gallery = new Gallery(name: "Kunsthistorisches Museum").save(failOnError: true)

        when:
        Painting painting = new Painting(title: "The Hunters in the Snow", painter: painter, gallery: gallery)
        painter.addToPaintings(painting)
        gallery.addToPaintings(painting)

        then:
        assert painting.save()
        assert painting.id != null
        assert painting.painter.name == painter.name
        assert painting.gallery.name == gallery.name
        assert painter.paintings.contains(painting)
        assert gallery.paintings.contains(painting)
    }

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
