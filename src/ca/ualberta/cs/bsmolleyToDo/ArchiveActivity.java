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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ArchiveActivity extends Fragment{
	
	// Setting up the array/adapter that will interact with the listview
	static ListView archList;
	static ArrayList<String> theArchList = new ArrayList<String>();
	static ArrayAdapter<String> adapter;
	
    static int archItems = 0;
    static int archChecked = 0;
    static int archUnchecked = 0; 
	
	public ArchiveActivity(){		
	}
		
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	// Set up the view for the tab
        View archiveView = inflater.inflate(R.layout.archive_main, container, false);
        
     	// Set up the resources used in the layout
    	final Button archUn = (Button) archiveView.findViewById(R.id.archUnBtn);
    	final Button archDel = (Button) archiveView.findViewById(R.id.archDelBtn);
    	archList = (ListView) archiveView.findViewById(R.id.archListView);
    	
        // Set up and adapter for use with our list 
    	adapter = new ArrayAdapter<String>(getActivity(), 
    			android.R.layout.simple_list_item_multiple_choice, theArchList);
    	archList.setAdapter(adapter);
    	adapter.notifyDataSetChanged();
    	
    	// Set up the ListView to respond to long clicks, items can be marked as complete or incomplete
    	archList.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String arch = (String) archList.getAdapter().getItem(position);
				if (arch.contains("[X] ")){
					String newToDo = arch.replace("[X] ", "[  ] ");
					theArchList.remove(position);
					theArchList.add(position, newToDo);
				}
				else{
					String newToDo = arch.replace("[  ] ", "[X] ");
					theArchList.remove(position);
					theArchList.add(position, newToDo);
				}

				adapter.notifyDataSetChanged();
				
				return true;
			}
    		
    	});
    	   
    	// Button used to send selected items to back to the To Do page
    	archUn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int listSize = archList.getCount();
				SparseBooleanArray checkedItems = archList.getCheckedItemPositions();
				ToDoActivity.grabArchItems(checkedItems, theArchList, listSize);	
				checkedItems = archList.getCheckedItemPositions();
				delArch(checkedItems, adapter, listSize); 
				
				adapter.notifyDataSetChanged();	
				
			}
    		
    	});
    	
    	// Button used to delete selected items
    	archDel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int listSize = archList.getCount();
				SparseBooleanArray checkedItems = archList.getCheckedItemPositions();
				delArch(checkedItems, adapter, listSize);          
                adapter.notifyDataSetChanged();	
				
			}
    		
    	});  	
    	
        return archiveView;
    }
    
    // Method called when the Delete Selected button is pressed
    private void delArch(SparseBooleanArray array, ArrayAdapter<String> adapter, int size){
		for(int i = size -1; i >= 0; i--){
            if(array.get(i)){
                adapter.remove(theArchList.get(i));
                //archItems -= 1;
            }
        }
		array.clear();
    }
    
    // Method used to retrieve items from the To Do list that will be moved to the archive
    public static void grabToDoItems(SparseBooleanArray array, ArrayList<String> toDoList, int size){  	
		for(int i = size -1; i >= 0; i--){
           if(array.get(i)){
				theArchList.add(toDoList.get(i));
				adapter.notifyDataSetChanged();
           }
        }
    }

    // Method that returns the size of the Archive list
	public static int grabArchiveHistory() {
		archItems = archList.getCount();
		return archItems;
	}

	// Method that returns the number of completed archive items
	public static int grabArchChecked() {
		int size = theArchList.size();
		archChecked = 0;
		
		for (int i = size -1; i >= 0; i--){
			if (theArchList.get(i).contains("[X] ")){
				archChecked += 1;
			}
		}
		return archChecked;
	}

	// Method that returns the number of incomplete archive items
	public static int grabArchUnchecked() {
		archItems = archList.getCount();
		archUnchecked = archItems - archChecked;
		return archUnchecked;
	}

	// Returns the Archive list
	public static ArrayList<String> grabArchList() {
		return theArchList;
	}
    
}
