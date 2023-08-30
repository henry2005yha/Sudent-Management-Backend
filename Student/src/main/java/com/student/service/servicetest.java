package com.student.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.core.header.FormDataContentDisposition;

import com.student.framework.ServerSession;
import com.student.mgr.Service001Mgr;
import com.student.share.DetailSet;
import com.student.share.Pager;
import com.student.share.Result;
import com.student.share.Student;
import com.student.share.StudentList;
import com.student.share.StudentListSet;
import com.student.share.detailid;

@Path("/service001")
public class servicetest {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@javax.ws.rs.core.Context
	static ServletContext context;
	public static String userid = "";
	public static String username = "";
	public static String userpsw = "";

	private void getPath() {
		ServerSession.serverPath = request.getServletContext().getRealPath("/") + "/";

	}

	@Path("/save")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Result insert(Student data) {
		getPath();
		Result rs = new Result();
		rs = Service001Mgr.save(data);
		return rs;

	}

	@Path("/StudentList")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public StudentListSet list(Pager data) {
		System.out.println("iiiii");
		getPath();
		StudentListSet sl = new StudentListSet();
		sl = Service001Mgr.getList(data);
		return sl;

	}

	@POST
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Result delete(Student data) {

		Result rs = new Result();
		String syskey = data.getStudent_syskey();
		getPath();
		rs = Service001Mgr.delete(syskey);

		return rs;

	}

	@POST
	@Path("/GetStudent")
	@Produces(MediaType.APPLICATION_JSON)
	public Student getStudent(Student data) {
		// System.out.println("test");
		Student student = new Student();
		String syskey = data.getStudent_syskey();
		getPath();
		student = Service001Mgr.getStudent(syskey);

		return student;

	}

	@Path("/upload")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Result upload(@FormDataParam("file") InputStream image,
			@FormDataParam("file") FormDataContentDisposition fileMetaData) {
		getPath();
		System.out.println("in upload");
		Result rs = new Result();
		System.out.println(fileMetaData.getName() + fileMetaData.getFileName());
		
		rs = Service001Mgr.upload(image, fileMetaData);
		return rs;

	}

	@Path("/image")
	@POST
	@Produces({ "image/png", "image/jpg" })
	public Response getFullImage(Student data) {
		//System.out.println("image test");
		getPath();
		String imageName = data.getPhoto();
		System.out.println(imageName);
		String imagePath = ServerSession.serverPath + "WEB-INF/image/" + imageName;

		BufferedImage image;
		try {
			image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// Write the image to the ByteArrayOutputStream in PNG format
			ImageIO.write(image, "png", baos);
		} catch (IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		byte[] imageData = baos.toByteArray();

		// Set appropriate headers for the response
		return Response.ok(imageData).header("Content-Disposition", "attachment; filename=image.png").build();
	}

	@Path("/excel")
	@GET
	public Response getExcel() {
		getPath();
		System.out.println("in excel");
		try (Workbook wb = new XSSFWorkbook()) {
			Sheet infoSheet = wb.createSheet("Student Information");
			Row headerRow = infoSheet.createRow(0);

			// For HEADER
			String[] infoHeaders = { "ID", "Name", "NRC", "DOB", "Phone", "Gender", "Nationality", "Address" };
			for (int i = 0; i < infoHeaders.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(infoHeaders[i]);
			}

			// FOR DATA
			// get data from db
			StudentListSet studentList = Service001Mgr.getListForExcel();
			System.out.println(studentList.getList());
			int rowIndex = 1;
			for (StudentList student : studentList.getList()) {
				System.out.println(student.getName());
				Row dataRow = infoSheet.createRow(rowIndex++);

				Cell idCell = dataRow.createCell(0);
				idCell.setCellValue(student.getStudent_id());

				Cell nameCell = dataRow.createCell(1);
				nameCell.setCellValue(student.getName());

				Cell nrcCell = dataRow.createCell(2);
				nrcCell.setCellValue(student.getNrc());

				Cell dobCell = dataRow.createCell(3);
				dobCell.setCellValue(student.getDob());

				Cell phoneCell = dataRow.createCell(4);
				phoneCell.setCellValue(student.getPhone());

				Cell genderCell = dataRow.createCell(5);
				genderCell.setCellValue(student.getGender());

				Cell nationalityCell = dataRow.createCell(6);
				nationalityCell.setCellValue(student.getNationality());

				Cell addressCell = dataRow.createCell(7);
				addressCell.setCellValue(student.getAddress());
			}

			// detail Sheet
			Sheet detailSheet = wb.createSheet("Student Detail");
			Row header = detailSheet.createRow(0);

			// For HEADER
			String[] detailHeaders = { "Name","ID", "YEAR", "MARK 1", "MARK 2", "MARK 3", "REMARK" };
			for (int i = 0; i < detailHeaders.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(detailHeaders[i]);
			}

			// FOR DATA
			// get data from db
			DetailSet detail = Service001Mgr.getDetailForExcel();
			int RowIndex = 1;
			for (detailid student : detail.getDeatilList()) {
				System.out.println(student.getStudent_id());
				Row dataRow = detailSheet.createRow(RowIndex++);
				
				Cell syskeyCell = dataRow.createCell(0);
				syskeyCell.setCellValue(student.getName());
				
				Cell idCell = dataRow.createCell(1);
				idCell.setCellValue(student.getStudent_id());

				Cell yearCell = dataRow.createCell(2);
				yearCell.setCellValue(student.getYear());

				Cell m1Cell = dataRow.createCell(3);
				m1Cell.setCellValue(student.getMark1());

				Cell m2Cell = dataRow.createCell(4);
				m2Cell.setCellValue(student.getMark2());

				Cell m3Cell = dataRow.createCell(5);
				m3Cell.setCellValue(student.getMark3());

				Cell remarkCell = dataRow.createCell(6);
				remarkCell.setCellValue(student.getRemark());

			}

			// Prepare the response
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			wb.write(outputStream);
			byte[] excelBytes = outputStream.toByteArray();

			// Set appropriate headers for the response
			Response.ResponseBuilder responseBuilder = Response.ok(excelBytes);
			responseBuilder.header("Content-Disposition", "attachment; filename=example.xlsx");

			return responseBuilder.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@Path("import")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Result ExcelImport(@FormDataParam("file") InputStream excelFile, @FormDataParam("file") FormDataContentDisposition fileMetaData) {
		getPath();
		System.out.println("in ExcelImport");
		Result rs = new Result();
		System.out.println(fileMetaData.getName()+fileMetaData.getFileName());
		
		rs = Service001Mgr.ExcelImport(excelFile, fileMetaData);
		return rs;
		
	}
	
}
