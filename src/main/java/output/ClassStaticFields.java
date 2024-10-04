/*
 * Copyright 2024 Azul Systems, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package output;

import entitites.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClassStaticFields {

    private MyClass myClass;

    private final Map<String, Entity> staticFields;

     public Entity getField(String name) {
         return staticFields.get(name);
     }

     public MyClass getMyClass() {
         return myClass;
     }

     public Map<String, Entity> getStaticFields() {
         return staticFields;
     }

     public void addStaticField(String fieldName, Entity field) {
         staticFields.put(fieldName, field);
     }

     public ClassStaticFields(MyClass myClass) {
         this(myClass, new HashMap<>());

     }

     public ClassStaticFields(MyClass myClass, Map<String, Entity> staticFields) {
         this.myClass = myClass;
         this.staticFields = new HashMap<>(staticFields);
     }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassStaticFields that = (ClassStaticFields) o;
        return Objects.equals(myClass, that.myClass) && Objects.equals(staticFields, that.staticFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myClass, staticFields);
    }
}
