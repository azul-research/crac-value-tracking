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

import entitites.*;
import entitites.derivatives.Derivative;
import entitites.derivatives.RootDerivative;

import java.util.*;

public class CurrentState {

    private final Entity[] variablesArray;
    private ArrayDeque<Entity> stack;
    private Optional<Entity> returnState;

    private final JVMState jvmState;

    public CurrentState(Entity[] variables, ArrayDeque<Entity> stack, Optional<Entity> returnState, JVMState jvmState) {
        this.variablesArray = variables.clone();
        this.returnState = returnState;
        this.stack = new ArrayDeque<>(stack);
        this.jvmState = new JVMState(jvmState);
    }

    public CurrentState(CurrentState state) {
        this.variablesArray = state.variablesArray.clone();
        this.returnState = state.returnState;
        this.stack = new ArrayDeque<>(state.stack);
        this.jvmState = new JVMState(state.jvmState);
    }

    public Set<MyClass> getLoadedClasses() {
        return jvmState.getLoadedClasses();
    }

    public Map<MyClass, ClassStaticFields> getInitialisedClasses() {
        return jvmState.getInitializedClasses();
    }

    public ArrayDeque<Entity> getStack() {
        return stack;
    }

    public Entity[] getVariablesArray() {
        return variablesArray;
    }

    public Optional<Entity> getReturnState() {
        return returnState;
    }

    public JVMState getJvmState() {
        return jvmState;
    }

    public Entity getVariableValue(int var) {
        return variablesArray[var];
    }

    public void setEmptyReturn() {
        returnState = Optional.empty();
    }

    public void updateStack(ArrayDeque<Entity> stack) {
        this.stack = new ArrayDeque<>(stack);
    }

    public void updateReturnState(Entity entity) {
        this.returnState = Optional.of(entity);
    }

    public void updateVariable(Integer number, Entity newValue) {
        variablesArray[number] = newValue;
    }

    public void updateInitialisedClasses(Map<MyClass, ClassStaticFields> newInitialisedClasses) {
        jvmState.setInitializedClasses(newInitialisedClasses);
    }

    public boolean isVariableDerivative(Integer var) {
        return variablesArray[var].isDerivativeSet();
    }

    public static CurrentState getEmptyState(int numberOfVars, Map<Integer, Entity> derivatives, JVMState jvmState) {
        Entity[] localVariables = new Entity[numberOfVars];
        Arrays.fill(localVariables, new Entity(Entity.Type.UNDEFINED));
        for (var number: derivatives.keySet()) {
            localVariables[number] = derivatives.get(number);
        }
        return new CurrentState(localVariables, new ArrayDeque<>(), Optional.empty(), jvmState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentState state = (CurrentState) o;
        return Objects.deepEquals(variablesArray, state.variablesArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(variablesArray);
    }

}
