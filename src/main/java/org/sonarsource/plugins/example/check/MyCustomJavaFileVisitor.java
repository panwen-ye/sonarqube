package org.sonarsource.plugins.example.check;

import org.sonar.java.model.declaration.VariableTreeImpl;
import org.sonar.java.model.expression.MethodInvocationTreeImpl;
import org.sonar.java.model.statement.ExpressionStatementTreeImpl;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyCustomJavaFileVisitor extends BaseTreeVisitor implements JavaFileScanner {

    private JavaFileScannerContext context;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        scan( context.getTree() );
    }

    @Override
    public void visitBlock(BlockTree tree) {
        List<StatementTree> statementTrees = tree.body();
        if (statementTrees != null && statementTrees.size() > 0) {
            for (StatementTree tree1 : statementTrees) {
                if (tree1 instanceof VariableTreeImpl) {
                    CompletableFuture.runAsync( () -> System.out.println(1) );
                    executorServiceCheck( (VariableTreeImpl) tree1 );
                } else if (tree1 instanceof ExpressionStatementTreeImpl) {
                    completableFutureCheck( (ExpressionStatementTreeImpl) tree1 );
                }
            }
        }
        super.visitBlock( tree );
    }

    @Override
    public void visitAnnotation(AnnotationTree annotationTree) {
        if(annotationTree.annotationType().toString().equals( "Async" )){
            if(annotationTree.arguments().size() == 0){
                context.reportIssue( this, annotationTree, "Unified thread pool is used except for special requirement thread pool" );
            }
        }
        super.visitAnnotation( annotationTree );
    }

    private void executorServiceCheck(VariableTreeImpl variableTree) {
        if (variableTree.type().toString().equals( "ExecutorService" )) {
            List<org.sonar.plugins.java.api.tree.Tree> treeList = variableTree.getChildren();
            if (treeList != null && treeList.size() > 0) {
                for (Tree tree2 : treeList) {
                    if (tree2 instanceof MethodInvocationTreeImpl) {
                        MethodInvocationTreeImpl methodInvocationTree = (MethodInvocationTreeImpl) tree2;
                        if (methodInvocationTree.symbol().name().equals( "newFixedThreadPool" )) {
                            context.reportIssue( this, methodInvocationTree, "Unified thread pool is used except for special requirement thread pool" );
                        }
                    }
                }
            }
        }
    }

    private void completableFutureCheck(ExpressionStatementTreeImpl expressionStatementTree) {
        MethodInvocationTreeImpl methodInvocationTree = (MethodInvocationTreeImpl) expressionStatementTree.expression();
        if(methodInvocationTree != null && methodInvocationTree.arguments().size() < 2){
            context.reportIssue( this, expressionStatementTree, "Unified thread pool is used except for special requirement thread pool" );
        }
    }

}
