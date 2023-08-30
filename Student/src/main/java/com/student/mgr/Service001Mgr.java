package com.student.mgr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.UUID;

import com.student.dao.Service001Dao;
import com.student.framework.ConnAdmin;
import com.student.framework.ServerSession;
import com.student.share.DetailSet;
import com.student.share.Pager;
import com.student.share.Result;
import com.student.share.Student;
import com.student.share.StudentList;
import com.student.share.StudentListSet;
import com.student.share.detailid;
import com.sun.jersey.core.header.FormDataContentDisposition;

public class Service001Mgr {

	public static String UQImageName = "";

	public static Result save(Student data) {
		Result rs = new Result();

		try (Connection conn = ConnAdmin.getConn()) {

			if (conn != null) {

				if (data.getStudent_syskey().equalsIgnoreCase("")) {
					String ID = data.getStudent_id();
					Boolean uqid = Service001Dao.checkId(ID, conn);
					if (uqid) {
						String NRC = data.getNrc();
						Boolean uqnrc = Service001Dao.checkNrc(NRC, conn);
						if(uqnrc) {
						// save.......
						rs = Service001Dao.save(data, conn);
						}else {
							rs.setMsg("NRC already exists");
						}
					}else {
						rs.setMsg("ID already exists");
					}
				} else {
					// update......
					System.out.println("in update");
					rs = Service001Dao.update(data, conn);
				}
			} else {
				rs.setMsg("Can't Connect To Database");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static StudentListSet getList(Pager data) {

		StudentListSet sl = new StudentListSet();
		try (Connection conn = ConnAdmin.getConn()) {
			if (conn != null) {
				sl = Service001Dao.getList(data, conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sl;
	}

	public static StudentListSet getListForExcel() {

		StudentListSet sl = new StudentListSet();
		try (Connection conn = ConnAdmin.getConn()) {
			if (conn != null) {
				sl = Service001Dao.getListForExcel(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sl;
	}

	public static Result delete(String syskey) {
		Result rs = new Result();
		try (Connection conn = ConnAdmin.getConn()) {
			if (conn != null) {
				rs = Service001Dao.delete(syskey, conn);
			} else {
				rs.setMsg("Can't Connect To Database");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static Student getStudent(String syskey) {

		Student student = new Student();
		try (Connection conn = ConnAdmin.getConn()) {
			if (conn != null) {
				student = Service001Dao.getStudent(syskey, conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return student;
	}

	public static Result upload(InputStream image, FormDataContentDisposition fileMetaData) {
		Result rs = new Result();
		Student s = new Student();
		try {
			String uniqueFilename = UUID.randomUUID().toString() + ".jpg";
			String serverDirectory = ServerSession.serverPath + "WEB-INF/image/";
			String serverPath = serverDirectory + uniqueFilename;
			System.out.println(serverPath);
			try (OutputStream outputStream = new FileOutputStream(serverPath)) {
				int bytesRead;
				byte[] buffer = new byte[4096];
				while ((bytesRead = image.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			UQImageName = uniqueFilename;
			s.setPhoto(uniqueFilename);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static DetailSet getDetailForExcel() {
		DetailSet sd = new DetailSet();
		try (Connection conn = ConnAdmin.getConn()) {
			if (conn != null) {
				sd = Service001Dao.getDetailForExcel(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sd;
	}

	public static Result ExcelImport(InputStream excelFile, FormDataContentDisposition fileMetaData) {
		Result rs = new Result();
		try (Connection conn = ConnAdmin.getConn()) {

			if (conn != null) {
				rs = Service001Dao.ExcelImport(excelFile, fileMetaData, conn);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

}
