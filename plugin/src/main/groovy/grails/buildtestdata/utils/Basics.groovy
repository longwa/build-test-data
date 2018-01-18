package grails.buildtestdata.utils

import java.sql.Time
import java.sql.Timestamp
import java.time.*

class Basics {
    protected static final List<Class> BASIC_TYPES = [
        String,
        Boolean,
        Byte,
        Short,
        Integer,
        Long,
        Float,
        Double,
        Character,
        Calendar,
        Date,
        Time,
        java.sql.Date,
        UUID,
        URL,
        URI,
        Currency,
        TimeZone,

        LocalDate,
        LocalDateTime,
        LocalTime,

        OffsetDateTime,
        OffsetTime,
        ZonedDateTime,
        Instant
    ]
    static boolean isBasicType(Class type){
        if(type.isArray()) isBasicType(type.componentType)        
        type.isPrimitive() || BASIC_TYPES.contains(type)
    }
    
    static def getArrayValue(Class type){
        type = boxPrimitive(type.componentType)
        switch(type){
            case Byte:
                byte[] inputBytes = [71, 73, 70, 56, 57, 97, 1, 0, 1, 0, -111, -1, 0, -1, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, 0, 33, -1, 11, 65, 68, 79, 66, 69, 58, 73, 82, 49, 46, 48, 2, -34, -19, 0, 33, -7, 4, 1, 0, 0, 2, 0, 44, 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 2, 84, 1, 0, 59]
                return inputBytes
            default:
                def value = getDefaultValue(type)                
                return [value]
        }
    }
    
    static Class boxPrimitive(Class type){
        if(!type.isPrimitive()) return type
        if(Byte.TYPE == type) return Byte;
        if(Long.TYPE == type) return Long;
        if(Double.TYPE == type) return Double;
        if(Integer.TYPE == type) return Integer;
        if(Float.TYPE == type) return Float;
        if(Boolean.TYPE == type) return Boolean;
        if(Character.TYPE == type) return Character;
        return type
    }
    
    static def getDefaultValue(Class type){
        type = boxPrimitive(type)
        switch(type) {
            case String:
                return ""
            case Calendar:
                return new GregorianCalendar()
            case Character:
                return 'a'
            case Currency:
                return Currency.getInstance(Locale.default)
            case TimeZone:
                return TimeZone.default
            case Locale:
                return Locale.default
            case java.sql.Date:
                return new java.sql.Date(new Date().time)
            case Time:
                return new Time(new Date().time)
            case Timestamp:
                return new Timestamp(new Date().time)
            case Date:
                return new Date()
            case Boolean:
                return false
            case { it in Number }:
                return 0
            case UUID:
                return UUID.randomUUID()
            case URI:
                return new URI('http://www.ietf.org/rfc/rfc2396.txt')
            case URL:
                return new URL('http://www.ietf.org/rfc/rfc2396.txt')
            case OffsetDateTime:
                return OffsetDateTime.now()
            case OffsetTime:
                return OffsetTime.now()
            case Instant:
                return Instant.now()
            case ZonedDateTime:
                return ZonedDateTime.now()
            case LocalDate:
                return LocalDate.now()
            case LocalDateTime:
                return LocalDateTime.now()
            case LocalTime:
                return LocalTime.now()
            case Byte:
                return 71
            case Enum:
                return type.enumConstants[0]
            default:
                return null
        }
    }
    
    static def getBasicValue(Class type){
        type.isArray()? getArrayValue(type):getDefaultValue(type)        
    }
}
