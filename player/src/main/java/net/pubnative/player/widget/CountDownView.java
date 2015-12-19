package net.pubnative.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.pubnative.player.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class CountDownView extends FrameLayout
{
    private ProgressBar progressBarView;
    private TextView    progressTextView;

    public CountDownView(Context context)
    {
        super(context);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx)
    {
        View rootView = inflate(ctx, R.layout.pubnative_player_count_down, this);
        progressBarView = (ProgressBar) rootView.findViewById(R.id.view_progress_bar);
        progressTextView = (TextView) rootView.findViewById(R.id.view_progress_text);
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
    }

    public void setProgress(int currentMs, int totalMs)
    {
        progressBarView.setMax(totalMs);
        progressBarView.setSecondaryProgress(totalMs);
        progressBarView.setProgress(currentMs);
        int remainSec = (totalMs - currentMs) / 1000 + 1;
        progressTextView.setText(String.valueOf(remainSec));
    }
}
