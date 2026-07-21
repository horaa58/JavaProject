package com.mycompany.java.project.haneen.file;

import com.mycompany.java.project.haneen.model.ParkingStaff;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class StaffFileHandler {

    private final String FILE_NAME = "parking_staff.dat";

    public void saveStaff(ArrayList<ParkingStaff> staffList) {

        try {
            FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(staffList);

            out.close();
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ParkingStaff> loadStaff() {

        ArrayList<ParkingStaff> staffList = new ArrayList<>();

        try {
            FileInputStream fileIn = new FileInputStream(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            staffList = (ArrayList<ParkingStaff>) in.readObject();

            in.close();
            fileIn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return staffList;
    }
}