package com.android.cellbroadcastreceiver;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsCbMessage;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.cellbroadcastreceiver.CellBroadcastContentProvider;
import com.android.cellbroadcastreceiver.CellBroadcastListActivity;
import com.android.cellbroadcastreceiver.module.R;
import java.util.ArrayList;

public class CellBroadcastListActivity extends Activity {
    public CursorLoaderListFragment mListFragment;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.cb_list_activity_title));
        ((NotificationManager) getSystemService("notification")).cancel(1);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentById(16908290) == null) {
            this.mListFragment = new CursorLoaderListFragment();
            fragmentManager.beginTransaction().add(16908290, this.mListFragment).commit();
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

    public static class CursorLoaderListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
        public static final Uri CONTENT_URI = Uri.parse("content://cellbroadcasts");
        public static final String KEY_LOADER_ID = "loader_id";
        public static final int LOADER_HISTORY_FROM_CBS = 2;
        public static final int MENU_DELETE = 0;
        public static final int MENU_DELETE_ALL = 3;
        public static final int MENU_SHOW_ALL_MESSAGES = 5;
        public static final int MENU_SHOW_REGULAR_MESSAGES = 4;
        public static final int MENU_VIEW_DETAILS = 1;
        public static final String[] QUERY_COLUMNS = {"_id", "slot_index", "sub_id", "geo_scope", "plmn", "lac", "cid", "serial_number", "service_category", "language", "dcs", "body", "format", "priority", "etws_warning_type", "cmas_message_class", "cmas_category", "cmas_response_type", "cmas_severity", "cmas_urgency", "cmas_certainty", "received_time", "location_check_time", "message_broadcasted", "message_displayed", "geometries", "maximum_wait_time"};
        private static final String TAG = CellBroadcastListActivity.class.getSimpleName();
        public CursorAdapter mAdapter;
        private int mCurrentLoaderId = 0;
        private final View.OnCreateContextMenuListener mOnCreateContextMenuListener = new View.OnCreateContextMenuListener() {
            public final void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                CellBroadcastListActivity.CursorLoaderListFragment.this.lambda$new$0$CellBroadcastListActivity$CursorLoaderListFragment(contextMenu, view, contextMenuInfo);
            }
        };

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setHasOptionsMenu(true);
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(R.layout.cell_broadcast_list_screen, viewGroup, false);
        }

        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            getListView().setOnCreateContextMenuListener(this.mOnCreateContextMenuListener);
            CellBroadcastCursorAdapter cellBroadcastCursorAdapter = new CellBroadcastCursorAdapter(getActivity());
            this.mAdapter = cellBroadcastCursorAdapter;
            setListAdapter(cellBroadcastCursorAdapter);
            this.mCurrentLoaderId = 1;
            if (bundle != null && bundle.containsKey(KEY_LOADER_ID)) {
                this.mCurrentLoaderId = bundle.getInt(KEY_LOADER_ID);
            }
            String str = TAG;
            Log.d(str, "onActivityCreated: id=" + this.mCurrentLoaderId);
            getLoaderManager().initLoader(this.mCurrentLoaderId, (Bundle) null, this);
        }

        public void onSaveInstanceState(Bundle bundle) {
            String str = TAG;
            Log.d(str, "onSaveInstanceState: id=" + this.mCurrentLoaderId);
            bundle.putInt(KEY_LOADER_ID, this.mCurrentLoaderId);
        }

        public void onResume() {
            super.onResume();
            Log.d(TAG, "onResume");
            if (this.mCurrentLoaderId != 0) {
                getLoaderManager().restartLoader(this.mCurrentLoaderId, (Bundle) null, this);
            }
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            menu.add(0, 3, 0, R.string.menu_delete_all).setIcon(17301564);
            menu.add(0, 5, 0, R.string.show_all_messages);
            menu.add(0, 4, 0, R.string.show_regular_messages);
        }

        public void onPrepareOptionsMenu(Menu menu) {
            boolean isTestingMode = CellBroadcastReceiver.isTestingMode(getContext());
            boolean z = false;
            menu.findItem(3).setVisible(!this.mAdapter.isEmpty() && !isTestingMode);
            menu.findItem(5).setVisible(isTestingMode && this.mCurrentLoaderId == 1);
            MenuItem findItem = menu.findItem(4);
            if (isTestingMode && this.mCurrentLoaderId == 2) {
                z = true;
            }
            findItem.setVisible(z);
        }

        public void onListItemClick(ListView listView, View view, int i, long j) {
            showDialogAndMarkRead(((CellBroadcastListItem) view).getMessage());
        }

        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String str = TAG;
            this.mCurrentLoaderId = i;
            if (i == 1) {
                Log.d(str, "onCreateLoader: normal history.");
                return new CursorLoader(getActivity(), CellBroadcastContentProvider.CONTENT_URI, CellBroadcastDatabaseHelper.QUERY_COLUMNS, (String) null, (String[]) null, "date DESC");
            } else if (i != 2) {
                return null;
            } else {
                Log.d(str, "onCreateLoader: history from cell broadcast service");
                return new CursorLoader(getActivity(), CONTENT_URI, QUERY_COLUMNS, (String) null, (String[]) null, "received_time DESC");
            }
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "onLoadFinished");
            this.mAdapter.swapCursor(cursor);
            getActivity().invalidateOptionsMenu();
            updateNoAlertTextVisibility();
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            Log.d(TAG, "onLoaderReset");
            this.mAdapter.swapCursor((Cursor) null);
        }

        private void showDialogAndMarkRead(SmsCbMessage smsCbMessage) {
            Intent intent = new Intent(getActivity(), CellBroadcastAlertDialog.class);
            ArrayList arrayList = new ArrayList();
            arrayList.add(smsCbMessage);
            intent.putParcelableArrayListExtra("com.android.cellbroadcastreceiver.SMS_CB_MESSAGE", arrayList);
            startActivity(intent);
        }

        private void showBroadcastDetails(SmsCbMessage smsCbMessage, long j, boolean z, String str) {
            new AlertDialog.Builder(getActivity()).setTitle(this.mCurrentLoaderId == 1 ? R.string.view_details_title : R.string.view_details_debugging_title).setMessage(CellBroadcastResources.getMessageDetails(getActivity(), this.mCurrentLoaderId == 2, smsCbMessage, j, z, str)).setCancelable(true).show();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$CellBroadcastListActivity$CursorLoaderListFragment(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(R.string.message_options);
            contextMenu.add(0, 1, 0, R.string.menu_view_details);
            if (this.mCurrentLoaderId == 1) {
                contextMenu.add(0, 0, 0, R.string.menu_delete);
            }
        }

        private void updateNoAlertTextVisibility() {
            TextView textView = (TextView) getActivity().findViewById(R.id.empty);
            if (textView != null) {
                textView.setVisibility(!hasAlertsInHistory() ? 0 : 4);
            }
        }

        private boolean hasAlertsInHistory() {
            return this.mAdapter.getCursor().getCount() > 0;
        }

        private long getLocationCheckTime(Cursor cursor) {
            if (this.mCurrentLoaderId != 2) {
                return -1;
            }
            return cursor.getLong(cursor.getColumnIndex("location_check_time"));
        }

        private boolean wasMessageDisplayed(Cursor cursor) {
            if (this.mCurrentLoaderId == 2 && cursor.getInt(cursor.getColumnIndex("message_displayed")) == 0) {
                return false;
            }
            return true;
        }

        private String getGeometryString(Cursor cursor) {
            if (this.mCurrentLoaderId == 2 && cursor.getColumnIndex("geometries") >= 0) {
                return cursor.getString(cursor.getColumnIndex("geometries"));
            }
            return null;
        }

        public boolean onContextItemSelected(MenuItem menuItem) {
            Cursor cursor = this.mAdapter.getCursor();
            if (cursor != null && cursor.getPosition() >= 0) {
                int itemId = menuItem.getItemId();
                if (itemId == 0) {
                    confirmDeleteThread(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                } else if (itemId == 1) {
                    showBroadcastDetails(CellBroadcastCursorAdapter.createFromCursor(getContext(), cursor), getLocationCheckTime(cursor), wasMessageDisplayed(cursor), getGeometryString(cursor));
                }
            }
            return super.onContextItemSelected(menuItem);
        }

        public boolean onOptionsItemSelected(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == 3) {
                confirmDeleteThread(-1);
                return false;
            } else if (itemId == 4) {
                getLoaderManager().restartLoader(1, (Bundle) null, this);
                return false;
            } else if (itemId != 5) {
                return true;
            } else {
                getLoaderManager().restartLoader(2, (Bundle) null, this);
                return false;
            }
        }

        public void confirmDeleteThread(long j) {
            confirmDeleteThreadDialog(new DeleteThreadListener(j), j == -1, getActivity());
        }

        public static void confirmDeleteThreadDialog(DeleteThreadListener deleteThreadListener, boolean z, Context context) {
            new AlertDialog.Builder(context).setIconAttribute(16843605).setCancelable(true).setPositiveButton(R.string.button_delete, deleteThreadListener).setNegativeButton(R.string.button_cancel, (DialogInterface.OnClickListener) null).setMessage(z ? R.string.confirm_delete_all_broadcasts : R.string.confirm_delete_broadcast).show();
        }

        public class DeleteThreadListener implements DialogInterface.OnClickListener {
            private final long mRowId;

            public DeleteThreadListener(long j) {
                this.mRowId = j;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                new CellBroadcastContentProvider.AsyncCellBroadcastTask(CursorLoaderListFragment.this.getActivity().getContentResolver()).execute(new CellBroadcastContentProvider.CellBroadcastOperation[]{
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001a: INVOKE  
                      (wrap: com.android.cellbroadcastreceiver.CellBroadcastContentProvider$AsyncCellBroadcastTask : 0x000c: CONSTRUCTOR  (r4v1 com.android.cellbroadcastreceiver.CellBroadcastContentProvider$AsyncCellBroadcastTask) = 
                      (wrap: android.content.ContentResolver : 0x0008: INVOKE  (r0v2 android.content.ContentResolver) = 
                      (wrap: android.app.Activity : 0x0004: INVOKE  (r0v1 android.app.Activity) = 
                      (wrap: com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment : 0x0002: IGET  (r0v0 com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment) = 
                      (r2v0 'this' com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener A[THIS])
                     com.android.cellbroadcastreceiver.CellBroadcastListActivity.CursorLoaderListFragment.DeleteThreadListener.this$0 com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment)
                     android.app.ListFragment.getActivity():android.app.Activity type: VIRTUAL)
                     android.app.Activity.getContentResolver():android.content.ContentResolver type: VIRTUAL)
                     call: com.android.cellbroadcastreceiver.CellBroadcastContentProvider.AsyncCellBroadcastTask.<init>(android.content.ContentResolver):void type: CONSTRUCTOR)
                      (wrap: com.android.cellbroadcastreceiver.CellBroadcastContentProvider$CellBroadcastOperation[] : ?: FILLED_NEW_ARRAY  (r0v4 com.android.cellbroadcastreceiver.CellBroadcastContentProvider$CellBroadcastOperation[]) = 
                      (wrap: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw : 0x0014: CONSTRUCTOR  (r1v0 com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw) = 
                      (r2v0 'this' com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener A[THIS])
                     call: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw.<init>(com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener):void type: CONSTRUCTOR)
                     elemType: com.android.cellbroadcastreceiver.CellBroadcastContentProvider$CellBroadcastOperation)
                     android.os.AsyncTask.execute(java.lang.Object[]):android.os.AsyncTask type: VIRTUAL in method: com.android.cellbroadcastreceiver.CellBroadcastListActivity.CursorLoaderListFragment.DeleteThreadListener.onClick(android.content.DialogInterface, int):void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
                    	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:485)
                    	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:497)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
                    	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:485)
                    	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:497)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
                    	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:485)
                    	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:497)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: ?: FILLED_NEW_ARRAY  (r0v4 com.android.cellbroadcastreceiver.CellBroadcastContentProvider$CellBroadcastOperation[]) = 
                      (wrap: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw : 0x0014: CONSTRUCTOR  (r1v0 com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw) = 
                      (r2v0 'this' com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener A[THIS])
                     call: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw.<init>(com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener):void type: CONSTRUCTOR)
                     elemType: com.android.cellbroadcastreceiver.CellBroadcastContentProvider$CellBroadcastOperation in method: com.android.cellbroadcastreceiver.CellBroadcastListActivity.CursorLoaderListFragment.DeleteThreadListener.onClick(android.content.DialogInterface, int):void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0014: CONSTRUCTOR  (r1v0 com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw) = 
                      (r2v0 'this' com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener A[THIS])
                     call: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw.<init>(com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener):void type: CONSTRUCTOR in method: com.android.cellbroadcastreceiver.CellBroadcastListActivity.CursorLoaderListFragment.DeleteThreadListener.onClick(android.content.DialogInterface, int):void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.filledNewArray(InsnGen.java:594)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:391)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 70 more
                    */
                /*
                    this = this;
                    com.android.cellbroadcastreceiver.CellBroadcastContentProvider$AsyncCellBroadcastTask r4 = new com.android.cellbroadcastreceiver.CellBroadcastContentProvider$AsyncCellBroadcastTask
                    com.android.cellbroadcastreceiver.CellBroadcastListActivity$CursorLoaderListFragment r0 = com.android.cellbroadcastreceiver.CellBroadcastListActivity.CursorLoaderListFragment.this
                    android.app.Activity r0 = r0.getActivity()
                    android.content.ContentResolver r0 = r0.getContentResolver()
                    r4.<init>(r0)
                    r0 = 1
                    com.android.cellbroadcastreceiver.CellBroadcastContentProvider$CellBroadcastOperation[] r0 = new com.android.cellbroadcastreceiver.CellBroadcastContentProvider.CellBroadcastOperation[r0]
                    com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw r1 = new com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastListActivity$CursorLoaderListFragment$DeleteThreadListener$TAg4p0SDbYyyZ_lTPcQfnMjLQpw
                    r1.<init>(r2)
                    r2 = 0
                    r0[r2] = r1
                    r4.execute(r0)
                    r3.dismiss()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastListActivity.CursorLoaderListFragment.DeleteThreadListener.onClick(android.content.DialogInterface, int):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onClick$0 */
            public /* synthetic */ boolean mo5355xfa4f2552(CellBroadcastContentProvider cellBroadcastContentProvider) {
                long j = this.mRowId;
                if (j != -1) {
                    return cellBroadcastContentProvider.deleteBroadcast(j);
                }
                return cellBroadcastContentProvider.deleteAllBroadcasts();
            }
        }
    }
}
