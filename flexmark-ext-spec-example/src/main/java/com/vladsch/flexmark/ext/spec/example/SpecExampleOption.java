package com.vladsch.flexmark.ext.spec.example;

import com.vladsch.flexmark.internal.util.sequence.BasedSequence;
import com.vladsch.flexmark.node.CustomNode;
import com.vladsch.flexmark.node.Visitor;

/**
 * A SpecExample block node
 */
public class SpecExampleOption extends CustomNode {
    @Override
    public void getAstExtra(StringBuilder out) {
        astExtraChars(out);
    }

    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    public SpecExampleOption() {
    }

    public SpecExampleOption(BasedSequence chars) {
        super(chars);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}