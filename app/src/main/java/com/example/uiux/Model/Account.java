package Model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Account {
    private String account_id;
    private String fullname;
    private String email;
    private String password;
    private Date birthday;
    private  String gender;
    private  String Phone;
    private  int account_type;
    private String address;

    private String image;

    public Account() {
    }

    public Account(String account_id, String fullname, String email, String password, Date birthday, String gender, String phone, int account_type, String address, String image) {
        this.account_id = account_id;
        this.fullname=fullname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
        Phone = phone;
        this.account_type = account_type;
        this.address = address;
        this.image = image;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
