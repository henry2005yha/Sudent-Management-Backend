package com.student.share;


import java.util.ArrayList;
import java.util.List;

public class Student {
	private String student_syskey = "";
	private String student_id = "";
	private String name = "";
	private String nrc = "";
	private String dob = "";
	private String phone = "";
	private String gender = "";
	private String nationality = "";
	private String address = "";
	private String photo="";
	private boolean photochange=false;
	public boolean isPhotochange() {
		return photochange;
	}
	public void setPhotochange(boolean photochange) {
		this.photochange = photochange;
	}
	List<StudentDetail> detail = new ArrayList<StudentDetail>();
	
	public String getStudent_id() {
		return student_id;
	}
	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNrc() {
		return nrc;
	}
	public void setNrc(String nrc) {
		this.nrc = nrc;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getStudent_syskey() {
		return student_syskey;
	}
	public void setStudent_syskey(String student_syskey) {
		this.student_syskey = student_syskey;
	}
	public List<StudentDetail> getDetail() {
		return detail;
	}
	public void setDetail(List<StudentDetail> detail) {
		this.detail = detail;
	}
	
	
	
}
