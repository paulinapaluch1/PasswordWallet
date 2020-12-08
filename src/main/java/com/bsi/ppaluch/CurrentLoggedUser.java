
package com.bsi.ppaluch;

import com.bsi.ppaluch.entity.User;

public class CurrentLoggedUser {

    private static User user;

    public static void setUser(User _user){
        user = _user;
    }

    public static User getUser() {
        return user;
    }


}
