package com.vladsch.flexmark.ext.wikilink;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkJiraRenderer;
import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkLinkRefProcessor;
import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkLinkResolver;
import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Extension for wikilinks
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link com.vladsch.flexmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link com.vladsch.flexmark.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed emoji shortcuts text regions are turned into {@link com.vladsch.flexmark.ext.wikilink.WikiLink} nodes.
 * </p>
 */
public class WikiLinkExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    public static final DataKey<Boolean> ALLOW_INLINES = new DataKey<>("ALLOW_INLINES", false);
    public static final DataKey<Boolean> ALLOW_ANCHORS = new DataKey<>("ALLOW_ANCHORS", false);
    public static final DataKey<Boolean> ALLOW_ANCHOR_ESCAPE = new DataKey<>("ALLOW_ANCHOR_ESCAPE", false);
    public static final DataKey<Boolean> ALLOW_PIPE_ESCAPE = new DataKey<>("ALLOW_PIPE_ESCAPE", false);
    public static final DataKey<Boolean> DISABLE_RENDERING = new DataKey<>("DISABLE_RENDERING", false);
    public static final DataKey<Boolean> LINK_FIRST_SYNTAX = new DataKey<>("LINK_FIRST_SYNTAX", false);
    public static final DataKey<String> LINK_PREFIX = new DataKey<>("LINK_PREFIX", "");
    public static final DataKey<String> IMAGE_PREFIX = new DataKey<>("IMAGE_PREFIX", "");
    public static final DataKey<Boolean> IMAGE_LINKS = new DataKey<>("IMAGE_LINKS", false);
    public static final DataKey<String> LINK_FILE_EXTENSION = new DataKey<>("LINK_FILE_EXTENSION", "");
    public static final DataKey<String> IMAGE_FILE_EXTENSION = new DataKey<>("IMAGE_FILE_EXTENSION", "");
    public static final LinkType WIKI_LINK = new LinkType("WIKI");

    private WikiLinkExtension() {
    }

    public static Extension create() {
        return new WikiLinkExtension();
    }

    @Override
    public void rendererOptions(final MutableDataHolder options) {

    }

    @Override
    public void parserOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.linkRefProcessorFactory(new WikiLinkLinkRefProcessor.Factory());
    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        switch (rendererType) {
            case "HTML":
                rendererBuilder.nodeRendererFactory(new NodeRendererFactory() {
                    @Override
                    public NodeRenderer create(DataHolder options) {
                        return new WikiLinkNodeRenderer(options);
                    }
                });
                rendererBuilder.linkResolverFactory(new WikiLinkLinkResolver.Factory());
                break;

            case "JIRA":
            case "YOUTRACK":
                rendererBuilder.nodeRendererFactory(new NodeRendererFactory() {
                    @Override
                    public NodeRenderer create(DataHolder options) {
                        return new WikiLinkJiraRenderer(options);
                    }
                });
                rendererBuilder.linkResolverFactory(new WikiLinkLinkResolver.Factory());
                break;
        }
    }
}
