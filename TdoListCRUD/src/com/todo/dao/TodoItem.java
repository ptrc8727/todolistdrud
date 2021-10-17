package com.todo.dao;

import java.util.Date;
import java.text.SimpleDateFormat;

public class TodoItem {
	
	@Override
	public String toString() {
		return "[" + category + "], title=" + title + ", desc=" + desc +"person on duty"+pOn+ ", duedate=" + duedate
				+ ", current_date=" + current_date + "[writer]"+writer;
	}

	private String category;
	private String duedate;
	private String title;
    private String desc;
    private String current_date;
    private String writer;
    private String pOn;
    
    String pattern = "yyyy-MM-dd HH:mm:ss";
    
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    
    public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getpOn() {
		return pOn;
	}

	public void setpOn(String pOn) {
		this.pOn = pOn;
	}

	public String getDuedate() {
		return duedate;
	}

	public void setDuedate(String duedate) {
		this.duedate = duedate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public TodoItem(String category, String title, String desc, String duedate, String writer, String pOn){
        
		this.category = category;
		this.title=title;
        this.desc=desc;
        this.current_date=sdf.format(new Date());
        this.duedate = duedate;
        this.writer = writer;
        this.pOn = pOn;
        
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }
    
    public String toSaveString() {
    	
    	return category+"##"+title+"##"+desc+"##"+current_date+"##"+duedate+"##"+writer+"##"+pOn+"\n";
    	
    }
}
