package com.vladsch.flexmark.ext.xwiki.macros.internal;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.xwiki.macros.Macro;
import com.vladsch.flexmark.ext.xwiki.macros.MacroAttribute;
import com.vladsch.flexmark.ext.xwiki.macros.MacroClose;
import com.vladsch.flexmark.parser.InlineParser;
import com.vladsch.flexmark.parser.InlineParserExtension;
import com.vladsch.flexmark.parser.InlineParserExtensionFactory;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class MacroInlineParser implements InlineParserExtension {
    private final MacroParsing parsing;
    private List<Macro> openMacros;

    public MacroInlineParser(final InlineParser inlineParser) {
        this.parsing = new MacroParsing(inlineParser.getParsing());
        this.openMacros = new ArrayList<>();
    }

    @Override
    public void finalizeDocument(final InlineParser inlineParser) {

    }

    @Override
    public void finalizeBlock(final InlineParser inlineParser) {
        for (int j = openMacros.size(); j-- > 0; ) {
            inlineParser.moveNodes(openMacros.get(j), inlineParser.getBlock().getLastChild());
        }

        openMacros.clear();
    }

    @Override
    public boolean parse(final InlineParser inlineParser) {
        if (inlineParser.peek(1) == '{') {
            BasedSequence input = inlineParser.getInput();
            int index = inlineParser.getIndex();
            Matcher matcher = inlineParser.matcher(parsing.MACRO_TAG);
            if (matcher != null) {
                BasedSequence macroOpen = input.subSequence(matcher.start(), matcher.end());

                // see what we have
                if (macroOpen.charAt(2) == '/') {
                    // close
                    BasedSequence macroName = input.subSequence(matcher.start(2), matcher.end(2));
                    for (int i = openMacros.size(); i-- > 0; ) {
                        if (openMacros.get(i).getName().equals(macroName)) {
                            // this one is now closed, we close all intervening ones too
                            inlineParser.flushTextNode();

                            for (int j = openMacros.size(); j-- > i; ) {
                                inlineParser.moveNodes(openMacros.get(j), inlineParser.getBlock().getLastChild());
                            }

                            MacroClose macroClose = new MacroClose(macroOpen.subSequence(0, 3), macroName, macroOpen.endSequence(2));
                            inlineParser.getBlock().appendChild(macroClose);
                            inlineParser.moveNodes(openMacros.get(i), macroClose);

                            if (i == 0) {
                                openMacros.clear();
                            } else {
                                openMacros = openMacros.subList(0, i);
                            }

                            return true;
                        }
                    }
                } else {
                    // open, see if open/close
                    BasedSequence macroName = input.subSequence(matcher.start(1), matcher.end(1));
                    boolean isClosedTag = macroOpen.endCharAt(3) == '/';
                    Macro macro = new Macro(macroOpen.subSequence(0, 2), macroName, macroOpen.endSequence(isClosedTag ? 3 : 2));
                    macro.setCharsFromContent();

                    inlineParser.flushTextNode();
                    inlineParser.getBlock().appendChild(macro);

                    if (!isClosedTag) {
                        openMacros.add(macro);
                    }

                    BasedSequence attributeText = macroOpen.baseSubSequence(macroName.getEndOffset(), macro.getClosingMarker().getStartOffset()).trim();
                    if (!attributeText.isEmpty()) {
                        // have some attribute text
                        macro.setAttributeText(attributeText);

                        // parse attributes
                        Matcher attributeMatcher = parsing.MACRO_ATTRIBUTE.matcher(attributeText);
                        while (attributeMatcher.find()) {
                            BasedSequence attributeName = attributeText.subSequence(attributeMatcher.start(1), attributeMatcher.end(1));
                            BasedSequence attributeSeparator = attributeMatcher.groupCount() == 1 || attributeMatcher.start(2) == -1 ? BasedSequence.NULL : attributeText.subSequence(attributeMatcher.end(1), attributeMatcher.start(2)).trim();
                            BasedSequence attributeValue = attributeMatcher.groupCount() == 1 || attributeMatcher.start(2) == -1 ? BasedSequence.NULL : attributeText.subSequence(attributeMatcher.start(2), attributeMatcher.end(2));
                            boolean isQuoted = attributeValue.length() >= 2 && (attributeValue.charAt(0) == '"' && attributeValue.endCharAt(1) == '"' || attributeValue.charAt(0) == '\'' && attributeValue.endCharAt(1) == '\'');
                            BasedSequence attributeOpen = !isQuoted ? BasedSequence.NULL : attributeValue.subSequence(0, 1);
                            BasedSequence attributeClose = !isQuoted ? BasedSequence.NULL : attributeValue.endSequence(1, 0);

                            if (isQuoted) {
                                attributeValue = attributeValue.midSequence(1,-1);
                            }

                            MacroAttribute attribute = new MacroAttribute(attributeName, attributeSeparator, attributeOpen, attributeValue, attributeClose);
                            macro.appendChild(attribute);
                        }
                    }

                    return true;
                }

                inlineParser.setIndex(index);
            }
        }
        return false;
    }

    public static class Factory implements InlineParserExtensionFactory {
        @Override
        public Set<Class<? extends InlineParserExtensionFactory>> getAfterDependents() {
            return null;
        }

        @Override
        public CharSequence getCharacters() {
            return "{";
        }

        @Override
        public Set<Class<? extends InlineParserExtensionFactory>> getBeforeDependents() {
            return null;
        }

        @Override
        public InlineParserExtension create(final InlineParser inlineParser) {
            return new MacroInlineParser(inlineParser);
        }

        @Override
        public boolean affectsGlobalScope() {
            return false;
        }
    }
}
