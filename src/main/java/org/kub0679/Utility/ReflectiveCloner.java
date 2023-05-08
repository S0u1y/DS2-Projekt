package org.kub0679.Utility;

import java.lang.reflect.Method;

public class ReflectiveCloner{

    public static<T> void clone(T from, T into){

        Method[] fields = into.getClass().getDeclaredMethods();
        for (int i = 0; i < fields.length; i++) {
            Method field = fields[i];
            if(field.getName().contains("set")){ //if method is a setter. we can also ask if it is annotated with the @Setter
                try {
                    field.invoke(   //invoke the setter method, asking for the result of getter method from the other instance
                            into,
                            from.getClass().getDeclaredMethod("get" + field.getName().substring(3)).invoke(from)
                    ); //we could also "break" encapsulation by simply setting every field, but this is "safer"
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
