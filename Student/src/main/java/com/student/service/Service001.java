/*
 * package com.student.service;
 * 
 * 
 * import java.io.IOException; import java.util.ArrayList; import
 * java.util.List;
 * 
 * import javax.servlet.ServletException; import
 * javax.servlet.annotation.WebServlet; import javax.servlet.http.HttpServlet;
 * import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import
 * javax.print.attribute.standard.Media; import javax.servlet.ServletContext;
 * import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import javax.ws.rs.Consumes; import
 * javax.ws.rs.DELETE; import javax.ws.rs.GET; import javax.ws.rs.POST; import
 * javax.ws.rs.Path; import javax.ws.rs.Produces; import
 * javax.ws.rs.core.Context; import javax.ws.rs.core.MediaType;
 * 
 * import com.student.framework.ServerSession; import
 * com.student.mgr.Service001Mgr; import com.student.share.Pager; import
 * com.student.share.Result; import com.student.share.Student; import
 * com.student.share.StudentList; import com.student.share.StudentListSet;
 * 
 * @Path("/service001") public class Service001 {
 * 
 * 
 * @Context HttpServletRequest request;
 * 
 * @Context HttpServletResponse response;
 * 
 * @javax.ws.rs.core.Context static ServletContext context; public static String
 * userid = ""; public static String username = ""; public static String userpsw
 * = "";
 * 
 * private void getPath() { ServerSession.serverPath =
 * request.getServletContext().getRealPath("/") + "/";
 * 
 * }
 * 
 * 
 * @Path("/save")
 * 
 * @POST
 * 
 * @Produces(MediaType.APPLICATION_JSON)
 * 
 * @Consumes(MediaType.APPLICATION_JSON) public Result insert(Student data) {
 * getPath(); Result rs = new Result(); //rs = Service001Mgr.save(data);
 * System.out.println("in upload"); return rs;
 * 
 * }
 * 
 * @Path("/StudentList")
 * 
 * @POST
 * 
 * @Produces(MediaType.APPLICATION_JSON)
 * 
 * @Consumes(MediaType.APPLICATION_JSON) public StudentListSet list(Pager data)
 * { System.out.println("iiiii"); getPath(); StudentListSet sl = new
 * StudentListSet(); sl = Service001Mgr.getList(data); return sl;
 * 
 * }
 * 
 * @POST
 * 
 * @Path("/delete")
 * 
 * @Produces(MediaType.APPLICATION_JSON) public Result delete(Student data) {
 * 
 * Result rs = new Result(); String syskey= data.getStudent_syskey(); getPath();
 * rs = Service001Mgr.delete(syskey);
 * 
 * 
 * return rs;
 * 
 * }
 * 
 * @POST
 * 
 * @Path("/GetStudent")
 * 
 * @Produces(MediaType.APPLICATION_JSON) public Student getStudent(Student data)
 * { System.out.println("test"); Student student = new Student(); String syskey=
 * data.getStudent_syskey(); getPath(); student =
 * Service001Mgr.getStudent(syskey);
 * 
 * 
 * return student;
 * 
 * }
 * 
 * @Path("/upload")
 * 
 * @POST
 * 
 * @Produces(MediaType.APPLICATION_JSON)
 * 
 * @Consumes(MediaType.APPLICATION_JSON) public Result upload(Student data) {
 * getPath(); Result rs = new Result(); //rs = Service001Mgr.save(data);
 * System.out.println("in upload"); System.out.println(); return rs;
 * 
 * }
 * 
 * }
 */