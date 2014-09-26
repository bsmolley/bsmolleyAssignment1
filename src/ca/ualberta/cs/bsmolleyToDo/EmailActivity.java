package ca.ualberta.cs.bsmolleyToDo;

import java.util.ArrayList;

import com.example.todo.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class EmailActivity extends Fragment {
	
	static ListView emailListView;
	static ArrayList<String> emailList = new ArrayList<String>();
	static ArrayAdapter<String> adapter;
	static EditText emailAddress;
	
	public EmailActivity(){
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	// Set up the view for the tab
        View emailView = inflater.inflate(R.layout.email_main, container, false);
        
        // Set up the resources used in the layout
        emailListView = (ListView) emailView.findViewById(R.id.emailListViewXML);
        Button selAll = (Button) emailView.findViewById(R.id.emSelAllBtn);
        Button emailBtn = (Button) emailView.findViewById(R.id.emEmail);
        Button unSelAll = (Button) emailView.findViewById(R.id.emUnselAll);
        emailAddress = (EditText) emailView.findViewById(R.id.emEmailSpace);

        // Set up and adapter for use with our list 
    	adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, emailList);
    	emailListView.setAdapter(adapter);

    	// Set up a button which navigates the list and selects all items
    	selAll.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				SparseBooleanArray checkedItems = emailListView.getCheckedItemPositions();
				for(int i = 0; i<=emailList.size(); i++){
					if (!checkedItems.get(i)){
						checkedItems.put(i, true);
					}
				}
				adapter.notifyDataSetChanged();			
			}
		});
    	
    	// Set up a button which navigates the list and unselects all items
    	unSelAll.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				SparseBooleanArray checkedItems = emailListView.getCheckedItemPositions();
				for(int i = 0; i<=emailList.size(); i++){
					if (checkedItems.get(i)){
						checkedItems.put(i, false);
					}
				}
				adapter.notifyDataSetChanged();
				
			}
		});
    	
    	// Set up a button that starts the send email intent
    	emailBtn.setOnClickListener(new View.OnClickListener(){	
			@Override
			public void onClick(View v) {
				sendEmail(emailAddress);		
			}
		});
 	

        return emailView;
    }
    
    // Called when returning to the page, updates the page
    public void onResume(){
    	super.onResume();
        ArrayList<String> toDoList = ToDoActivity.grabToDoList();
        ArrayList<String> archList = ArchiveActivity.grabArchList();
        composeMailList(toDoList, archList);
        adapter.notifyDataSetChanged();
    }
    
    // Learned how to send emails
    // from http://www.tutorialspoint.com/android/android_sending_email.htm Sept 2014
    
    // Used in response to a button press, sends an email with the email list contents to
    // the email address given
    private void sendEmail(EditText emailAddress) {
    	String[] address = {emailAddress.getText().toString()};
    	ArrayList<String> message = new ArrayList<String>();
    	
		SparseBooleanArray checkedItems = emailListView.getCheckedItemPositions();
		for(int i = 0; i<=emailList.size(); i++){
			if (checkedItems.get(i)){
				message.add(emailList.get(i));
			}
		}
		
		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("message/rfc822");
		if (address != null)
			email.putExtra(Intent.EXTRA_EMAIL, address);
		email.putExtra(Intent.EXTRA_SUBJECT, "To Do List Items");
		email.putExtra(Intent.EXTRA_TEXT, message.toString());
		startActivity(email);
	}

    // Learned how to combine list contents
    // from http://stackoverflow.com/questions/6383330/how-to-combine-two-array-list-and-show-in-a-listview-in-android Sept 2014
    
    // This method compiles the contents of the To Do list and the Archive list for viewing in the Email page
	private void composeMailList(ArrayList<String> toDoList, ArrayList<String> archList) {
		emailList.clear();
		emailList.addAll(toDoList);
		emailList.addAll(archList);
		adapter.notifyDataSetChanged();
    }
    

}
