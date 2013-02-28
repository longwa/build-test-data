package grails.buildtestdata.mixin;

import grails.test.mixin.support.MixinMethod;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.GrailsArtefactClassInjector;
import org.codehaus.groovy.grails.compiler.injection.test.TestForTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.junit.Before;

import java.lang.reflect.Modifier;
import java.util.Map;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class BuildTransformation extends TestForTransformation {

    private static final ClassNode MY_TYPE = new ClassNode(Build.class);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();

    @Override
    public void visit(ASTNode[] astNodes, SourceUnit source) {
        if (!(astNodes[0] instanceof AnnotationNode) || !(astNodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: " + astNodes[0].getClass() +
                    " / " + astNodes[1].getClass());
        }

        AnnotatedNode parent = (AnnotatedNode) astNodes[1];
        AnnotationNode node = (AnnotationNode) astNodes[0];
        if (!MY_TYPE.equals(node.getClassNode()) || !(parent instanceof ClassNode)) {
            return;
        }

        ClassNode classNode = (ClassNode) parent;
        String cName = classNode.getName();
        if (classNode.isInterface()) {
            error(source, "Error processing interface '" + cName + "'. " + MY_TYPE_NAME +
                    " not allowed for interfaces.");
        }

        ListExpression values = getListOfClasses(node);
        if (values == null) {
            error(source, "Error processing class '" + cName + "'. " + MY_TYPE_NAME +
                    " annotation expects a class or a list of classes to mock");
            return;
        }

        weaveMixinClass(classNode, BuildTestDataUnitTestMixin.class);
        for (Expression expression : values.getExpressions()) {
            if (expression instanceof ClassExpression) {
                weaveMock(classNode, (ClassExpression) expression, false);
                addBuildCollaboratorToSetup(classNode, (ClassExpression) expression);
            }
        }
    }

    private void addBuildCollaboratorToSetup(ClassNode classNode, ClassExpression targetClassExpression) {
        BlockStatement methodBody;
        if (isJunit3Test(classNode)) {
            methodBody = getOrCreateNoArgsMethodBody(classNode, SET_UP_METHOD);
            addBuildCollaborator(targetClassExpression, methodBody);
        } else {
            addToJunit4BeforeMethods(classNode, targetClassExpression);
        }
    }

    private void addToJunit4BeforeMethods(ClassNode classNode, ClassExpression targetClassExpression) {
        Map<String, MethodNode> declaredMethodsMap = classNode.getDeclaredMethodsMap();
        boolean weavedIntoBeforeMethods = false;
        for (MethodNode methodNode : declaredMethodsMap.values()) {
            if (isDeclaredBeforeMethod(methodNode)) {
                Statement code = getMethodBody(methodNode);
                addBuildCollaborator(targetClassExpression, (BlockStatement) code);
                weavedIntoBeforeMethods = true;
            }
        }

        if (!weavedIntoBeforeMethods) {
            BlockStatement junit4Setup = getJunit4Setup(classNode);
            addBuildCollaborator(targetClassExpression, junit4Setup);
        }
    }

    private boolean isDeclaredBeforeMethod(MethodNode methodNode) {
        return isPublicInstanceMethod(methodNode) && hasAnnotation(methodNode, Before.class) && !hasAnnotation(methodNode, MixinMethod.class);
    }

    private boolean isPublicInstanceMethod(MethodNode methodNode) {
        return !methodNode.isSynthetic() && !methodNode.isStatic() && methodNode.isPublic();
    }

    private Statement getMethodBody(MethodNode methodNode) {
        Statement code = methodNode.getCode();
        if (!(code instanceof BlockStatement)) {
            BlockStatement body = new BlockStatement();
            body.addStatement(code);
            code = body;
        }
        return code;
    }

    private BlockStatement getJunit4Setup(ClassNode classNode) {
        MethodNode setupMethod = classNode.getMethod(SET_UP_METHOD, GrailsArtefactClassInjector.ZERO_PARAMETERS);
        if (setupMethod == null) {
            setupMethod = new MethodNode(SET_UP_METHOD, Modifier.PUBLIC,ClassHelper.VOID_TYPE,GrailsArtefactClassInjector.ZERO_PARAMETERS,null,new BlockStatement());
            setupMethod.addAnnotation(MIXIN_METHOD_ANNOTATION);
            classNode.addMethod(setupMethod);
        }
        if (setupMethod.getAnnotations(BEFORE_CLASS_NODE).size() == 0) {
            setupMethod.addAnnotation(BEFORE_ANNOTATION);
        }
        return getOrCreateMethodBody(classNode, setupMethod, SET_UP_METHOD);

    }

    protected void addBuildCollaborator(ClassExpression targetClass, BlockStatement methodBody) {
        ArgumentListExpression args = new ArgumentListExpression();
        args.addExpression(targetClass);
        methodBody.getStatements().add(0, new ExpressionStatement(new MethodCallExpression(THIS_EXPRESSION, "mockForBuild", args)));
    }
}
