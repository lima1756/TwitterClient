package com.ivanmorett.twitterclient;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;

public class ProgressBarController {
    private boolean isDanger, isWarning;
    private Drawable progressDrawable;
    private ProgressBar pb;

    public ProgressBarController(ProgressBar pb){
        this.pb = pb;
        isDanger = false;
        isWarning = false;
        LayerDrawable progressBarDrawable = (LayerDrawable) this.pb.getProgressDrawable();
        progressDrawable = progressBarDrawable.getDrawable(0);
    }

    private void changeColor(Context context, int color){

    }

    public boolean verifyStatus(Context context, int count)
    {
        if(count>=200 && count <=240){
            setColor(context,true, false, R.color.colorWarning);
        }
        else if(count>240){
            setColor(context,false, true, R.color.colorDanger);
        }
        else if(isWarning || isDanger){
            setColor(context, false, false, R.color.colorAccent);
        }
        return isDanger || isWarning;
    }

    public boolean isOk(){
        return !isDanger;
    }

    public void updateSize(int count){
        pb.setProgress(count);
    }

    public void restartProgress()
    {
        pb.setProgress(0);
    }

    private void setColor(Context context, boolean warning, boolean danger, int color){
        isWarning = warning;
        isDanger = danger;
        progressDrawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN);
    }
}
