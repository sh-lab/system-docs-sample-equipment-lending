package net.shlab.hogefugapiyo.equipmentlending.application.command;

import net.shlab.hogefugapiyo.framework.core.service.CommandService;

public interface RegisterEquipmentCommandService extends CommandService<RegisterEquipmentCommandService.Request> {

    record Request(String adminUserId, String equipmentName, String equipmentType, String storageLocation,
                   String statusCode, String remarks) {
    }

    record HistoryResponse(String commandId, long equipmentId) {
    }
}
