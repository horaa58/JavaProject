package com.mycompany.java.project.haneen.file;

import com.mycompany.java.project.haneen.model.ParkingEntry;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ParkingEntryFileHandler {

    private final String FILE_NAME = "parking_entries.dat";

    public void saveEntries(ArrayList<ParkingEntry> entryList) {

        try {
            FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(entryList);

            out.close();
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ParkingEntry> loadEntries() {

        ArrayList<ParkingEntry> entryList = new ArrayList<>();

        try {
            FileInputStream fileIn = new FileInputStream(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            entryList = (ArrayList<ParkingEntry>) in.readObject();

            in.close();
            fileIn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return entryList;
    }
}