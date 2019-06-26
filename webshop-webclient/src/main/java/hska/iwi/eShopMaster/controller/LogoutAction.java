package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.ApiConfig;

public class LogoutAction extends ActionSupport {

    private static final long serialVersionUID = -530488065543708898L;

    public String execute() {
        // Clear session:
        ActionContext.getContext().getSession().clear();
        ApiConfig.resetRestTemplate();

        return "success";
    }

}
