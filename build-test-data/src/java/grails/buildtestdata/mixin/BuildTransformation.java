package grails.buildtestdata.mixin;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.test.TestForTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class BuildTransformation extends TestForTransformation {
    private static final ClassNode MY_TYPE = new ClassNode(Build.class);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();

    @Override
    public void visit(ASTNode[] astNodes, SourceUnit source) {
        if (!(astNodes[0] instanceof AnnotationNode) || !(astNodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: " + astNodes[0].getClass() + " / " + astNodes[1].getClass());
        }

        AnnotatedNode parent = (AnnotatedNode) astNodes[1];
        AnnotationNode node = (AnnotationNode) astNodes[0];
        if (!MY_TYPE.equals(node.getClassNode()) || !(parent instanceof ClassNode)) {
            return;
        }

        ClassNode classNode = (ClassNode) parent;
        String cName = classNode.getName();
        if (classNode.isInterface()) {
            error(source, "Error processing interface '" + cName + "'. " + MY_TYPE_NAME + " not allowed for interfaces.");
        }

        ListExpression values = getListOfClasses(node);
        if (values == null) {
            error(source, "Error processing class '" + cName + "'. " + MY_TYPE_NAME + " annotation expects a class or a list of classes to mock");
            return;
        }

        List<ClassExpression> domainClassNodes = new ArrayList<ClassExpression>();
        for (Expression expression : values.getExpressions()) {
            if (expression instanceof ClassExpression) {
                ClassExpression classEx = (ClassExpression) expression;
                domainClassNodes.add(classEx);
            }
        }

        if (!domainClassNodes.isEmpty()) {
            weaveMixinClass(classNode, BuildTestDataUnitTestMixin.class);
        }
    }
}
