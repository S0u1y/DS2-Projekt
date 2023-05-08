package org.kub0679.Utility;

import java.util.HashMap;
import java.util.Map;

//basic mapper class for all CRUD operations
//ensures CRUD operations are created only ONCE per class - statically
public class CRUDMAPPER {
    public record crud(
            String CREATE,
            String SELECT,
            String UPDATE,
            String DELETE
    ) {}

    public static Map<String, crud> cruds = new HashMap<>();

}
