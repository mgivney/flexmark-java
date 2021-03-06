
package com.vladsch.flexmark.ext.jekyll.front.matter;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.jekyll.front.matter.internal.JekyllFrontMatterBlockParser;
import com.vladsch.flexmark.ext.jekyll.front.matter.internal.JekyllFrontMatterNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Extension for jekyll_front_matters
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link com.vladsch.flexmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link com.vladsch.flexmark.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed jekyll_front_matter text is turned into {@link JekyllFrontMatterBlock} nodes.
 * </p>
 */
public class JekyllFrontMatterExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    private JekyllFrontMatterExtension() {
    }

    public static Extension create() {
        return new JekyllFrontMatterExtension();
    }

    @Override
    public void rendererOptions(final MutableDataHolder options) {

    }

    @Override
    public void parserOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new JekyllFrontMatterBlockParser.Factory());
    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        switch (rendererType) {
            case "HTML":
                rendererBuilder.nodeRendererFactory(new NodeRendererFactory() {
                    @Override
                    public NodeRenderer create(DataHolder options) {
                        return new JekyllFrontMatterNodeRenderer(options);
                    }
                });
                break;

            case "JIRA":
            case "YOUTRACK":
                break;
        }
    }
}
