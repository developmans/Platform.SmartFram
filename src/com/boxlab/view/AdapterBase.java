package com.boxlab.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
/**
 * »ù±¾µÄAdapter
 * 
 *
 */
public abstract class AdapterBase<E> extends BaseAdapter {

	private final List<E> DummyList = new ArrayList<E>();
	protected List<E> mList;
	protected Context mContext;
	protected LayoutInflater mLayoutInflater;
	
	public AdapterBase(Context pContext,List<E> pList)
	{
		mContext = pContext;
		if(pList != null)
			mList = pList;
		else
			mList = DummyList;
		mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	public LayoutInflater getLayoutInflater()
	{
		return mLayoutInflater;
	}
	
	public Context getContext()
	{
		return mContext;
	}
	
	public List<E> getList()
	{
		return mList;
	}
	
	public void setList(List<E> pList) {
		mList = pList;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int pPosition) {
		return mList.get(pPosition);
	}

	@Override
	public long getItemId(int pPosition) {
		return pPosition;
	}

}
