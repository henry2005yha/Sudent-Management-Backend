package com.student.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.student.framework.KeyGenerator;
import com.student.mgr.Service001Mgr;
import com.student.share.DetailSet;
import com.student.share.Pager;
import com.student.share.Result;
import com.student.share.Student;
import com.student.share.StudentDetail;
import com.student.share.StudentList;
import com.student.share.StudentListSet;
import com.student.share.detailid;
import com.sun.jersey.core.header.FormDataContentDisposition;

public class Service001Dao {

	public static String datetoString() {
		String date = "";
		java.util.Date l_Date = new java.util.Date();
		date = getStartZero(4, String.valueOf(l_Date.getYear() + 1900))
				+ getStartZero(2, String.valueOf(l_Date.getMonth() + 1))
				+ getStartZero(2, String.valueOf(l_Date.getDate()));

		return date;
	}

	public static String getStartZero(int aZeroCount, String aValue) {
		while (aValue.length() < aZeroCount) {
			aValue = "0" + aValue;
		}
		return aValue;
	}

	public static Result save(Student data, Connection conn) throws SQLException {
		// System.out.println("i m in save");
		Result rs = new Result();

		String syskey = String.valueOf(KeyGenerator.generateSyskey());
		// System.out.println(syskey);
		String date = datetoString();

		String imageName = Service001Mgr.UQImageName;
		System.out.println(imageName);
		// insert query

		PreparedStatement ps = conn.prepareStatement(
				"Insert into student_info (student_syskey, student_id, name, nrc, dob, phone, gender, nationality, address, photo, created_date, modified_date, record_status) values (?,?,?,?,?,?,?,?,?,?,?,?,?);");
		ps.setString(1, syskey);
		ps.setString(2, data.getStudent_id());
		ps.setString(3, data.getName());
		ps.setString(4, data.getNrc());
		ps.setString(5, data.getDob());
		ps.setString(6, data.getPhone());
		ps.setString(7, data.getGender());
		ps.setString(8, data.getNationality());
		ps.setString(9, data.getAddress());
		ps.setString(10, imageName);
		ps.setString(11, date);
		ps.setString(12, date);
		ps.setInt(13, 1);

		if (ps.executeUpdate() > 0) {
			for (int i = 0; i < data.getDetail().size(); i++) {
				PreparedStatement stmt = conn.prepareStatement(
						"Insert into detail (year, mark1, mark2, mark3, remark, student_syskey, record_status, created_date, modified_date) values (?,?,?,?,?,?,?,?,?);");
				// System.out.println(data.getDetail().get(i).getYear());
				stmt.setString(1, data.getDetail().get(i).getYear());
				stmt.setInt(2, data.getDetail().get(i).getMark1());
				stmt.setInt(3, data.getDetail().get(i).getMark2());
				stmt.setInt(4, data.getDetail().get(i).getMark3());
				stmt.setString(5, data.getDetail().get(i).getRemark());
				stmt.setString(6, syskey);
				stmt.setInt(7, 1);
				stmt.setString(8, date);
				stmt.setString(9, date);

				if (stmt.executeUpdate() > 0) {
					rs.setMsg("Save Successfully");
				} else {
					rs.setMsg("failed to insert into detail");
				}
			}
		} else {
			rs.setMsg("failed to insert into student_info");
		}
		Service001Mgr.UQImageName = "";
		return rs;
	}

