package standalone
class Standalone {
    // a standalone domain object that doesn't rely on other domain objects, no object graph to create
	String name
	Integer age
	Date created
    String emailAddress

    static belongsTo = [parent: Standalone] // shouldn't matter as it's nullable, so it shouldn't get created
	
	static constraints = {
		name(nullable: false)
        parent(nullable: true)
        emailAddress blank: false, unique: true, email: true
	}
	
	String toString() { "$name" }
}
