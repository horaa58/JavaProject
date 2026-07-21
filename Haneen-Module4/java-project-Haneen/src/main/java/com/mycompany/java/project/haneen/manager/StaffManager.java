
package com.mycompany.java.project.haneen.manager;
import java.util.ArrayList;
import com.mycompany.java.project.haneen.model.ParkingStaff;
public class StaffManager {
    
    private ArrayList<ParkingStaff> staffList;
    
    public StaffManager() {
    staffList = new ArrayList<>();
}
    
public void addStaff(ParkingStaff staff) {
    staffList.add(staff);
}

public ArrayList<ParkingStaff> getAllStaff() {
    return staffList;
}

public ParkingStaff searchStaffById(String staffId) {
    for (ParkingStaff staff : staffList) {
        if (staff.getStaffId().equalsIgnoreCase(staffId)) {
            return staff;
        }
    }

    return null;
}

public boolean deleteStaff(String staffId) {

    for (ParkingStaff staff : staffList) {

        if (staff.getStaffId().equalsIgnoreCase(staffId)) {
            staffList.remove(staff);
            return true;
        }

    }

    return false;
}

public boolean updateStaff(ParkingStaff updatedStaff) {

    for (ParkingStaff staff : staffList) {

        if (staff.getStaffId().equalsIgnoreCase(updatedStaff.getStaffId())) {

            staff.setStaffName(updatedStaff.getStaffName());
            staff.setRole(updatedStaff.getRole());
            staff.setAssignedGate(updatedStaff.getAssignedGate());
            staff.setAssignedFloor(updatedStaff.getAssignedFloor());
            staff.setShiftTime(updatedStaff.getShiftTime());
            staff.setStaffStatus(updatedStaff.getStaffStatus());

            return true;
        }
    }

    return false;
}


    
}
