package net.shlab.hogefugapiyo.equipmentlending.application.query;

public record UserLendingRequestEquipmentDto(
        long equipmentId,
        String equipmentCode,
        String equipmentName,
        String equipmentTypeLabel,
        String storageLocation
) {
}
