package hibernate4

class Painting {
    String title
    static belongsTo = [painter: Painter, gallery: Gallery]
}
