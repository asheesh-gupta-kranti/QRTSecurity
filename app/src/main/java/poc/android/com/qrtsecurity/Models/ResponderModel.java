package poc.android.com.qrtsecurity.Models;

/**
 * Created by ashutoshmishra on 12/09/18.
 */

public class ResponderModel {

    private int responderId;
    private String responderName;
    private String responderPhone;
    private String dob;
    private String vehicleRegNo;
    private String licenceNo;
    private String gender;

    public int getResponderId() {
        return responderId;
    }

    public void setResponderId(int responderId) {
        this.responderId = responderId;
    }

    public String getResponderName() {
        return responderName;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    public String getResponderPhone() {
        return responderPhone;
    }

    public void setResponderPhone(String responderPhone) {
        this.responderPhone = responderPhone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getVehicleRegNo() {
        return vehicleRegNo;
    }

    public void setVehicleRegNo(String vehicleRegNo) {
        this.vehicleRegNo = vehicleRegNo;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