	public static StudentListSet getList(Pager data, Connection conn) throws SQLException {
		StudentListSet sl = new StudentListSet();
		ArrayList<StudentList> list = new ArrayList<StudentList>();

		String whereClause = " where record_status=1";
		if (!data.getSearchval().equalsIgnoreCase("")) {
			whereClause += " and (name like '%" + data.getSearchval() + "%' OR student_id like '%" + data.getSearchval()
					+ "%' OR nrc like '%" + data.getSearchval() + "%' OR phone like '%" + data.getSearchval() + "%')";
		}
		int end = data.getPagesize() * data.getCurrentpage();
		int start = (data.getCurrentpage() - 1) * data.getPagesize() + 1;
		PreparedStatement ps = conn.prepareStatement(
				"SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY student_syskey desc) AS RowNum,* FROM (select student_syskey,student_id,name,nrc,dob,phone,gender,nationality,address,photo from student_info "
						+ whereClause
						+ " ) b) AS RowConstrainedResult WHERE RowNum >= ? AND RowNum <= ? order by student_syskey desc;");
		ps.setInt(1, start);
		ps.setInt(2, end);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			StudentList s = new StudentList();
			s.setStudent_syskey(rs.getString("student_syskey"));
			s.setStudent_id(rs.getString("student_id"));
			s.setName(rs.getString("name"));
			s.setNrc(rs.getString("nrc"));
			s.setDob(rs.getString("dob"));
			s.setPhone(rs.getString("phone"));
			s.setGender(rs.getString("gender"));
			s.setNationality(rs.getString("nationality"));
			s.setAddress(rs.getString("address"));
			s.setPhoto(rs.getString("photo"));
			list.add(s);
		}
		if (list.size() > 0) {
			String query = "select count(student_syskey) as total from student_info" + whereClause;
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet res = pst.executeQuery();
			if (res.next()) {
				sl.setCount(res.getInt("total"));
			}
			sl.setList(list);
			sl.setMsg("success");
		} else {
			sl.setMsg("Data Not Found");
		}

