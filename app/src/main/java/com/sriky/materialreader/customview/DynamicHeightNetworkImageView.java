package com.sriky.materialreader.customview;

import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

public class DynamicHeightNetworkImageView extends NetworkImageView {
    private float mAspectRatio = 1.5f;

    public DynamicHeightNetworkImageView(Context context) {
        super(context);
    }

    public DynamicHeightNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int aspectRatioSupportHeight = (int)((float)MeasureSpec.getSize(widthMeasureSpec) * mAspectRatio);
        int aspectRationSpec = MeasureSpec.makeMeasureSpec(aspectRatioSupportHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, aspectRationSpec);
    }
}
