package triangle

class Manager {
    static hasMany = [workers:Worker]
    static belongsTo = [director:Director]
}
