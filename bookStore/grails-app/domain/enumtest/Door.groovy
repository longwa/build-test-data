package enumtest

class Door {
    DoorStatus status
}


enum DoorStatus {
    OPEN("Open"),
    CLOSED("Closed")

    String status

    DoorStatus(String status) {
        this.status = status
    }

    static list() {
        EnumSet.allOf(DoorStatus)
    }

    // this blows up...
    static get(String s) {
        EnumSet.allOf(DoorStatus.class).find {
            it.status == s
        }
    }
}
