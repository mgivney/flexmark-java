package com.vladsch.flexmark.ext.xwiki.macros;

import com.vladsch.flexmark.ast.CustomBlock;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.HashMap;
import java.util.Map;

/**
 * A macros block node
 */
@SuppressWarnings("WeakerAccess")
public class MacroBlock extends CustomBlock {

    @Override
    public void getAstExtra(StringBuilder out) {
        if (isClosedTag()) out.append(" isClosed");
        segmentSpanChars(out, getMacroContentChars(), "macroContent");
    }

    @Override
    public BasedSequence[] getSegments() {
        return Node.EMPTY_SEGMENTS;
    }

    public MacroBlock() {
    }

    public MacroBlock(BasedSequence chars) {
        super(chars);
    }

    public MacroBlock(Node node) {
        super();
        appendChild(node);
        this.setCharsFromContent();
    }

    public Map<String, String> getAttributes() {
        return getMacroNode().getAttributes();
    }

    public Macro getMacroNode() {
        Node firstChild = getFirstChild();
        assert firstChild instanceof Macro;
        return (Macro) firstChild;
    }

    public boolean isClosedTag() {
        return getMacroNode().isClosedTag();
    }

    public BasedSequence getMacroContentChars() {
        Node firstChild = getFirstChild();
        Node lastChild = getLastChild();
        Node firstContentNode = firstChild.getNext();
        Node lastContentNode = lastChild instanceof MacroClose ? lastChild.getPrevious() : lastChild;

        //noinspection UnnecessaryLocalVariable
        BasedSequence contentChars = Node.spanningChars(firstContentNode == null || firstContentNode instanceof MacroClose ? BasedSequence.NULL : firstContentNode.getChars(),
                lastContentNode == null || lastContentNode == firstChild ? BasedSequence.NULL : lastContentNode.getChars());

        return contentChars;
    }
}
