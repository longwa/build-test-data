package grails.buildtestdata.handler

import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import nl.flotsam.xeger.Xeger
import org.grails.datastore.gorm.validation.constraints.MatchesConstraint
import org.grails.datastore.mapping.validation.ValidationErrors

@CompileStatic
class MatchesConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty) {
        // If what we have already matches, we are good
        if(!appliedConstraint.validate(instance,getValue(instance,propertyName),new ValidationErrors(instance))){
            MatchesConstraint matchesConstraint = appliedConstraint as MatchesConstraint
            Xeger generator = new Xeger(toAutomatonRegex(matchesConstraint.regex))
            setValue(instance,propertyName,generator.generate())
        }
    }

    /** The java.util.regex character class shorthands the automaton library lacks, with their expanded form */
    static final Map<String, String> CHARACTER_CLASS_SHORTHANDS = [
        d: '[0-9]',
        D: '[^0-9]',
        w: '[a-zA-Z0-9_]',
        W: '[^a-zA-Z0-9_]',
        s: '[ \t\n\r\f]',
        S: '[^ \t\n\r\f]',
    ].asImmutable()

    /** A leading embedded flag expression such as {@code (?ix)} */
    static final java.util.regex.Pattern INLINE_FLAGS = ~/^\(\?([a-zA-Z]+)\)/

    /**
     * Rewrites a java.util.regex pattern into the dialect of the automaton library generating the values: a leading
     * embedded flag expression and the anchors are dropped, comments mode is resolved and the \d, \w and \s style
     * shorthands are expanded. The library would otherwise treat them as literal characters and generate values that
     * don't match. The flags themselves only make the pattern more permissive, so generating from the pattern as
     * written still yields a matching value.
     */
    static String toAutomatonRegex(String regex) {
        String flagless = regex
        java.util.regex.Matcher flagMatcher = INLINE_FLAGS.matcher(regex)
        if (flagMatcher.find()) {
            flagless = regex.substring(flagMatcher.end())
            if (flagMatcher.group(1).contains('x')) {
                flagless = stripComments(flagless)
            }
        }
        String anchorless = stripAnchors(flagless)
        StringBuilder result = new StringBuilder(anchorless.length())
        boolean inCharacterClass = false
        for (int i = 0; i < anchorless.length(); i++) {
            char current = anchorless.charAt(i)
            if (current == ('\\' as char) && i + 1 < anchorless.length()) {
                String escaped = anchorless.charAt(i + 1) as String
                String expansion = CHARACTER_CLASS_SHORTHANDS[escaped]
                if (expansion == null || (inCharacterClass && expansion.startsWith('[^'))) {
                    // not a shorthand, or a negated one which can't be nested in a character class
                    result.append(current).append(escaped)
                }
                else {
                    result.append(inCharacterClass ? expansion.substring(1, expansion.length() - 1) : expansion)
                }
                i++
            }
            else {
                if (current == ('[' as char)) {
                    inCharacterClass = true
                }
                else if (current == (']' as char)) {
                    inCharacterClass = false
                }
                result.append(current)
            }
        }
        result.toString()
    }

    /**
     * Resolves the COMMENTS flag: outside a character class, unescaped whitespace is ignored and # starts a comment
     * running to the end of the line
     */
    static String stripComments(String regex) {
        StringBuilder result = new StringBuilder(regex.length())
        boolean inCharacterClass = false
        boolean inComment = false
        for (int i = 0; i < regex.length(); i++) {
            char current = regex.charAt(i)
            if (inComment) {
                inComment = current != ('\n' as char)
                continue
            }
            if (current == ('\\' as char) && i + 1 < regex.length()) {
                result.append(current).append(regex.charAt(i + 1))
                i++
            }
            else if (inCharacterClass) {
                inCharacterClass = current != (']' as char)
                result.append(current)
            }
            else if (current == ('#' as char)) {
                inComment = true
            }
            else if (!Character.isWhitespace(current)) {
                inCharacterClass = current == ('[' as char)
                result.append(current)
            }
        }
        result.toString()
    }

    /**
     * The matches constraint (and Bean Validation's @Pattern) match the whole value, so ^ and $ anchors are redundant.
     */
    static String stripAnchors(String regex) {
        String result = regex
        if (result.startsWith('^')) {
            result = result.substring(1)
        }
        if (result.endsWith('$') && !result.endsWith('\\$')) {
            result = result.substring(0, result.length() - 1)
        }
        result
    }
}

