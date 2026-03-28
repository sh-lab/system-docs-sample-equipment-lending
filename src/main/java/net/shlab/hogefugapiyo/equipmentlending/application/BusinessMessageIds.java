package net.shlab.hogefugapiyo.equipmentlending.application;

/**
 * 業務メッセージIDの定数を管理する。
 */
public final class BusinessMessageIds {

    public static final String LENDING_REQUEST_ACCEPTED = "MSG_I_002";
    public static final String RETURN_REQUEST_ACCEPTED = "MSG_I_003";
    public static final String REJECTED_CONFIRM_COMPLETED = "MSG_I_004";
    public static final String APPROVE_REQUEST_COMPLETED = "MSG_I_005";
    public static final String REJECT_REQUEST_COMPLETED = "MSG_I_006";
    public static final String RETURN_CONFIRM_COMPLETED = "MSG_I_007";
    public static final String EQUIPMENT_SELECTION_INVALID = "MSG_E_001";
    public static final String RETURN_REQUEST_INVALID = "MSG_E_002";
    public static final String REJECTED_CONFIRM_INVALID = "MSG_E_003";
    public static final String REQUEST_DISPLAY_INVALID = "MSG_E_004";
    public static final String APPROVE_REQUEST_INVALID = "MSG_E_005";
    public static final String REJECT_REQUEST_INVALID = "MSG_E_006";
    public static final String RETURN_CONFIRM_INVALID = "MSG_E_007";

    private BusinessMessageIds() {
    }
}
