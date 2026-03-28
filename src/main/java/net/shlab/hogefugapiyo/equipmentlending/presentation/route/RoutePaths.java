package net.shlab.hogefugapiyo.equipmentlending.presentation.route;

import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;

/**
 * 画面遷移で利用するルートパス定数を管理する。
 */
public final class RoutePaths {

    public static final String ROOT = "/";
    public static final String LOGIN = "/" + Views.LOGIN;
    public static final String HFP_ELV100_USER_MYPAGE = "/" + Views.HFP_ELV100_USER_MYPAGE;
    public static final String HFP_ELV200_ADMIN_MYPAGE = "/" + Views.HFP_ELV200_ADMIN_MYPAGE;
    public static final String HFP_ELV300_EQUIPMENT_SEARCH = "/" + Views.HFP_ELV300_EQUIPMENT_SEARCH;
    public static final String HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START = HFP_ELV300_EQUIPMENT_SEARCH + "/lending-start";
    public static final String HFP_ELV400_USER_LENDING_REQUEST = "/" + Views.HFP_ELV400_USER_LENDING_REQUEST;
    public static final String HFP_ELV400_USER_LENDING_REQUEST_LENDING = HFP_ELV400_USER_LENDING_REQUEST + "/lending";
    public static final String HFP_ELV400_USER_LENDING_REQUEST_RETURN = HFP_ELV400_USER_LENDING_REQUEST + "/return";
    public static final String HFP_ELV400_USER_LENDING_REQUEST_REJECTED_CONFIRM =
            HFP_ELV400_USER_LENDING_REQUEST + "/rejected-confirm";
    public static final String HFP_ELV500_ADMIN_LENDING_REVIEW = "/" + Views.HFP_ELV500_ADMIN_LENDING_REVIEW;
    public static final String HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE = HFP_ELV500_ADMIN_LENDING_REVIEW + "/approve";
    public static final String HFP_ELV500_ADMIN_LENDING_REVIEW_REJECT = HFP_ELV500_ADMIN_LENDING_REVIEW + "/reject";
    public static final String HFP_ELV500_ADMIN_LENDING_REVIEW_RETURN_CONFIRM =
            HFP_ELV500_ADMIN_LENDING_REVIEW + "/return-confirm";

    private RoutePaths() {
    }

    public static String fromView(String viewName) {
        return "/" + viewName;
    }
}
