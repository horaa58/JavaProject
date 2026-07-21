
package com.mycompany.java.project.haneen.model;
import java.io.Serializable;


public class ParkingEntry implements Serializable {
    
    private String entryId;
    private String vehicleNumber;
    private String ownerName;
    private String slotId;
    private String entryDate;
    private String entryTime;
    private String exitDate;
    private String exitTime;
    private String parkingStatus;
    private String paymentStatus;
    
    public ParkingEntry(String entryId, String vehicleNumber, String ownerName,
        String slotId, String entryDate, String entryTime,
        String exitDate, String exitTime,
        String parkingStatus, String paymentStatus) {

    this.entryId = entryId;
    this.vehicleNumber = vehicleNumber;
    this.ownerName = ownerName;
    this.slotId = slotId;
    this.entryDate = entryDate;
    this.entryTime = entryTime;
    this.exitDate = exitDate;
    this.exitTime = exitTime;
    this.parkingStatus = parkingStatus;
    this.paymentStatus = paymentStatus;
}

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public void setParkingStatus(String parkingStatus) {
        this.parkingStatus = parkingStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getExitDate() {
        return exitDate;
    }

    public String getExitTime() {
        return exitTime;
    }

    public String getParkingStatus() {
        return parkingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

   
    
}
