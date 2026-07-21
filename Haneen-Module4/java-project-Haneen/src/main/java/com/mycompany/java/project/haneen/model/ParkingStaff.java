/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.java.project.haneen.model;
import java.io.Serializable;

/**
 *
 * @author haneen_almutairi
 */
public class ParkingStaff implements Serializable {
    
    private String staffId;
    private String staffName;
    private String role;
    private String assignedGate;
    private String assignedFloor;
    private String shiftTime;
    private String staffStatus;
    
    public ParkingStaff(String staffId, String staffName, String role,String assignedGate, String assignedFloor,String shiftTime, String staffStatus) {

    this.staffId = staffId;
    this.staffName = staffName;
    this.role = role;
    this.assignedGate = assignedGate;
    this.assignedFloor = assignedFloor;
    this.shiftTime = shiftTime;
    this.staffStatus = staffStatus;
}

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAssignedGate(String assignedGate) {
        this.assignedGate = assignedGate;
    }

    public void setAssignedFloor(String assignedFloor) {
        this.assignedFloor = assignedFloor;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
    }

    public void setStaffStatus(String staffStatus) {
        this.staffStatus = staffStatus;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getRole() {
        return role;
    }

    public String getAssignedGate() {
        return assignedGate;
    }

    public String getAssignedFloor() {
        return assignedFloor;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public String getStaffStatus() {
        return staffStatus;
    }
    
    @Override
    public String toString() {
       return "ParkingStaff{" +
            "staffId=" + staffId +
            ", staffName=" + staffName +
            ", role=" + role +
            ", assignedGate=" + assignedGate +
            ", assignedFloor=" + assignedFloor +
            ", shiftTime=" + shiftTime +
            ", staffStatus=" + staffStatus +
            '}';
}
    
    
    
}
