package compony.lcxj.com.ratingviewdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import compony.lcxj.com.ratingviewdemo.R;

/**
 * Created by liao on 2017/5/17.
 */

public class RatingView extends View {
    //总的星数
    private int ratingNum = 5;
    //当前星级
    private float rating = 1.7f;
    //星星大小
    private int ratingSize = 10;
    //星星间隔
    private int ratingMargin = 10;
    //星星背景图片
    private int ratingDrawableResId;
    //选中是的颜色
    private int selectColor;
    //按下的时间
    private long downTime;
    //是否只是整型
    private boolean isInteger;
    private boolean userEnable;

    private OnRatingChangeListener onRatingChangeListener;

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingView);
        ratingNum = typedArray.getInteger(R.styleable.RatingView_ratingNum, 5);
        rating = typedArray.getFloat(R.styleable.RatingView_rating, 1.7f);
        ratingSize = typedArray.getDimensionPixelSize(R.styleable.RatingView_ratingSize, 10);
        ratingMargin = typedArray.getDimensionPixelOffset(R.styleable.RatingView_ratingMargin, 10);
        ratingDrawableResId = typedArray.getResourceId(R.styleable.RatingView_ratingDrawable, -1);
        selectColor = typedArray.getColor(R.styleable.RatingView_ratingSlectColor, Color.RED);
        userEnable = typedArray.getBoolean(R.styleable.RatingView_userEnable, true);
        isInteger = typedArray.getBoolean(R.styleable.RatingView_isInteger, false);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //绘制星星背景
        for (int i = 0; i < ratingNum; i++) {
            canvas.drawBitmap(getRatingBg(), getPaddingLeft() + i * (ratingSize + ratingMargin), getPaddingTop(), paint);
        }
        //绘制选中星级
        int selectNum = (int) rating;
        float reNum = rating % selectNum;
        if (selectNum > 0) {
            for (int i = 0; i < selectNum; i++) {
                canvas.drawBitmap(createSelectRate(1f), getPaddingLeft() + i * (ratingSize + ratingMargin), getPaddingTop(), paint);
            }
            if (reNum > 0) {
                if (isInteger) {
                    canvas.drawBitmap(createSelectRate(1f), getPaddingLeft() + selectNum * (ratingSize + ratingMargin), getPaddingTop(), paint);
                } else {
                    canvas.drawBitmap(createSelectRate(reNum), getPaddingLeft() + selectNum * (ratingSize + ratingMargin), getPaddingTop(), paint);
                }
            }
        } else {
            if (rating > 0) {
                if (isInteger) {
                    canvas.drawBitmap(createSelectRate(1f), getPaddingLeft() + selectNum * (ratingSize + ratingMargin), getPaddingTop(), paint);
                } else {
                    canvas.drawBitmap(createSelectRate(rating), getPaddingLeft() + selectNum * (ratingSize + ratingMargin), getPaddingTop(), paint);
                }
            }


        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!userEnable) return super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (System.currentTimeMillis() - downTime < 100) {
                //点击事件
                float x = event.getX();
                float currentRating = (x - getPaddingLeft()) / (ratingSize + ratingMargin);
                if (currentRating != rating) {
                    if (onRatingChangeListener != null) {
                        if (isInteger) {
                            onRatingChangeListener.onChange(getIntRating(currentRating));
                        } else
                            onRatingChangeListener.onChange(currentRating);
                    }
                    rating = currentRating;
                    invalidate();
                }
            }
        }
        return true;
    }

    /**
     * 按比例缩放背景
     *
     * @return
     */
    private Bitmap getRatingBg() {
        Bitmap src = BitmapFactory.decodeResource(getResources(), ratingDrawableResId);
        Bitmap out = Bitmap.createBitmap(ratingSize, ratingSize, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(out);
        canvas.drawBitmap(src, new Rect(0, 0, src.getWidth(), src.getHeight()), new Rect(0, 0, ratingSize, ratingSize), null);
        src.recycle();
        return out;
    }

    /**
     * 生成选择的图片
     *
     * @return
     */
    private Bitmap createSelectRate(float percent) {
        Bitmap bit = getRatingBg();
        Bitmap src = Bitmap.createBitmap(ratingSize, ratingSize, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(src);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(bit, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(createBitmap(selectColor, percent), 0, 0, paint);
        bit.recycle();
        return src;
    }

    /**
     * 按照比例生成 覆盖纯色矩形
     *
     * @param color
     * @param precent
     * @return
     */
    private Bitmap createBitmap(int color, float precent) {
        Bitmap bitmap = Bitmap.createBitmap((int) (ratingSize * precent), ratingSize, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }

    /**
     * 测量控件大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratingDrawableResId != -1) {
            //计算宽度
            int totalWidth = ratingSize * ratingNum + (ratingNum - 1) * ratingMargin + getPaddingLeft() + getPaddingRight();
            //计算高度
            int totalHeight = ratingSize + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public float getRating() {
        return isInteger ? getIntRating(rating) : rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
        invalidate();
    }

    public interface OnRatingChangeListener {
        void onChange(float rating);
    }

    /**
     * float 转换成int
     *
     * @param rat
     * @return
     */
    private int getIntRating(float rat) {
        int num = (int) rat;
        if (rat - num > 0) {
            num++;
        }
        return num;
    }

}

