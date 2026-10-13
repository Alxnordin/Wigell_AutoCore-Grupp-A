package com.wac.autocore.factory;

import com.wac.autocore.model.WorkOrderType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WorkOrderFactory {

    private final Map<WorkOrderType, WorkOrderCreator> creators = new EnumMap<>(WorkOrderType.class);

    public WorkOrderFactory(List<WorkOrderCreator> creators){

        //registrerar varje skapare under dess ordertyp
        for (WorkOrderCreator creator : creators){
            this.creators.put(creator.getType(), creator);
        }
    }

    public WorkOrderCreator getCreator(WorkOrderType type) {

        //hanterar skaparen som ansvarar för den valda ordertypen
        WorkOrderCreator creator = creators.get(type);

        if (creator == null) {
            throw new IllegalArgumentException(
                    "No Creator registered for work order type: " + type
            );
        }

        return creator;
    }
}
