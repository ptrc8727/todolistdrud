package com.todo.dao;

import java.util.*;

import com.todo.service.TodoSortByDate;
import com.todo.service.TodoSortByName;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.text.ParseException;
import com.todo.service.DbConnect;

import java.util.Date;
import java.text.SimpleDateFormat;

public class TodoList {
	Connection conn;

	public TodoList() {
		this.conn = DbConnect.getConnection();
	}
	
	public void closeC() {
		
		DbConnect.closeConnection();
		
	}

	public void importData(String filename) {
		
		try {
			
			String sqld = "delete from list;";
			String sqldd = "delete from person;";
			
			PreparedStatement pstmtd = this.conn.prepareStatement(sqld);
			pstmtd.executeUpdate();
			
			pstmtd = this.conn.prepareStatement(sqldd);
			pstmtd.executeUpdate();
			
			pstmtd.close();
			
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			String sql = "insert into list (title, memo, category, current_date, Due_date)"+" values (?,?,?,?,?);";
			String sql0 = "insert into person (writer, target)"+" values (?,?);";
			int records = 0;
			while((line=br.readLine())!=null) {
				
				StringTokenizer st = new StringTokenizer(line, "##");
				String category = st.nextToken();
				String title = st.nextToken();
				String description = st.nextToken();
				String due_date = st.nextToken();
				String current_date = st.nextToken();
				
				String writer = st.nextToken();
				String pOn = st.nextToken();
				
				PreparedStatement pstmt = this.conn.prepareStatement(sql);
				pstmt.setString(1, category);
				pstmt.setString(2, title);
				pstmt.setString(3, description);
				pstmt.setString(4, current_date);
				pstmt.setString(5, due_date);
				int count = pstmt.executeUpdate();
				if(count>0) records++;
				
				pstmt = this.conn.prepareStatement(sql0);
				pstmt.setString(1, writer);
				pstmt.setString(2, pOn);
				pstmt.executeUpdate();
				pstmt.close();
				
			}
			
			System.out.println(records+" records read!");
			br.close();
			
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public int addItem(TodoItem t) {
		
		String sql = "insert into list (title, memo, category, current_date, Due_date)"+" values (?,?,?,?,?);";
		String sql0 = "insert into person (writer, target)"+" values (?,?);";
		PreparedStatement pstmt;
		int count = 0;
		
		try {
			
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, t.getTitle());
			pstmt.setString(2, t.getDesc());
			pstmt.setString(3, t.getCategory());
			pstmt.setString(4, t.getCurrent_date());
			pstmt.setString(5, t.getDuedate());
			count = pstmt.executeUpdate();
			
			pstmt = this.conn.prepareStatement(sql0);
			pstmt.setString(1, t.getWriter());
			pstmt.setString(2, t.getpOn());
			pstmt.executeUpdate();
			pstmt.close();
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		
		return count;
		
	}

	public void deleteItem(int ind) {
		
		String sql = "delete from list where id=?;";
		String sql0 = "delete from person where id=?;";
		PreparedStatement pstmt;
		int count = 0;
		try {
			
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setInt(1,ind);
			count = pstmt.executeUpdate();
			
			pstmt = this.conn.prepareStatement(sql0);
			pstmt.setInt(1,ind);
			pstmt.executeUpdate();
			
			pstmt.close();
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}

	public int editItem(TodoItem t, int ii) {
		
		String sql = "update list set title=?, memo=?, category=?, current_date=?, due_date=?" + " where id =?;";
		String sql0 = "update person set writer=?, target=?" + " where id =?;";
		PreparedStatement pstmt;
		int count = 0;
		try {
			
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, t.getTitle());
			pstmt.setString(2, t.getDesc());
			pstmt.setString(3, t.getCategory());
			pstmt.setString(4, t.getCurrent_date());
			pstmt.setString(5, t.getDuedate());
			pstmt.setInt(6, ii);
			count = pstmt.executeUpdate();
			
			pstmt = this.conn.prepareStatement(sql0);
			pstmt.setString(1, t.getWriter());
			pstmt.setString(2, t.getpOn());
			pstmt.executeUpdate();
			
			pstmt.close();
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		
		return count;
		
	}
	
	public int len() {
		
		List<TodoItem> list = getList();
		return list.size();
		
	}
	
	public TodoItem get(int a) {
		
		List<TodoItem> list = getList();
		return list.get(a);
		
	}

	public ArrayList<TodoItem> getList() {
		
		ArrayList<TodoItem> list = new ArrayList<TodoItem>();
		Statement stmt, stmt0;
		
		try {
			stmt = this.conn.createStatement();
			stmt0 = this.conn.createStatement();
			String sql = "SELECT * FROM list";
			String sql0 = "SELECT * FROM person";
			ResultSet rs = stmt.executeQuery(sql);
			ResultSet rs0 = stmt0.executeQuery(sql0);
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String category = rs.getString("category");
				String title = rs.getString("title");
				String description = rs.getString("memo");
				String due_date = rs.getString("due_date");
				String current_date = rs.getString("current_date");
				
				String writer = rs0.getString("writer");
				String pOn = rs0.getString("target");
				
				TodoItem t = new TodoItem(title, description, category, due_date, writer, pOn);
				t.setCurrent_date(current_date);
				list.add(t);
				
			}
			
			stmt.close();
			stmt0.close();
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		
		return list;
		
	}
	
	public ArrayList<TodoItem> getListKey(String key){
		
		ArrayList<TodoItem> list = new ArrayList<TodoItem>();
		PreparedStatement pstmt, pstmt0;
		key = "%"+key+"%";
		try {
			
			String sql = "SELECT * FROM list WHERE title like ? or memo like ?";
			String sql0 = "SELECT * FROM person WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,key);
			pstmt.setString(2,key);
			ResultSet rs = pstmt.executeQuery();
			pstmt0 = conn.prepareStatement(sql0);
			
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String category = rs.getString("category");
				String title = rs.getString("title");
				String description = rs.getString("memo");
				String due_date = rs.getString("due_date");
				String current_date = rs.getString("current_date");
				
				
				pstmt0.setInt(1, id);
				ResultSet rs0 = pstmt0.executeQuery();
				String writer = rs0.getString("writer");
				String pOn = rs0.getString("target");
				
				
				TodoItem t = new TodoItem(title, description, category, due_date, writer, pOn);
				t.setCurrent_date(current_date);
				list.add(t);
				
			}
			
			pstmt0.close();
			pstmt.close();
			
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		
		return list;
		
	}
	
	public ArrayList<TodoItem> getCate(String str){
		
		ArrayList<TodoItem> list = new ArrayList<TodoItem>();
		PreparedStatement pstmt, pstmt0;
		try {
			
			String sql = "SELECT * FROM list WHERE category=?";
			String sql0 = "SELECT * FROM person WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,str);
			pstmt0 = conn.prepareStatement(sql0);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String category = rs.getString("category");
				String title = rs.getString("title");
				String description = rs.getString("memo");
				String due_date = rs.getString("due_date");
				String current_date = rs.getString("current_date");
				
				pstmt0.setInt(1, id);
				ResultSet rs0 = pstmt.executeQuery();
				String writer = rs0.getString("writer");
				String pOn = rs0.getString("target");
				
				TodoItem t = new TodoItem(title, description, category, due_date, writer, pOn);
				t.setCurrent_date(current_date);
				list.add(t);
				
			}
			
			pstmt.close();
			pstmt0.close();
			
		}catch(SQLException e){
			
			e.printStackTrace();
			
		}
		
		
		return list;
	}
	
	public ArrayList<String> getCategories(){
		
		int count =0;
		ArrayList<String> list = new ArrayList<String>();
		Statement stmt;
		try {
			
			stmt = conn.createStatement();
			String sql = "SELECT DISTINCT category FROM list";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				
				count++;
				String qq = rs.getString("category");
				System.out.print(qq );
				list.add(qq);
				
			}
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		return list;
	}
	
	public ArrayList<TodoItem> getOrderedList(String ord, int ord_i){
		
		ArrayList<TodoItem> list = new ArrayList<TodoItem>();
		Statement stmt;
		PreparedStatement pstmt0;
		
		try {
			
			stmt = conn.createStatement();
			
			String sql = "SELECT * FROM list ORDER BY "+ord;
			String sql0 = "SELECT * FROM person WHERE id=?";
			pstmt0 = conn.prepareStatement(sql0);
			if(ord_i==0) {
				
				sql+="desc";
				
			}
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				
			int id = rs.getInt("id");
			String category = rs.getString("category");
			String title = rs.getString("title");
			String description = rs.getString("memo");
			String due_date = rs.getString("due_date");
			String current_date = rs.getString("current_date");
				
			pstmt0.setInt(1, id);
			ResultSet rs0 = pstmt0.executeQuery();
			String writer = rs0.getString("writer");
			String pOn = rs0.getString("target");
			
			TodoItem t = new TodoItem(title, description, category, due_date, writer, pOn);
			t.setCurrent_date(current_date);
			list.add(t);
				
			}
			
		}catch(SQLException e){
			
			e.printStackTrace();
			
		}
		return list;
	}
	
	public int getCount() {
		
		return this.getList().size();
		
	}

	public void sortByName() {
		List<TodoItem> list = getList();
		Collections.sort(list, new TodoSortByName());

	}

	public void listAll() {
		List<TodoItem> list = getList();
		System.out.println("\n"
				+ "inside list_All method\n");
		for (TodoItem myitem : list) {
			System.out.println(myitem.getTitle() + myitem.getDesc());
		}
	}
	
	public void reverseList() {
		List<TodoItem> list = getList();
		Collections.reverse(list);
	}

	public void sortByDate() {
		List<TodoItem> list = getList();
		Collections.sort(list, new TodoSortByDate());
	}

	public int indexOf(TodoItem t) {
		List<TodoItem> list = getList();
		return list.indexOf(t);
	}

	public Boolean isDuplicate(String title) {
		List<TodoItem> list = getList();
		for (TodoItem item : list) {
			if (title.equals(item.getTitle())) return true;
		}
		return false;
	}
	
	public int indexOf(String str) {
		
		List<TodoItem> list = getList();
		return list.indexOf(str);
		
	}
	
	public void printDateRemain() {
		
		String pattern = "yyyy-MM-dd HH:mm:ss";
	    
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		    
		String cur_date=sdf.format(new Date());
		int i=1;
		List<TodoItem> list = getList();
		for (TodoItem item : list) {
			
			try {
				
				Date cur = sdf.parse(cur_date);
				Date due = sdf.parse(item.getDuedate());
				
				long calDate = due.getTime() - cur.getTime(); 
				long reDays = calDate/(24*60*60*1000);
				
				if(reDays>0) {
					
					System.out.println(i+"  "+item.getTitle()+"is "+reDays+" Days left...!");
					
				}else if(reDays == 0) {
					
					System.out.println(i+"  "+item.getTitle()+" the deadline of this work is today...!");
					
				}else {
					
					System.out.println(i+"  "+item.getTitle()+" the deadline has already overed...!");
					
				}
				
				
			}catch(ParseException e) {
				
				e.printStackTrace();
				
			}
			i++;
		}
		
	}
	
	
}
