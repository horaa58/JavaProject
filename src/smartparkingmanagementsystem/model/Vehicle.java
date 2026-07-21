package smartparkingmanagementsystem.model;

import java.io.Serializable;
import java.util.Objects;

/** Master registration record for one vehicle. */
public final class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String vehicleNumber;
    private final String ownerName;
    private final String phone;
    private final VehicleType vehicleType;
    private final RegistrationStatus registrationStatus;

    public Vehicle(String vehicleNumber, String ownerName, String phone,
            VehicleType vehicleType, RegistrationStatus registrationStatus) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.registrationStatus = registrationStatus;
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public String getOwnerName() { return ownerName; }
    public String getPhone() { return phone; }
    public VehicleType getVehicleType() { return vehicleType; }
    public RegistrationStatus getRegistrationStatus() { return registrationStatus; }

    public Vehicle withStatus(RegistrationStatus status) {
        return new Vehicle(vehicleNumber, ownerName, phone, vehicleType, status);
    }

    @Override public boolean equals(Object value) {
        if (this == value) return true;
        if (!(value instanceof Vehicle other)) return false;
        return Objects.equals(vehicleNumber, other.vehicleNumber)
                && Objects.equals(ownerName, other.ownerName) && Objects.equals(phone, other.phone)
                && vehicleType == other.vehicleType && registrationStatus == other.registrationStatus;
    }

    @Override public int hashCode() {
        return Objects.hash(vehicleNumber, ownerName, phone, vehicleType, registrationStatus);
    }

    @Override public String toString() {
        return "Vehicle{vehicleNumber='" + vehicleNumber + "', ownerName='" + ownerName
                + "', phone='" + phone + "', vehicleType=" + vehicleType
                + ", registrationStatus=" + registrationStatus + "}";
    }
}
