package com.student.share;

public class Pager {
	private int pagesize = 0;
	private int currentpage = 1;
	private String searchval = "";

	

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	public String getSearchval() {
		return searchval;
	}

	public void setSearchval(String searchval) {
		this.searchval = searchval;
	}

}
