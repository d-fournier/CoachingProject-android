package fr.sims.coachingproject.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import org.markdownj.MarkdownProcessor;

/**
 * Created by dfour on 11/03/2016.
 */
public class MarkDownView extends WebView {
    public MarkDownView(Context context) {
        super(context);
    }

    public MarkDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean setMarkdown(String md) {
        MarkdownProcessor m = new MarkdownProcessor();
        String html = m.markdown(md);
        if (html != null) {
            loadData(html, "text/html; charset=UTF-8", null);
            return true;
        }
        return false;
    }
}