		return sl;
	}

	public static Result delete(String syskey, Connection conn) throws SQLException {
		Result rs = new Result();
		PreparedStatement ps = conn.prepareStatement("Update student_info set record_status=4 where student_syskey =?");
		ps.setString(1, syskey);
		if (ps.executeUpdate() > 0) {
			PreparedStatement stmt = conn.prepareStatement("Update detail set record_status=4 where student_syskey =?");
			stmt.setString(1, syskey);
			if (stmt.executeUpdate() > 0) {
				rs.setMsg("Delete successfully");
			} else {
				rs.setMsg("Delete failed");
			}
		} else {
			rs.setMsg("Delete failed");
		}
		return rs;
	}

	public static Student getStudent(String syskey, Connection conn) throws SQLException {
		Student student = new Student();
		List<StudentDetail> detail = new ArrayList<StudentDetail>();
		PreparedStatement ps = conn.prepareStatement(
				"select  student_syskey,student_id,name,nrc,dob,phone,gender,nationality,address,photo from student_info where record_status=1 and student_syskey=?");
		ps.setString(1, syskey);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			student.setStudent_syskey(rs.getString("student_syskey"));
			student.setStudent_id(rs.getString("student_id"));
			student.setName(rs.getString("name"));
			student.setNrc(rs.getString("nrc"));
			student.setDob(rs.getString("dob"));
			student.setPhone(rs.getString("phone"));
			student.setGender(rs.getString("gender"));
			student.setNationality(rs.getString("nationality"));
			student.setAddress(rs.getString("address"));
			student.setPhoto(rs.getString("photo"));

			PreparedStatement stmt = conn.prepareStatement(
					"select  year,mark1,mark2,mark3,remark from detail where record_status=1 and student_syskey=?");
			stmt.setString(1, syskey);
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				StudentDetail sd = new StudentDetail();
				sd.setYear(res.getString("year"));
				sd.setMark1(res.getInt("mark1"));
				sd.setMark2(res.getInt("mark2"));
				sd.setMark3(res.getInt("mark3"));
				sd.setRemark(res.getString("remark"));
				detail.add(sd);
			}
			if (detail.size() > 0) {
				student.setDetail(detail);
				;
			}
		}
		return student;

	}

	public static Result update(Student data, Connection conn) throws SQLException {
		Result rs = new Result();
		System.out.println(data.getStudent_syskey());
		String date = datetoString();
		String imageName = Service001Mgr.UQImageName;
		System.out.println(imageName);
		// update query
		PreparedStatement ps = conn.prepareStatement(
				"update student_info set student_id=?,name=?,nrc=?,dob=?,phone=?,gender=?,nationality=?,address=?,photo=?,modified_date=? where student_syskey=?");
		ps.setString(1, data.getStudent_id());
		ps.setString(2, data.getName());
		ps.setString(3, data.getNrc());
		ps.setString(4, data.getDob());
		ps.setString(5, data.getPhone());
		ps.setString(6, data.getGender());
		ps.setString(7, data.getNationality());
		ps.setString(8, data.getAddress());
		ps.setString(9, imageName);
		ps.setString(10, date);
		ps.setString(11, data.getStudent_syskey());
		if (ps.executeUpdate() > 0) {
			PreparedStatement stmt = conn.prepareStatement("Update detail set record_status=4 where student_syskey =?");
			stmt.setString(1, data.getStudent_syskey());
			stmt.executeUpdate();
			for (int i = 0; i < data.getDetail().size(); i++) {
				PreparedStatement pstmt = conn.prepareStatement(
						"Insert into detail (year, mark1, mark2, mark3, remark, student_syskey, record_status, created_date, modified_date) values (?,?,?,?,?,?,?,?,?);");
				// System.out.println(data.getDetail().get(i).getYear());
				pstmt.setString(1, data.getDetail().get(i).getYear());
				pstmt.setInt(2, data.getDetail().get(i).getMark1());
				pstmt.setInt(3, data.getDetail().get(i).getMark2());
				pstmt.setInt(4, data.getDetail().get(i).getMark3());
				pstmt.setString(5, data.getDetail().get(i).getRemark());
				pstmt.setString(6, data.getStudent_syskey());
				pstmt.setInt(7, 1);
				pstmt.setString(8, date);
				pstmt.setString(9, date);

				if (pstmt.executeUpdate() > 0) {
					rs.setMsg("Update Successfully");
				} else {
					rs.setMsg("failed to update detail");
				}

			}
		} else {
			rs.setMsg("failed to Update student_info");
		}

		return rs;
	}

	public static StudentListSet getListForExcel(Connection conn) throws SQLException {
		StudentListSet sl = new StudentListSet();
		ArrayList<StudentList> list = new ArrayList<StudentList>();

		String whereClause = " where record_status=1";
		PreparedStatement ps = conn.prepareStatement(
				"Select student_syskey,student_id,name,nrc,dob,phone,gender,nationality,address,photo from student_info "
						+ whereClause);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			StudentList s = new StudentList();
			s.setStudent_syskey(rs.getString("student_syskey"));
			s.setStudent_id(rs.getString("student_id"));
			s.setName(rs.getString("name"));
			s.setNrc(rs.getString("nrc"));
			s.setDob(rs.getString("dob"));
			s.setPhone(rs.getString("phone"));
			s.setGender(rs.getString("gender"));
			s.setNationality(rs.getString("nationality"));
			s.setAddress(rs.getString("address"));
			s.setPhoto(rs.getString("photo"));
			list.add(s);
		}
		if (list.size() > 0) {
			String query = "select count(student_syskey) as total from student_info" + whereClause;
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet res = pst.executeQuery();
			if (res.next()) {
				sl.setCount(res.getInt("total"));
			}
			sl.setList(list);
			sl.setMsg("success");
		} else {
			sl.setMsg("Data Not Found");
		}

		return sl;
	}

	public static DetailSet getDetailForExcel(Connection conn) throws SQLException {
		DetailSet ds = new DetailSet();
		ArrayList<detailid> list = new ArrayList<detailid>();
		PreparedStatement ps = conn.prepareStatement(
				"select student_info.student_syskey, name, student_id, year, mark1, mark2, mark3, remark, student_info.record_status from student_info Join detail on student_info.student_syskey = detail.student_syskey where student_info.record_status = 1 and detail.record_status = 1;");
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			detailid s = new detailid();
			s.setStudent_syskey(rs.getString("student_syskey"));
			s.setName(rs.getString("name"));
			s.setStudent_id(rs.getString("student_id"));
			s.setYear(rs.getString("year"));
			s.setMark1(rs.getInt("mark1"));
			s.setMark2(rs.getInt("mark2"));
			s.setMark3(rs.getInt("mark3"));
			s.setRemark(rs.getString("remark"));
			list.add(s);
		}
		if (list.size() > 0) {
			ds.setDeatilList(list);
		}
		return ds;
	}

	public static Boolean checkId(String ID, Connection conn) throws SQLException {
		Boolean uq = true;
		PreparedStatement ps = conn.prepareStatement(
				"select student_syskey from student_info where student_id = ? and record_status = 1;");
		ps.setString(1, ID);
		ResultSet rss = ps.executeQuery();
		if (rss.next()) {
			uq = false;
		} else {
			uq = true;
		}

		return uq;
	}
	
	public static Boolean checkNrc(String NRC, Connection conn) throws SQLException {
		Boolean uq = true;
		PreparedStatement ps = conn.prepareStatement(
				"select student_syskey from student_info where nrc = ? and record_status = 1;");
		ps.setString(1, NRC);
		ResultSet rss = ps.executeQuery();
		if (rss.next()) {
			uq = false;
		} else {
			uq = true;
		}

		return uq;
	}
	
	

	public static Result ExcelImport(InputStream excelFile, FormDataContentDisposition fileMetaData, Connection conn)
			throws SQLException, EncryptedDocumentException, IOException {
		Result rs = new Result();
		List<String> studentIDs = new ArrayList<String>();
		PreparedStatement ps = conn.prepareStatement("SELECT student_id FROM student_info where record_status = 1;");

		ResultSet rss = ps.executeQuery();
		while (rss.next()) {
			studentIDs.add(rss.getString("student_id"));
		}
		System.out.println(studentIDs);
		Workbook workbook = WorkbookFactory.create(excelFile);
		Sheet sheet1 = workbook.getSheetAt(0);
		Sheet sheet2 = workbook.getSheetAt(1);
		String date = datetoString();

		for (Row row : sheet1) {
			// Skip the first row (header row)
			if (row.getRowNum() == 0) {
				continue;
			}

			// Read cell values and insert into the database
			String id = row.getCell(0).getStringCellValue();
			String name = row.getCell(1).getStringCellValue();
			String nrc = row.getCell(2).getStringCellValue();
			String dob = row.getCell(3).getStringCellValue();
			String phone = row.getCell(4).getStringCellValue();
			String gender = row.getCell(5).getStringCellValue();
			String nationality = row.getCell(6).getStringCellValue();
			String address = row.getCell(7).getStringCellValue();

			System.out.print(name + " ");
			System.out.print(id + " ");
			System.out.print(nrc + " ");
			System.out.print(dob + " ");
			System.out.print(phone + " ");
			System.out.print(gender + " ");
			System.out.print(nationality + " ");
			System.out.println(address + " ");

			if (!studentIDs.contains(id)) {
				System.out.println("New ID is valid and not present in the existing student IDs.");
				String syskey = String.valueOf(KeyGenerator.generateSyskey());
				System.out.println(syskey);
				PreparedStatement insert = conn.prepareStatement(
						"Insert into student_info (student_syskey, student_id, name, nrc, dob, phone, gender, nationality, address, photo, created_date, modified_date, record_status) values (?,?,?,?,?,?,?,?,?,?,?,?,?);");

				insert.setString(1, syskey);
				insert.setString(2, id);
				insert.setString(3, name);
				insert.setString(4, nrc);
				insert.setString(5, dob);
				insert.setString(6, phone);
				insert.setString(7, gender);
				insert.setString(8, nationality);
				insert.setString(9, address);
				insert.setString(10, "");
				insert.setString(11, date);
				insert.setString(12, date);
				insert.setInt(13, 1);

				if (insert.executeUpdate() > 0) {
					System.out.println("Import successfully");
					rs.setMsg("Import successfully");
				} else {
					rs.setMsg("Import failed");
				}

			} else {
				System.out.println("New ID already exists in the database.");
			}

		}
		// for detail table
		for (Row row : sheet2) {
			if (row.getRowNum() == 0) {
				continue;
			}

			String sId = "";
			String year = "";
			int mark1 = 0;
			int mark2 = 0;
			int mark3 = 0;
			String remark = "";

			// student_id
			Cell sIdCell = row.getCell(1);
			if (sIdCell != null) {
				if (sIdCell.getCellType() == CellType.STRING) {
					sId = sIdCell.getStringCellValue().trim();
				} else if (sIdCell.getCellType() == CellType.NUMERIC) {
					sId = String.valueOf((int) sIdCell.getNumericCellValue());
				}
			}

			// year
			Cell yearCell = row.getCell(2);
			if (yearCell != null) {
				if (yearCell.getCellType() == CellType.STRING) {
					year = yearCell.getStringCellValue().trim();
				} else if (yearCell.getCellType() == CellType.NUMERIC) {
					year = String.valueOf((int) yearCell.getNumericCellValue());
				}
			}

			// Handling mark1, mark2, and mark3 columns (Columns 4, 5, and 6 in Excel)
			Cell mark1Cell = row.getCell(3);
			Cell mark2Cell = row.getCell(4);
			Cell mark3Cell = row.getCell(5);

			if (mark1Cell != null && mark1Cell.getCellType() == CellType.NUMERIC) {
				mark1 = (int) mark1Cell.getNumericCellValue();
			}

			if (mark2Cell != null && mark2Cell.getCellType() == CellType.NUMERIC) {
				mark2 = (int) mark2Cell.getNumericCellValue();
			}

			if (mark3Cell != null && mark3Cell.getCellType() == CellType.NUMERIC) {
				mark3 = (int) mark3Cell.getNumericCellValue();
			}

			// remark
			Cell remarkCell = row.getCell(6);
			if (remarkCell != null) {
				if (remarkCell.getCellType() == CellType.STRING) {
					remark = remarkCell.getStringCellValue().trim();
				} else if (remarkCell.getCellType() == CellType.NUMERIC) {
					remark = String.valueOf((int) yearCell.getNumericCellValue());
				}
			}

			// update or insert for each rows
			PreparedStatement student_syskey = conn.prepareStatement(
					" SELECT detail.student_syskey  FROM detail join student_info on detail.student_syskey = student_info.student_syskey WHERE student_id = ? and year = ? and detail.record_status = 1;");
			PreparedStatement insertDetail = conn.prepareStatement(
					"Insert into detail (year, mark1, mark2, mark3, remark, student_syskey, record_status, created_date, modified_date) values (?,?,?,?,?,?,?,?,?);");

			student_syskey.setString(1, sId);
			student_syskey.setString(2, year);
			ResultSet rs1 = student_syskey.executeQuery();
			
			if (rs1.next()) {

				PreparedStatement updateDetail = conn.prepareStatement(
						"Update detail set mark1=?, mark2=?, mark3=?, remark=?, modified_date=? where student_syskey=? and year=? and record_status=1;");

				String Ssyskey = rs1.getString("student_syskey");

				updateDetail.setInt(1, mark1);
				updateDetail.setInt(2, mark2);
				updateDetail.setInt(3, mark3);
				updateDetail.setString(4, remark);
				updateDetail.setString(5, date);
				updateDetail.setString(6, Ssyskey);
				updateDetail.setString(7, year);

				if (updateDetail.executeUpdate() > 0) {
					rs.setMsg("Import successfully");
				} else {
					rs.setMsg("Import failed");
				}
			}
			// insert
			else {
				PreparedStatement selectSyskey = conn.prepareStatement(
						"select student_syskey from student_info where student_id =? and record_status=1;");
				selectSyskey.setString(1, sId);
				ResultSet rs2 = selectSyskey.executeQuery();
				
				if(rs2.next()){
					
					String Ssyskey = rs2.getString("student_syskey");
					
					insertDetail.setString(1, year);
					insertDetail.setInt(2, mark1);
					insertDetail.setInt(3, mark2);
					insertDetail.setInt(4, mark3);
					insertDetail.setString(5, remark);
					insertDetail.setString(6, Ssyskey);
					insertDetail.setInt(7, 1);
					insertDetail.setString(8, date);
					insertDetail.setString(9, date);
					
					if (insertDetail.executeUpdate() > 0) {
						rs.setMsg("Import successfully");
					} else {
						rs.setMsg("Import failed");
					}
				}
			}
		}

		return rs;
	}

	
}
