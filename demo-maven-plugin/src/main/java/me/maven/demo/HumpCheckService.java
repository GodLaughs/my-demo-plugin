package me.maven.demo;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.regex.Pattern;

/**
 * @author caihui006@ke.com
 * @summary 驼峰检查服务
 * @since 2019-12-01 16:24
 */
class HumpCheckService {

    private Log log = new SystemStreamLog();

    /**
     * Regexp for one-char loop variables.
     */
    private static Pattern sSingleChar = Utils.getPattern("^[a-z]$");

    private static Pattern format = Utils.getPattern("^[a-z][a-zA-Z0-9]*$", 0);

    void process(FileText fileText) throws RecognitionException, TokenStreamException {
        final FileContents contents = new FileContents(fileText);
        final DetailAST rootAST = TreeWalker.parse(contents);
        notifyBegin(contents);
        processIter(rootAST);
    }

    private void processIter(DetailAST root) {
        DetailAST currNode = root;
        while (currNode != null) {
            notifyVisit(currNode);
            DetailAST toVisit = currNode.getFirstChild();
            while (currNode != null && toVisit == null) {
                toVisit = currNode.getNextSibling();
                currNode = currNode.getParent();
            }
            currNode = toVisit;
        }
    }

    private void notifyVisit(DetailAST ast) {
        if (mustCheckName(ast)) {
            final DetailAST nameAST = ast.findFirstToken(TokenTypes.IDENT);
            if (!format.matcher(nameAST.getText()).find()) {
                log.info("找到不符合驼峰命名的变量了:位于第" + ast.getLineNo() + "行" + "，名为：" + nameAST.getText());
            }
        }
    }

    private boolean mustCheckName(DetailAST ast) {
        final DetailAST modifiersAST =
                ast.findFirstToken(TokenTypes.MODIFIERS);
        final boolean isFinal = (modifiersAST != null)
                && modifiersAST.branchContains(TokenTypes.FINAL);
        if (isForLoopVariable(ast)) {
            DetailAST firstToken = ast.findFirstToken(TokenTypes.IDENT);
            if (firstToken != null) {
                return !sSingleChar.matcher(firstToken.getText()).find();
            } else {
                return false;
            }
        }
        return (!isFinal && ScopeUtils.isLocalVariableDef(ast));
    }

    private boolean isForLoopVariable(DetailAST aVariableDef) {
        DetailAST parent = aVariableDef.getParent();
        if (parent != null) {
            final int parentType = parent.getType();
            return parentType == TokenTypes.FOR_INIT
                    || parentType == TokenTypes.FOR_EACH_CLAUSE;
        } else {
            return false;
        }
    }

    private void notifyBegin(FileContents contents) {
        log.info("\n开始校验" + contents.getFilename() + "的变量命名是否符合驼峰规范");
    }

    private void notifyEnd(FileContents contents) {
        log.info("\n" + contents.getFilename() + "的变量命名校验已结束");
    }
}
