package com.roto.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.roto.base.R;
/**
 * Created by hardy on 16/9/22.
 */

public class TableView extends LinearLayout implements View.OnClickListener {

    private OnItemClickListener mClickListener;
    private BaseAdapter adapter;
    private Drawable divider = getResources().getDrawable(R.drawable.gray_horizontal_line);
    private Drawable dividerOfGroupEnd;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1)
                return;
            if (adapter == null || adapter.isEmpty()) {
                removeAllViews();
                return;
            }
            removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, TableView.this);
                addView(view);
            }
        }
    };

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TableView);
        divider = array.getDrawable(R.styleable.TableView_dividernova);
        dividerOfGroupEnd = array.getDrawable(R.styleable.TableView_group_divider);
        array.recycle();

        if (divider == null) {
            divider = getResources().getDrawable(R.drawable.gray_horizontal_line);
        }
        if (dividerOfGroupEnd == null) {
            dividerOfGroupEnd = getResources().getDrawable(R.drawable.gray_horizontal_line);
        }
        setOrientation(VERTICAL);
    }

    public Drawable getDivider() {
        return divider;
    }

    public void setDivider(Drawable divider) {
        if (divider == this.divider) {
            return;
        }
        this.divider = divider;
        requestLayout();
    }

    public void setDividerOfGroupEnd(Drawable divider) {
        this.dividerOfGroupEnd = divider;
        requestLayout();
    }

    public void setDividerOfGroupEnd(int resid) {
        if (resid > 0) {
            this.dividerOfGroupEnd = getResources().getDrawable(resid);
            requestLayout();
        }
    }

    @Override
    public void childDrawableStateChanged(View child) {
        super.childDrawableStateChanged(child);
        if (divider != null) {
            invalidate(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (divider != null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() == View.VISIBLE && !(child instanceof TableHeader)
                        && child.getHeight() > 0) {
                    if (isGroupEnd(i)) {
                        drawDividerOfGroupEnd(canvas, child);
                    } else {
                        drawDivider(canvas, child);
                    }
                }
            }
        }
    }

    private boolean isGroupEnd(int pos) {
        for (int i = pos + 1; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != null && v.getVisibility() == VISIBLE) {
                return v instanceof TableHeader;
            }
        }
        return true;
    }

    private void drawDivider(Canvas canvas, View view) {
        if (divider != null) {
            int[] state = view.getDrawableState();
            divider.setState(state);
            int height = divider.getCurrent().getIntrinsicHeight();
            if (height <= 0) {
                return;
            }
            final Rect bounds = new Rect();
            bounds.left = getPaddingLeft() + getItemPadding();
            bounds.top = view.getBottom() - height;
            bounds.right = getRight() - getLeft() - getPaddingRight();
            bounds.bottom = view.getBottom();
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    private int getItemPadding() {
        return itemPadding;
    }

    private int itemPadding = getResources().getDimensionPixelOffset(R.dimen.table_item_padding);

    public void setItemPadding(int padding) {
        itemPadding = padding;
    }

    private void drawDividerOfGroupEnd(Canvas canvas, View view) {
        Drawable divider = this.dividerOfGroupEnd == null ? this.divider : this.dividerOfGroupEnd;
        if (divider != null) {
            int[] state = view.getDrawableState();
            divider.setState(state);
            int height = divider.getCurrent().getIntrinsicHeight();
            if (height <= 0) {
                return;
            }
            final Rect bounds = new Rect();
            bounds.left = getPaddingLeft();
            bounds.top = view.getBottom() - height;
            bounds.right = getRight() - getLeft() - getPaddingRight();
            bounds.bottom = view.getBottom();
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 100);
        }

        @Override
        public void onInvalidated() {
            onChanged();
        }
    };

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        if (this.adapter != null)
            this.adapter.unregisterDataSetObserver(observer);
        this.adapter = adapter;
        if (this.adapter != null)
            this.adapter.registerDataSetObserver(observer);
        removeAllViews();
        observer.onChanged();
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            int position = -1;
            long id = -1;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (v == view) {
                    position = i;
                    break;
                }
            }
            if (position < 0)
                return;
            if (adapter != null)
                id = adapter.getItemId(position);
            if (id == -1) {
                id = v.getId();
            }
            mClickListener.onItemClick(this, v, position, id);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TableView parent, View view, int position, long id);
    }

    public Object getItemAtPosition(int position) {
        return (adapter == null || position < 0) ? null : adapter.getItem(position);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() == View.VISIBLE && !(child instanceof AdapterView)) {
                if (mClickListener != null) {
                    boolean isClickable = child.isClickable();
                    child.setOnClickListener(this);
                    if (!isClickable)
                        child.setClickable(false);
                }
            }
        }
    }

}