/*   Copyright 2014 Brandon Smolley

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   */

package ca.ualberta.cs.bsmolleyToDo;

import java.util.ArrayList;

import com.example.todo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ToDoActivity extends Fragment {
		
    // Set up ArrayAdapter for use with the listview
	static ListView toDoList;
    static ArrayList<String> theToDoList = new ArrayList<String>();
    static ArrayAdapter<String> adapter;
    
    static int totalToDoItems = 0;
    static int toDoChecked = 0;
    static int toDoUnchecked = 0;  
  
	// The constructor
	public ToDoActivity(){		
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	// Interpreted code from http://www.cs.dartmouth.edu/~campbell/cs65/lecture08/lecture08.html Sept 2014
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	// Set up the view for the tab
        View todoView = inflater.inflate(R.layout.to_do_main, container, false);
        
    	// Set up the resources used in the layout
    	final Button addBtn = (Button) todoView.findViewById(R.id.todoAddBtn);
    	final Button delBtn = (Button) todoView.findViewById(R.id.todoDelBtn);
    	final Button arcBtn = (Button) todoView.findViewById(R.id.todoArcBtn);
    	final EditText toDoItem = (EditText) todoView.findViewById(R.id.todoMess);
    	toDoList = (ListView) todoView.findViewById(R.id.todoList);
    	
        // Set up and adapter for use with our list 
    	// Used list adapter code from http://sunil-android.blogspot.ca/2013/08/actionbar-tab-listfragment-in-android.html Sept 2014
    	// Used code from http://stackoverflow.com/questions/15547997/android-listview-in-fragment-layout Sept 2014
    	// Used code from http://www.androprogrammer.com/2014/03/create-android-dynamic-view-with.html Sept 2014
    	adapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_multiple_choice, theToDoList);
    	toDoList.setAdapter(adapter);
    	adapter.notifyDataSetChanged();
    	
    	// Set up a long click listener which crosses or restores To Do list items
    	toDoList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String toDo = (String) toDoList.getAdapter().getItem(position);
				if (toDo.contains("[X] ")){
					String newToDo = toDo.replace("[X] ", "[  ] ");
					theToDoList.remove(position);
					theToDoList.add(position, newToDo);
				}
				else{
					String newToDo = toDo.replace("[  ] ", "[X] ");
					theToDoList.remove(position);
					theToDoList.add(position, newToDo);
				}

				adapter.notifyDataSetChanged();
				
				return true;
			}
    		
    	});
    	
    	// Learned about resetting text fields from
    	// http://stackoverflow.com/questions/5972306/set-temporary-text-in-a-edittext-textview-in-android Sept 2014
    	// Set up the Add to List button, take text from EditText field and set it to the ListView
    	addBtn.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				String input = toDoItem.getText().toString();
				addToDo(input);
				toDoItem.setText(null); // Resets the text field after message is entered
				adapter.notifyDataSetChanged();			
			}
		});
    	
    	// Learned how to use checked list items
    	// from http://theopentutorials.com/tutorials/android/listview/android-multiple-selection-listview/ Sept 2014
    	// Set up a Delete button, used to delete selected items from the To Do list
    	delBtn.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				int listSize = toDoList.getCount();
				SparseBooleanArray checkedItems = toDoList.getCheckedItemPositions();
				delToDo(checkedItems, adapter, listSize);    
                adapter.notifyDataSetChanged();								
			}
		});
    	
    	// Set up an Archive button, which sends selected To Do items to the archive
    	arcBtn.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				int listSize = toDoList.getCount();
				SparseBooleanArray checkedItems = toDoList.getCheckedItemPositions();
				ArchiveActivity.grabToDoItems(checkedItems, theToDoList, listSize);  
				delToDo(checkedItems, adapter, listSize);
                adapter.notifyDataSetChanged();	
			}
		});
    	
    	return todoView;
    }
    
	// Learned about the interaction between EditText and ListView
	// from http://stackoverflow.com/questions/16981501/android-listview-with-edittext-and-textview Sept 2014
    private void addToDo(String text){
    	if (text.length() > 0){
    		theToDoList.add("[  ] " + text); 
    	}
    	 	
    }
    
    // Learned how to remove items from a list view
    // from http://wptrafficanalyzer.in/blog/deleting-selected-items-from-listview-in-android/ Sept 2014
    // This method is called to when the Delete Selected button is called, it removes To Do items
    private void delToDo(SparseBooleanArray array, ArrayAdapter<String> adapter, int size){
		for(int i = size -1; i >= 0; i--){
            if(array.get(i)){
                adapter.remove(theToDoList.get(i));
            }
        }
		array.clear();
    }
    
    // Learned how to add items to a list
    // from http://stackoverflow.com/questions/4540754/dynamically-add-elements-to-a-listview-android Sept 2014
    // Method called from the Archive to send Archived items back to the To Do list
    public static void grabArchItems(SparseBooleanArray array, ArrayList<String> archList, int size){ 	
		for(int i = size -1; i >= 0; i--){
            if(array.get(i)){
                theToDoList.add(archList.get(i));
                adapter.notifyDataSetChanged();            
            }
        }	
    }

    // Method used to return the size of the To Do list
	public static int grabToDoHistory() {
    	totalToDoItems = toDoList.getCount();
		return totalToDoItems;
	}

	// Method used to return the number of completed To Do list items
	public static int grabToDoChecked() {
		int size = theToDoList.size();
		toDoChecked = 0;
		
		for (int i = size -1; i >= 0; i--){
			if (theToDoList.get(i).contains("[X] ")){
				toDoChecked += 1;
			}
		}
		return toDoChecked;
	}

	// Method used to return the number of incomplete To Do list items
	public static int grabToDoUnchecked() {
		totalToDoItems = toDoList.getCount();
		toDoUnchecked = totalToDoItems - toDoChecked;
		return toDoUnchecked;
	}

	// Returns the current To Do list
	public static ArrayList<String> grabToDoList() {
		return theToDoList;
	}
    
}
