/*******************************************************************************
 * Copyright (c) 2014 Richard Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Richard Hirner (bitfire web engineering) - initial API and implementation
 ******************************************************************************/
package at.bitfire.davdroid.syncadapter;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.azapps.mirakel.davdroid.R;

public class SelectCollectionsFragment extends ListFragment {
	public static final String KEY_SERVER_INFO = "server_info";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final ListView listView = getListView();
		listView.setPadding(20, 30, 20, 30);
		
		View header = getActivity().getLayoutInflater().inflate(R.layout.select_collections_header, null);
		listView.addHeaderView(header);
		
		final ServerInfo serverInfo = (ServerInfo)getArguments().getSerializable(KEY_SERVER_INFO);
		final SelectCollectionsAdapter adapter = new SelectCollectionsAdapter(view.getContext(), serverInfo);
		setListAdapter(adapter);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int itemPosition = position - 1;	// one list header view at pos. 0
				if (adapter.getItemViewType(itemPosition) == SelectCollectionsAdapter.TYPE_ADDRESS_BOOKS_ROW) {
					// unselect all other address books
					for (int pos = 1; pos <= adapter.getNAddressBooks(); pos++)
						if (pos != itemPosition)
							listView.setItemChecked(pos + 1, false);
				}
				
				getActivity().invalidateOptionsMenu();
			}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.select_collections, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.next:
			ServerInfo serverInfo = (ServerInfo)getArguments().getSerializable(KEY_SERVER_INFO);
			
			// synchronize only selected collections
			for (ServerInfo.ResourceInfo addressBook : serverInfo.getAddressBooks())
				addressBook.setEnabled(false);
			for (ServerInfo.ResourceInfo calendar : serverInfo.getCalendars())
				calendar.setEnabled(false);
			
			ListAdapter adapter = getListView().getAdapter();
			for (long id : getListView().getCheckedItemIds()) {
				int position = (int)id + 1;		// +1 because header view is inserted at pos. 0 
				ServerInfo.ResourceInfo info = (ServerInfo.ResourceInfo)adapter.getItem(position);
				info.setEnabled(true);
			}
			
			// pass to "account details" fragment
			AccountDetailsFragment accountDetails = new AccountDetailsFragment();
			Bundle arguments = new Bundle();
			arguments.putSerializable(SelectCollectionsFragment.KEY_SERVER_INFO, serverInfo);
			accountDetails.setArguments(arguments);
			
			getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, accountDetails)
				.addToBackStack(null)
				.commitAllowingStateLoss();
			break;
		default:
			return false;
		}
		return true;
	}
	
	
	// input validation
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		boolean ok = false;
		try {
			ok = getListView().getCheckedItemCount() > 0;
		} catch(IllegalStateException e) {
		}
		MenuItem item = menu.findItem(R.id.next);
		item.setEnabled(ok);
	}
}
