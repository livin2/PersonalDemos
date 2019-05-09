package com.dhu777.picm.login;

import com.dhu777.picm.BasePresenter;
import com.dhu777.picm.BaseView;

public interface LoginContract {
    interface View extends BaseView<Presenter>{
        boolean isActive();
    }
    interface Presenter extends BasePresenter {

    }
}
