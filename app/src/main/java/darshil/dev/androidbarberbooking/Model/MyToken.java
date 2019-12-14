package darshil.dev.androidbarberbooking.Model;

import darshil.dev.androidbarberbooking.Common.Common;

public class MyToken {

    private  String userPhone;
    private String token;
    private Common.TOKEN_TYPE tokenType;

    public MyToken() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Common.TOKEN_TYPE getTokenType() {
        return tokenType;
    }

    public void setTokenType(Common.TOKEN_TYPE tokenType) {
        this.tokenType = tokenType;
    }
}
