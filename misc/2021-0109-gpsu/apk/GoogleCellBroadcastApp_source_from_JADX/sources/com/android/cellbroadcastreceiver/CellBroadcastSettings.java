package com.android.cellbroadcastreceiver;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.MenuItem;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.cellbroadcastreceiver.CellBroadcastSettings;
import com.android.cellbroadcastreceiver.module.R;
import java.util.HashMap;
import java.util.Map;

public class CellBroadcastSettings extends Activity {
    private static final Map<Integer, Resources> sResourcesCache = new HashMap();
    private static boolean sUseResourcesForSubId = true;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (((UserManager) getSystemService("user")).hasUserRestriction("no_config_cell_broadcasts")) {
            setContentView(R.layout.cell_broadcast_disallowed_preference_screen);
        } else if (getFragmentManager().findFragmentById(16908290) == null) {
            getFragmentManager().beginTransaction().add(16908290, new CellBroadcastSettingsFragment()).commit();
        }
    }

    public void onStart() {
        super.onStart();
        getWindow().addSystemFlags(524288);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    public static void resetAllPreferences(Context context) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.remove("enable_cmas_extreme_threat_alerts").remove("enable_cmas_severe_threat_alerts").remove("enable_cmas_amber_alerts").remove("enable_public_safety_messages").remove("enable_emergency_alerts").remove("alert_reminder_interval").remove("enable_alert_speech").remove("override_dnd").remove("enable_area_update_info_alerts").remove("enable_test_alerts").remove("enable_state_local_test_alerts").remove("enable_alert_vibrate").remove("enable_cmas_presidential_alerts").remove("receive_cmas_in_second_language").remove("enable_exercise_alerts");
        if (!ActivityManager.isRunningInUserTestHarness()) {
            Log.d("CellBroadcastSettings", "In not test harness mode. reset main toggle.");
            edit.remove("enable_alerts_master_toggle");
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature("android.hardware.type.watch")) {
            edit.remove("watch_alert_reminder");
        }
        edit.commit();
        if (packageManager.hasSystemFeature("android.hardware.type.watch")) {
            PreferenceManager.setDefaultValues(context, R.xml.watch_preferences, true);
        } else {
            PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
        }
    }

    public static class CellBroadcastSettingsFragment extends PreferenceFragment {
        private Preference mAlertHistory;
        private PreferenceCategory mAlertPreferencesCategory;
        private TwoStatePreference mAlertReminder;
        private Preference mAlertsHeader;
        private TwoStatePreference mAmberCheckBox;
        private TwoStatePreference mAreaUpdateInfoCheckBox;
        /* access modifiers changed from: private */
        public boolean mDisableSevereWhenExtremeDisabled = true;
        private TwoStatePreference mEmergencyAlertsCheckBox;
        private TwoStatePreference mEnableVibrateCheckBox;
        private TwoStatePreference mExerciseTestCheckBox;
        private TwoStatePreference mExtremeCheckBox;
        private TwoStatePreference mMasterToggle;
        private TwoStatePreference mOverrideDndCheckBox;
        private TwoStatePreference mPresidentialCheckBox;
        private TwoStatePreference mPublicSafetyMessagesChannelCheckBox;
        private TwoStatePreference mReceiveCmasInSecondLanguageCheckBox;
        private ListPreference mReminderInterval;
        /* access modifiers changed from: private */
        public TwoStatePreference mSevereCheckBox;
        private TwoStatePreference mSpeechCheckBox;
        private TwoStatePreference mStateLocalTestCheckBox;
        private TwoStatePreference mTestCheckBox;
        private final BroadcastReceiver mTestingModeChangedReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (((action.hashCode() == 645814986 && action.equals("com.android.cellbroadcastreceiver.intent.ACTION_TESTING_MODE_CHANGED")) ? (char) 0 : 65535) == 0) {
                    CellBroadcastSettingsFragment.this.updatePreferenceVisibility();
                }
            }
        };

        private void initPreferences() {
            this.mExtremeCheckBox = (TwoStatePreference) findPreference("enable_cmas_extreme_threat_alerts");
            this.mSevereCheckBox = (TwoStatePreference) findPreference("enable_cmas_severe_threat_alerts");
            this.mAmberCheckBox = (TwoStatePreference) findPreference("enable_cmas_amber_alerts");
            this.mMasterToggle = (TwoStatePreference) findPreference("enable_alerts_master_toggle");
            this.mPublicSafetyMessagesChannelCheckBox = (TwoStatePreference) findPreference("enable_public_safety_messages");
            this.mEmergencyAlertsCheckBox = (TwoStatePreference) findPreference("enable_emergency_alerts");
            this.mReminderInterval = (ListPreference) findPreference("alert_reminder_interval");
            this.mSpeechCheckBox = (TwoStatePreference) findPreference("enable_alert_speech");
            this.mOverrideDndCheckBox = (TwoStatePreference) findPreference("override_dnd");
            this.mAreaUpdateInfoCheckBox = (TwoStatePreference) findPreference("enable_area_update_info_alerts");
            this.mTestCheckBox = (TwoStatePreference) findPreference("enable_test_alerts");
            this.mExerciseTestCheckBox = (TwoStatePreference) findPreference("enable_exercise_alerts");
            this.mStateLocalTestCheckBox = (TwoStatePreference) findPreference("enable_state_local_test_alerts");
            this.mAlertHistory = findPreference("emergency_alert_history");
            this.mAlertsHeader = findPreference("alerts_header");
            this.mReceiveCmasInSecondLanguageCheckBox = (TwoStatePreference) findPreference("receive_cmas_in_second_language");
            this.mEnableVibrateCheckBox = (TwoStatePreference) findPreference("enable_alert_vibrate");
            this.mPresidentialCheckBox = (TwoStatePreference) findPreference("enable_cmas_presidential_alerts");
            if (getActivity().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
                this.mAlertReminder = (TwoStatePreference) findPreference("watch_alert_reminder");
                if (Integer.valueOf(this.mReminderInterval.getValue()).intValue() == 0) {
                    this.mAlertReminder.setChecked(false);
                } else {
                    this.mAlertReminder.setChecked(true);
                }
                this.mAlertReminder.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return CellBroadcastSettings.CellBroadcastSettingsFragment.this.mo5439xb5183b23(preference, obj);
                    }
                });
                ((PreferenceScreen) findPreference("category_alert_preferences")).removePreference(this.mReminderInterval);
                return;
            }
            this.mAlertPreferencesCategory = (PreferenceCategory) findPreference("category_alert_preferences");
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("category_emergency_alerts");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initPreferences$0 */
        public /* synthetic */ boolean mo5439xb5183b23(Preference preference, Object obj) {
            try {
                this.mReminderInterval.setValueIndex(((Boolean) obj).booleanValue() ? 1 : 3);
            } catch (IndexOutOfBoundsException unused) {
                this.mReminderInterval.setValue(String.valueOf(0));
                Log.w("CellBroadcastSettings", "Setting default value");
            }
            return true;
        }

        public void onCreatePreferences(Bundle bundle, String str) {
            TwoStatePreference twoStatePreference;
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mTestingModeChangedReceiver, new IntentFilter("com.android.cellbroadcastreceiver.intent.ACTION_TESTING_MODE_CHANGED"));
            if (getActivity().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
                addPreferencesFromResource(R.xml.watch_preferences);
            } else {
                addPreferencesFromResource(R.xml.preferences);
            }
            initPreferences();
            Resources resourcesForDefaultSubId = CellBroadcastSettings.getResourcesForDefaultSubId(getContext());
            this.mDisableSevereWhenExtremeDisabled = resourcesForDefaultSubId.getBoolean(R.bool.disable_severe_when_extreme_disabled);
            C02952 r5 = new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    CellBroadcastReceiver.startConfigService(preference.getContext(), CellBroadcastConfigService.ACTION_ENABLE_CHANNELS);
                    if (CellBroadcastSettingsFragment.this.mDisableSevereWhenExtremeDisabled && preference.getKey().equals("enable_cmas_extreme_threat_alerts")) {
                        boolean booleanValue = ((Boolean) obj).booleanValue();
                        if (CellBroadcastSettingsFragment.this.mSevereCheckBox != null) {
                            CellBroadcastSettingsFragment.this.mSevereCheckBox.setEnabled(booleanValue);
                            CellBroadcastSettingsFragment.this.mSevereCheckBox.setChecked(false);
                        }
                    }
                    if (preference.getKey().equals("enable_alerts_master_toggle")) {
                        CellBroadcastSettingsFragment.this.setAlertsEnabled(((Boolean) obj).booleanValue());
                    }
                    if (preference.getKey().equals("enable_area_update_info_alerts")) {
                        boolean booleanValue2 = ((Boolean) obj).booleanValue();
                        Intent intent = new Intent("com.android.cellbroadcastreceiver.action.AREA_UPDATE_INFO_ENABLED");
                        intent.putExtra("enable", booleanValue2);
                        CellBroadcastSettingsFragment.this.getContext().sendBroadcast(intent, "com.android.cellbroadcastservice.FULL_ACCESS_CELL_BROADCAST_HISTORY");
                    }
                    new BackupManager(CellBroadcastSettingsFragment.this.getContext()).dataChanged();
                    return true;
                }
            };
            initReminderIntervalList();
            TwoStatePreference twoStatePreference2 = this.mMasterToggle;
            if (twoStatePreference2 != null) {
                twoStatePreference2.setOnPreferenceChangeListener(r5);
                if (!this.mMasterToggle.isChecked()) {
                    setAlertsEnabled(false);
                }
            }
            TwoStatePreference twoStatePreference3 = this.mAreaUpdateInfoCheckBox;
            if (twoStatePreference3 != null) {
                twoStatePreference3.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference4 = this.mExtremeCheckBox;
            if (twoStatePreference4 != null) {
                twoStatePreference4.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference5 = this.mPublicSafetyMessagesChannelCheckBox;
            if (twoStatePreference5 != null) {
                twoStatePreference5.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference6 = this.mEmergencyAlertsCheckBox;
            if (twoStatePreference6 != null) {
                twoStatePreference6.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference7 = this.mSevereCheckBox;
            if (twoStatePreference7 != null) {
                twoStatePreference7.setOnPreferenceChangeListener(r5);
                if (this.mDisableSevereWhenExtremeDisabled && (twoStatePreference = this.mExtremeCheckBox) != null) {
                    this.mSevereCheckBox.setEnabled(twoStatePreference.isChecked());
                }
            }
            TwoStatePreference twoStatePreference8 = this.mAmberCheckBox;
            if (twoStatePreference8 != null) {
                twoStatePreference8.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference9 = this.mTestCheckBox;
            if (twoStatePreference9 != null) {
                twoStatePreference9.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference10 = this.mExerciseTestCheckBox;
            if (twoStatePreference10 != null) {
                twoStatePreference10.setOnPreferenceChangeListener(r5);
            }
            TwoStatePreference twoStatePreference11 = this.mStateLocalTestCheckBox;
            if (twoStatePreference11 != null) {
                twoStatePreference11.setOnPreferenceChangeListener(r5);
            }
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (this.mOverrideDndCheckBox != null) {
                if (!defaultSharedPreferences.getBoolean("override_dnd_settings_changed", false)) {
                    this.mOverrideDndCheckBox.setChecked(resourcesForDefaultSubId.getBoolean(R.bool.override_dnd_default));
                }
                this.mOverrideDndCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(defaultSharedPreferences) {
                    public final /* synthetic */ SharedPreferences f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return CellBroadcastSettings.CellBroadcastSettingsFragment.this.mo5440x5e9addd9(this.f$1, preference, obj);
                    }
                });
            }
            Preference preference = this.mAlertHistory;
            if (preference != null) {
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public final boolean onPreferenceClick(Preference preference) {
                        return CellBroadcastSettings.CellBroadcastSettingsFragment.this.mo5441xebd58f5a(preference);
                    }
                });
            }
            updateVibrationPreference(defaultSharedPreferences.getBoolean("override_dnd", false));
            updatePreferenceVisibility();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreatePreferences$1 */
        public /* synthetic */ boolean mo5440x5e9addd9(SharedPreferences sharedPreferences, Preference preference, Object obj) {
            sharedPreferences.edit().putBoolean("override_dnd_settings_changed", true).apply();
            updateVibrationPreference(((Boolean) obj).booleanValue());
            return true;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreatePreferences$2 */
        public /* synthetic */ boolean mo5441xebd58f5a(Preference preference) {
            startActivity(new Intent(getContext(), CellBroadcastListActivity.class));
            return true;
        }

        private void updateVibrationPreference(boolean z) {
            TwoStatePreference twoStatePreference = this.mEnableVibrateCheckBox;
            if (twoStatePreference != null) {
                if (z) {
                    twoStatePreference.setChecked(true);
                }
                this.mEnableVibrateCheckBox.setEnabled(!z);
            }
        }

        /* access modifiers changed from: private */
        public void updatePreferenceVisibility() {
            Resources resourcesForDefaultSubId = CellBroadcastSettings.getResourcesForDefaultSubId(getContext());
            CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(getContext(), Integer.MAX_VALUE);
            TwoStatePreference twoStatePreference = this.mMasterToggle;
            if (twoStatePreference != null) {
                twoStatePreference.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_main_switch_settings));
            }
            TwoStatePreference twoStatePreference2 = this.mPresidentialCheckBox;
            if (twoStatePreference2 != null) {
                twoStatePreference2.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_presidential_alerts_settings));
            }
            TwoStatePreference twoStatePreference3 = this.mExtremeCheckBox;
            boolean z = false;
            if (twoStatePreference3 != null) {
                twoStatePreference3.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_extreme_alert_settings) && !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_alert_extreme_channels_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference4 = this.mSevereCheckBox;
            if (twoStatePreference4 != null) {
                twoStatePreference4.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_severe_alert_settings) && !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_alerts_severe_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference5 = this.mAmberCheckBox;
            if (twoStatePreference5 != null) {
                twoStatePreference5.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_amber_alert_settings) && !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_amber_alerts_channels_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference6 = this.mPublicSafetyMessagesChannelCheckBox;
            if (twoStatePreference6 != null) {
                twoStatePreference6.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_public_safety_settings) && !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.public_safety_messages_channels_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference7 = this.mTestCheckBox;
            if (twoStatePreference7 != null) {
                twoStatePreference7.setVisible(CellBroadcastSettings.isTestAlertsToggleVisible(getContext()));
            }
            if (this.mExerciseTestCheckBox != null) {
                this.mExerciseTestCheckBox.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_separate_exercise_settings) && (resourcesForDefaultSubId.getBoolean(R.bool.show_exercise_settings) || CellBroadcastReceiver.isTestingMode(getContext())) && !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.exercise_alert_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference8 = this.mEmergencyAlertsCheckBox;
            if (twoStatePreference8 != null) {
                twoStatePreference8.setVisible(!cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.emergency_alerts_channels_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference9 = this.mStateLocalTestCheckBox;
            if (twoStatePreference9 != null) {
                twoStatePreference9.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_state_local_test_settings) && !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.state_local_test_alert_range_strings).isEmpty());
            }
            TwoStatePreference twoStatePreference10 = this.mReceiveCmasInSecondLanguageCheckBox;
            if (twoStatePreference10 != null) {
                twoStatePreference10.setVisible(!resourcesForDefaultSubId.getString(R.string.emergency_alert_second_language_code).isEmpty());
            }
            TwoStatePreference twoStatePreference11 = this.mAreaUpdateInfoCheckBox;
            if (twoStatePreference11 != null) {
                twoStatePreference11.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.config_showAreaUpdateInfoSettings));
            }
            TwoStatePreference twoStatePreference12 = this.mOverrideDndCheckBox;
            if (twoStatePreference12 != null) {
                twoStatePreference12.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_override_dnd_settings));
            }
            TwoStatePreference twoStatePreference13 = this.mEnableVibrateCheckBox;
            if (twoStatePreference13 != null) {
                twoStatePreference13.setVisible(resourcesForDefaultSubId.getBoolean(R.bool.show_override_dnd_settings) || !resourcesForDefaultSubId.getBoolean(R.bool.override_dnd));
            }
            Preference preference = this.mAlertsHeader;
            if (preference != null) {
                preference.setVisible(!getContext().getString(R.string.alerts_header_summary).isEmpty());
            }
            TwoStatePreference twoStatePreference14 = this.mSpeechCheckBox;
            if (twoStatePreference14 != null) {
                if (resourcesForDefaultSubId.getBoolean(R.bool.show_alert_speech_setting) || getActivity().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
                    z = true;
                }
                twoStatePreference14.setVisible(z);
            }
        }

        private void initReminderIntervalList() {
            Resources resourcesForDefaultSubId = CellBroadcastSettings.getResourcesForDefaultSubId(getContext());
            String[] stringArray = resourcesForDefaultSubId.getStringArray(R.array.alert_reminder_interval_active_values);
            String[] stringArray2 = resourcesForDefaultSubId.getStringArray(R.array.alert_reminder_interval_entries);
            String[] strArr = new String[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                int findIndexOfValue = this.mReminderInterval.findIndexOfValue(stringArray[i]);
                if (findIndexOfValue != -1) {
                    strArr[i] = stringArray2[findIndexOfValue];
                } else {
                    Log.e("CellBroadcastSettings", "Can't find " + stringArray[i]);
                }
            }
            this.mReminderInterval.setEntries(strArr);
            this.mReminderInterval.setEntryValues(stringArray);
            ListPreference listPreference = this.mReminderInterval;
            listPreference.setSummary(listPreference.getEntry());
            this.mReminderInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(this) {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });
        }

        /* access modifiers changed from: private */
        public void setAlertsEnabled(boolean z) {
            TwoStatePreference twoStatePreference = this.mSevereCheckBox;
            if (twoStatePreference != null) {
                twoStatePreference.setEnabled(z);
                this.mSevereCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference2 = this.mExtremeCheckBox;
            if (twoStatePreference2 != null) {
                twoStatePreference2.setEnabled(z);
                this.mExtremeCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference3 = this.mAmberCheckBox;
            if (twoStatePreference3 != null) {
                twoStatePreference3.setEnabled(z);
                this.mAmberCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference4 = this.mAreaUpdateInfoCheckBox;
            if (twoStatePreference4 != null) {
                twoStatePreference4.setEnabled(z);
                this.mAreaUpdateInfoCheckBox.setChecked(z);
            }
            PreferenceCategory preferenceCategory = this.mAlertPreferencesCategory;
            if (preferenceCategory != null) {
                preferenceCategory.setEnabled(z);
            }
            TwoStatePreference twoStatePreference5 = this.mEmergencyAlertsCheckBox;
            if (twoStatePreference5 != null) {
                twoStatePreference5.setEnabled(z);
                this.mEmergencyAlertsCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference6 = this.mPublicSafetyMessagesChannelCheckBox;
            if (twoStatePreference6 != null) {
                twoStatePreference6.setEnabled(z);
                this.mPublicSafetyMessagesChannelCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference7 = this.mStateLocalTestCheckBox;
            if (twoStatePreference7 != null) {
                twoStatePreference7.setEnabled(z);
                this.mStateLocalTestCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference8 = this.mTestCheckBox;
            if (twoStatePreference8 != null) {
                twoStatePreference8.setEnabled(z);
                this.mTestCheckBox.setChecked(z);
            }
            TwoStatePreference twoStatePreference9 = this.mExerciseTestCheckBox;
            if (twoStatePreference9 != null) {
                twoStatePreference9.setEnabled(z);
                this.mExerciseTestCheckBox.setChecked(z);
            }
        }

        public void onResume() {
            super.onResume();
            updatePreferenceVisibility();
        }

        public void onDestroy() {
            super.onDestroy();
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mTestingModeChangedReceiver);
        }
    }

    public static boolean isTestAlertsToggleVisible(Context context) {
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(context, Integer.MAX_VALUE);
        Resources resourcesForDefaultSubId = getResourcesForDefaultSubId(context);
        boolean z = !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.required_monthly_test_range_strings).isEmpty() || (!cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.exercise_alert_range_strings).isEmpty() && !resourcesForDefaultSubId.getBoolean(R.bool.show_separate_exercise_settings)) || !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.operator_defined_alert_range_strings).isEmpty() || !cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.etws_test_alerts_range_strings).isEmpty();
        if ((resourcesForDefaultSubId.getBoolean(R.bool.show_test_settings) || CellBroadcastReceiver.isTestingMode(context)) && z) {
            return true;
        }
        return false;
    }

    public static void setUseResourcesForSubId(boolean z) {
        sUseResourcesForSubId = z;
    }

    public static Resources getResources(Context context, int i) {
        if (i == Integer.MAX_VALUE || !SubscriptionManager.isValidSubscriptionId(i) || !sUseResourcesForSubId) {
            return context.getResources();
        }
        if (sResourcesCache.containsKey(Integer.valueOf(i))) {
            return sResourcesCache.get(Integer.valueOf(i));
        }
        Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(context, i);
        sResourcesCache.put(Integer.valueOf(i), resourcesForSubId);
        return resourcesForSubId;
    }

    public static Resources getResourcesForDefaultSubId(Context context) {
        return getResources(context, SubscriptionManager.getDefaultSubscriptionId());
    }
}
