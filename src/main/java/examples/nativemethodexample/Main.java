package examples.nativemethodexample;

import input.ControlFlowGraph;
import javassist.CtBehavior;
import javassist.Modifier;

public class Main {

    public static void main(String[] args) {
        var cfg = new ControlFlowGraph("/Users/dariasuvorova/IdeaProjects/unsafecodeanalysis/src/main/java/examples/nativemethodexample");

        CtBehavior method = cfg.getBehavior("examples.nativemethodexample.Base", "registerNatives", "()V");


        System.out.println(method.getModifiers());


        if (Modifier.isNative(method.getModifiers())) {
            System.out.println("The method is native.");
        } else {
            System.out.println("The method is not native.");
        }
    }
}
