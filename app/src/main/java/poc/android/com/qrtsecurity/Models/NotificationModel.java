package poc.android.com.qrtsecurity.Models;

public class NotificationModel {

    private String tripId;
    private String triggeredTimestamp;
    private String passengerName;
    private String passengerPhone;
    private String passengerEmerName;
    private String passengerEmerPhone;
    private String driverName;
    private String driverPhone;
    private String triggerStatus;
    private LocationModel triggerdLocation;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTriggeredTimestamp() {
        return triggeredTimestamp;
    }

    public void setTriggeredTimestamp(String triggeredTimestamp) {
        this.triggeredTimestamp = triggeredTimestamp;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getPassengerEmerName() {
        return passengerEmerName;
    }

    public void setPassengerEmerName(String passengerEmerName) {
        this.passengerEmerName = passengerEmerName;
    }

    public String getPassengerEmerPhone() {
        return passengerEmerPhone;
    }

    public void setPassengerEmerPhone(String passengerEmerPhone) {
        this.passengerEmerPhone = passengerEmerPhone;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(String triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public LocationModel getTriggerdLocation() {
        return triggerdLocation;
    }

    public void setTriggerdLocation(LocationModel triggerdLocation) {
        this.triggerdLocation = triggerdLocation;
    }
}
