package dev.rwg.chickenfight.server.models;

import lombok.Data;

import java.util.UUID;

@Data
public class ContractItem {

    UUID itemUuid;
    ItemType itemType;

    public ContractItem(UUID itemUuid, ItemType itemType) {
        this.itemUuid = itemUuid;
        this.itemType = itemType;
    }
}
