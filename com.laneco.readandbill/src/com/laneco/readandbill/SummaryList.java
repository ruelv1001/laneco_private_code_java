package com.laneco.readandbill;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.generic.readandbill.R;
import com.laneco.readandbill.database.Consumer;
import com.laneco.readandbill.database.ConsumerDataSource;
import com.laneco.readandbill.database.Reading;
import com.laneco.readandbill.database.ReadingDataSource;
import java.util.Comparator;
import java.util.List;
public class SummaryList extends com.generic.readandbill.SummaryList{
	private static final int READ = 20;
    private static final int UNREAD = 10;
    private Activity activity;
    private String activityTitle;
    private ConsumerArrayAdapter adapter;
    private Context context;
    private ConsumerDataSource dsc;
    
    // orig line do not delete
    //private ReadingDataSource dsr;
    // end orig line
    
    // new line delete anytime
    protected ReadingDataSource dsr;
    // end new line
    
    private String sortingType;
    private List<Consumer> values;

    class C00411 implements TextWatcher {
        C00411() {
        }

        public void afterTextChanged(Editable s) {
        }
        
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (SummaryList.this.adapter != null) {
                if (count < before) {
                    SummaryList.this.adapter.resetData();
                }
                SummaryList.this.adapter.getFilter().filter(s.toString());
            }
        }
    }
    
    // START OF PARAGRAPH
    // delete this anytime
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Consumer Context Menu");
        menu.add(0, v.getId(), 0, "View Reading Details");
        menu.add(0, v.getId(), 0, "Reprint");
        menu.add(0, v.getId(), 0, "Delete Reading");
    }
  
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Reading reading = this.dsr.getReading(this.adapter.getItem(position).getId(), (int) SORT_SEQUENCE);
        if (reading.getId() != -1) {
            registerForContextMenu(getListView());
            l.showContextMenuForChild(v);
            return;
        }
        Intent intent = myTransactionActivityCaller(reading);
        intent.putExtra("id", this.adapter.getItemId(position));
        intent.putExtra("pos", position);
        startActivityForResult(intent, SORT_ACCOUNT_NUMBER);
    }
    
    public boolean onContextItemSelected(MenuItem item) {
        long position = (long) ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        Consumer consumer = this.adapter.getItem((int) position);
        Reading reading = this.dsr.getReading(consumer.getId(), (int) SORT_SEQUENCE);
        if (item.getTitle().equals("Delete Reading")) {
            this.dsr.updateReadingCancelled(position+1);
            this.adapter.notifyDataSetChanged();
        } else if (item.getTitle().equals("View Reading Details")) {
            Intent intent = myTransactionActivityCaller(reading);
            intent.putExtra("id", this.adapter.getItemId((int) position));
            intent.putExtra("pos", position);
            startActivityForResult(intent, SORT_ACCOUNT_NUMBER);
        } else if (item.getTitle().equals("Reprint")) {
            SplashScreen.btPrinter.print(new StatementGenerator(this.context, consumer, reading).generateSOA());
        }
        return true;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == SORT_ACCOUNT_NUMBER) {
            applySort();
        }
    }

	protected Intent myTransactionActivityCaller(Reading reading) {
    if (reading.getId() != -1) {
        return new Intent(this, MyReadingEntry.class);
    }
    return new Intent(this, MyReadingEntry.class);
	}

	public void onContextMenuClosed(Menu menu) {
    super.onContextMenuClosed(menu);
    unregisterForContextMenu(getListView());
	}
    //END OF PARAGRAPH

    class C00422 implements Comparator<Consumer> {
        C00422() {
        }

        public int compare(Consumer lhs, Consumer rhs) {
            return lhs.getAccountNumber().compareTo(rhs.getAccountNumber());
        }
    }

    class C00433 implements Comparator<Consumer> {
        C00433() {
        }

        public int compare(Consumer lhs, Consumer rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    class C00444 implements Comparator<Consumer> {
        C00444() {
        }

        public int compare(Consumer lhs, Consumer rhs) {
            return lhs.getMeterSerial().compareTo(rhs.getMeterSerial());
        }
    }

    public SummaryList() {
        this.activityTitle = "Read Consumer Summary";
        this.sortingType = "(Account Number)";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dsc = new ConsumerDataSource(this);
        this.dsc = new ConsumerDataSource(this);
        this.dsr = new ReadingDataSource(this);
        setContentView(R.layout.activity_summary);
        setTitle(this.activityTitle + " " + this.sortingType);
        this.search = (EditText) findViewById(R.id.etslSearch);
        this.totalConsumer = (TextView) findViewById(R.id.tvslTotalRecord);
        this.totalRead = (TextView) findViewById(R.id.tvslTotalRead);
        this.totalUnread = (TextView) findViewById(R.id.tvslTotalUnread);
    }
    
    protected void onResume() {
        super.onResume();
        changeListMode(READ);
    }

    private void changeListMode(int mode) {
        String str;
        switch (mode) {
            case UNREAD /*10*/:
                str = "Unread Consumer Summary";
                this.activityTitle = str;
                this.values = this.dsc.getUnreadSummary();
                this.adapter = new ConsumerArrayAdapter(this, this.values);
                this.totalConsumer.setText(this.dsc.getNumberOfConsumer().toString());
                this.totalUnread.setText(Integer.toString(this.values.size()));
                this.totalRead.setText(Integer.toString(this.dsc.getNumberOfConsumer().intValue() - this.values.size()));
                break;
            case READ /*20*/:
                str = "Read Consumer Summary";
                this.activityTitle = str;
                this.values = this.dsc.getReadSummary();
                this.adapter = new ConsumerArrayAdapter(this, this.values);
                this.totalConsumer.setText(this.dsc.getNumberOfConsumer().toString());
                this.totalRead.setText(Integer.toString(this.values.size()));
                this.totalUnread.setText(Integer.toString(this.dsc.getNumberOfConsumer().intValue() - this.values.size()));
                break;
        }
        switch (this.sortMode) {
        case ConsumerArrayAdapter.ACCOUNT_NUMBER /*UNREAD 10*/:
            this.sortingType = "(Account Number)";
            break;
        case ConsumerArrayAdapter.NAME /*30*/:
            this.sortingType = "(Name)";
            break;
        case ConsumerArrayAdapter.METER_SERIAL /*40*/:
            this.sortingType = "(Meter Serial)";
            break;
        }
        setTitle(this.activityTitle + " " + this.sortingType);
        applySort();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem unReadOnly = menu.findItem(R.id.iclSeeOnlyUnread);
        MenuItem printSummary = menu.findItem(R.id.islPrintSummary);
        unReadOnly.setVisible(false);
        printSummary.setVisible(true);
        menu.add(0, 1, 5, "Switch to unread");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Switch to unread")) {
            item.setTitle("Switch to read");
            changeListMode(UNREAD);
            return true;
        } else if (item.getTitle().equals("Switch to read")) {
            item.setTitle("Switch to unread");
            changeListMode(READ);
            return true;
        } else if (item.getItemId() != R.id.islPrintSummary) {
            return super.onOptionsItemSelected(item);
        } else {
            SplashScreen.btPrinter.print(new SummaryDataGenerator(this, this.values, this.dsc.getNumberOfConsumer()).getSummary());
            return true;
        }
    }
    
    protected void applySort() {
        if (this.adapter != null) {
            switch (this.sortMode) {
                case UNREAD /*10*/:
                    this.adapter.sort(new C00422());
                    this.adapter.setFilterMode(UNREAD);
                    setTitle("Summary List (Account Number)");
                    break;
                case ConsumerArrayAdapter.NAME /*30*/:
                    this.adapter.sort(new C00433());
                    this.adapter.setFilterMode(READ);
                    setTitle("Summary List (Name)");
                    break;
                case ConsumerArrayAdapter.METER_SERIAL /*40*/:
                	this.adapter.sort(new C00444());
                    this.adapter.setFilterMode(40);
                    setTitle("Summary List (Meter Serial)");
                    break;
            }
            
            setListAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
        }
    }
}
