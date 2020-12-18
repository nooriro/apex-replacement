package com.android.cellbroadcastreceiver;

import android.content.Context;
import android.database.Cursor;
import android.telephony.SmsCbMessage;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.cellbroadcastreceiver.module.R;

public class CellBroadcastListItem extends RelativeLayout {
    private SmsCbMessage mCbMessage;
    private TextView mChannelView;
    private Context mContext;
    private TextView mDateView;
    private TextView mMessageView;

    public CellBroadcastListItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    /* access modifiers changed from: package-private */
    public SmsCbMessage getMessage() {
        return this.mCbMessage;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mChannelView = (TextView) findViewById(R.id.channel);
        this.mDateView = (TextView) findViewById(R.id.date);
        this.mMessageView = (TextView) findViewById(R.id.message);
    }

    public void bind(SmsCbMessage smsCbMessage) {
        this.mCbMessage = smsCbMessage;
        this.mChannelView.setText(CellBroadcastResources.getDialogTitleResource(this.mContext, smsCbMessage));
        this.mDateView.setText(DateUtils.formatDateTime(getContext(), smsCbMessage.getReceivedTime(), 527121));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(smsCbMessage.getMessageBody());
        Cursor query = this.mContext.getContentResolver().query(CellBroadcastContentProvider.CONTENT_URI, CellBroadcastDatabaseHelper.QUERY_COLUMNS, "date=?", new String[]{Long.toString(smsCbMessage.getReceivedTime())}, (String) null);
        if (query != null) {
            while (true) {
                try {
                    if (query.moveToNext()) {
                        if (query.getInt(query.getColumnIndexOrThrow("read")) != 0) {
                            spannableStringBuilder.setSpan(new StyleSpan(1), 0, spannableStringBuilder.length(), 33);
                            break;
                        }
                    } else {
                        break;
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
        }
        if (query != null) {
            query.close();
        }
        this.mMessageView.setText(spannableStringBuilder);
        return;
        throw th;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        accessibilityEvent.getText().add(DateUtils.formatDateTime(getContext(), this.mCbMessage.getReceivedTime(), 17));
        this.mChannelView.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        this.mMessageView.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }
}
