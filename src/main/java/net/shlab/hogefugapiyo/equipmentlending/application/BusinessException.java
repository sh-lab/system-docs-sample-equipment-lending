package net.shlab.hogefugapiyo.equipmentlending.application;

public class BusinessException extends RuntimeException {

    private final String messageId;

    public BusinessException(String messageId) {
        super(messageId);
        this.messageId = messageId;
    }

    public String messageId() {
        return messageId;
    }
}
