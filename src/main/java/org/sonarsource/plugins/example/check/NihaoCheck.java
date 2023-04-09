package org.sonarsource.plugins.example.check;

import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "line1")
public class NihaoCheck extends BaseTreeVisitor implements JavaFileScanner {

    private JavaFileScannerContext context;
    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        Loggers.get(getClass()).info("scan start");
        scan(context.getTree());
    }

    @Override
    public void visitLiteral(LiteralTree tree) {
        if (tree.is(Tree.Kind.STRING_LITERAL)) {
            String value = ((ExpressionTree) tree).toString();
            if (value.contains("nihao")) {
                Loggers.get(getClass()).info("11111111111111111111");
                context.reportIssue(this, tree, "遇到了'nihao'关键字");
            }
        }
        super.visitLiteral(tree);
    }
}



