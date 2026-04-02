package net.shlab.hogefugapiyo.equipmentlending.application.command;

import net.shlab.hogefugapiyo.framework.core.service.CommandService;

public interface UpdateEquipmentInfoCommandService extends CommandService<UpdateEquipmentInfoCommandService.Request> {

    record Request(String adminUserId, long equipmentId, String equipmentName, String statusCode, String remarks, int version) {
    }

    record HistoryResponse(String commandId, long equipmentId) {
    }
}
