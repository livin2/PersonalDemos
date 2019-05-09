package com.dhu777.picm.mock;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.mock.data.FakeLoginRepositrory;

public class Injection {
    public static LoginDataSource provideLoginRepositrory(){
        return  FakeLoginRepositrory.getInstance();
    }


}
