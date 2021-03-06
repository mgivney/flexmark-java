package com.vladsch.flexmark.ext.xwiki.macros;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.xwiki.macros.internal.*;
import com.vladsch.flexmark.ext.xwiki.macros.internal.MacroBlockParser;
import com.vladsch.flexmark.ext.xwiki.macros.internal.MacroNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Extension for macross
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link com.vladsch.flexmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link com.vladsch.flexmark.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed macros text is turned into {@link Macro} nodes.
 * </p>
 */
public class MacroExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    public static final DataKey<Boolean> ENABLE_INLINE_MACROS = new DataKey<>("ENABLE_INLINE_MACROS", true);
    public static final DataKey<Boolean> ENABLE_BLOCK_MACROS = new DataKey<>("ENABLE_BLOCK_MACROS", true);
    public static final DataKey<Boolean> ENABLE_RENDERING = new DataKey<>("ENABLE_RENDERING", false);

    private MacroExtension() {
    }

    @Override
    public void rendererOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(final HtmlRenderer.Builder rendererBuilder, final String rendererType) {
        rendererBuilder.nodeRendererFactory(new NodeRendererFactory() {
            @Override
            public NodeRenderer create(DataHolder options) {
                return new MacroNodeRenderer(options);
            }
        });
    }

    public static Extension create() {
        return new MacroExtension();
    }

    @Override
    public void parserOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        if (ENABLE_BLOCK_MACROS.getFrom(parserBuilder)) parserBuilder.customBlockParserFactory(new MacroBlockParser.Factory());
        if (ENABLE_INLINE_MACROS.getFrom(parserBuilder)) parserBuilder.customInlineParserExtensionFactory(new MacroInlineParser.Factory());
    }
}
