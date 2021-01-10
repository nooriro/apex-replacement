package com.android.cellbroadcastreceiver;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import android.text.TextUtils;
import com.android.cellbroadcastreceiver.module.R;
import java.util.ArrayList;

public class CellBroadcastSearchIndexableProvider extends SearchIndexablesProvider {
    public static final int[] INDEXABLE_KEYWORDS_RESOURCES = {R.string.etws_earthquake_warning, R.string.etws_tsunami_warning, R.string.cmas_presidential_level_alert, R.string.cmas_required_monthly_test, R.string.emergency_alerts_title};
    public static final SearchIndexableResource[] INDEXABLE_RES = {new SearchIndexableResource(1, R.xml.preferences, CellBroadcastSettings.class.getName(), R.mipmap.ic_launcher_cell_broadcast)};

    public boolean onCreate() {
        return true;
    }

    public Context getContextMethod() {
        return CellBroadcastSearchIndexableProvider.super.getContext();
    }

    public Resources getResourcesMethod() {
        return CellBroadcastSettings.getResourcesForDefaultSubId(getContextMethod());
    }

    public boolean isTestAlertsToggleVisible() {
        return CellBroadcastSettings.isTestAlertsToggleVisible(getContextMethod());
    }

    public Cursor queryXmlResources(String[] strArr) {
        if (isAutomotive()) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
        int length = INDEXABLE_RES.length;
        for (int i = 0; i < length; i++) {
            matrixCursor.addRow(new Object[]{Integer.valueOf(INDEXABLE_RES[i].rank), Integer.valueOf(INDEXABLE_RES[i].xmlResId), null, Integer.valueOf(INDEXABLE_RES[i].iconResId), "android.intent.action.MAIN", getContextMethod().getPackageName(), INDEXABLE_RES[i].className});
        }
        return matrixCursor;
    }

    public Cursor queryRawData(String[] strArr) {
        if (isAutomotive()) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        Resources resourcesMethod = getResourcesMethod();
        Object[] objArr = new Object[SearchIndexablesContract.INDEXABLES_RAW_COLUMNS.length];
        objArr[1] = resourcesMethod.getString(R.string.sms_cb_settings);
        ArrayList arrayList = new ArrayList();
        for (int string : INDEXABLE_KEYWORDS_RESOURCES) {
            arrayList.add(resourcesMethod.getString(string));
        }
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(getContextMethod(), Integer.MAX_VALUE);
        if (!cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.public_safety_messages_channels_range_strings).isEmpty()) {
            arrayList.add(resourcesMethod.getString(R.string.public_safety_message));
        }
        if (!cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.state_local_test_alert_range_strings).isEmpty()) {
            arrayList.add(resourcesMethod.getString(R.string.state_local_test_alert));
        }
        objArr[5] = TextUtils.join(",", arrayList);
        objArr[6] = resourcesMethod.getString(R.string.sms_cb_settings);
        objArr[12] = CellBroadcastSettings.class.getSimpleName();
        objArr[9] = "android.intent.action.MAIN";
        objArr[10] = getContextMethod().getPackageName();
        objArr[11] = CellBroadcastSettings.class.getName();
        matrixCursor.addRow(objArr);
        return matrixCursor;
    }

    public Cursor queryNonIndexableKeys(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
        Resources resourcesMethod = getResourcesMethod();
        if (!resourcesMethod.getBoolean(R.bool.show_presidential_alerts_settings)) {
            matrixCursor.addRow(new Object[]{"enable_cmas_presidential_alerts"});
        }
        if (!CellBroadcastSettings.getResources(getContextMethod(), Integer.MAX_VALUE).getBoolean(R.bool.show_alert_speech_setting)) {
            matrixCursor.addRow(new Object[]{"enable_alert_speech"});
        }
        if (!resourcesMethod.getBoolean(R.bool.show_extreme_alert_settings)) {
            matrixCursor.addRow(new Object[]{"enable_cmas_extreme_threat_alerts"});
        }
        if (!resourcesMethod.getBoolean(R.bool.show_severe_alert_settings)) {
            matrixCursor.addRow(new Object[]{"enable_cmas_severe_threat_alerts"});
        }
        if (!resourcesMethod.getBoolean(R.bool.show_amber_alert_settings)) {
            matrixCursor.addRow(new Object[]{"enable_cmas_amber_alerts"});
        }
        if (!resourcesMethod.getBoolean(R.bool.config_showAreaUpdateInfoSettings)) {
            matrixCursor.addRow(new Object[]{"enable_area_update_info_alerts"});
        }
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(getContextMethod(), Integer.MAX_VALUE);
        if (cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_amber_alerts_channels_range_strings).isEmpty()) {
            matrixCursor.addRow(new Object[]{"enable_cmas_amber_alerts"});
        }
        if (cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.emergency_alerts_channels_range_strings).isEmpty()) {
            matrixCursor.addRow(new Object[]{"enable_emergency_alerts"});
        }
        if (cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.public_safety_messages_channels_range_strings).isEmpty()) {
            matrixCursor.addRow(new Object[]{"enable_public_safety_messages"});
        }
        if (cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.state_local_test_alert_range_strings).isEmpty()) {
            matrixCursor.addRow(new Object[]{"enable_state_local_test_alerts"});
        }
        boolean isTestAlertsToggleVisible = isTestAlertsToggleVisible();
        if (resourcesMethod.getString(R.string.emergency_alert_second_language_code).isEmpty()) {
            matrixCursor.addRow(new Object[]{"receive_cmas_in_second_language"});
        }
        return matrixCursor;
    }

    public boolean isAutomotive() {
        return getContextMethod().getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }
}
