package demo;

public class Presentation {

//    public static void main(String[] args) {
//
//
//
//
//
//
//
//
//
//
//    }


    public static void main(String[] args) {

        String x = "";
        if (args.length > 0)

        {
            x = args[0];
        }
        else {
            x = "none";
        }

        String result = someFunction(x);

    }


    static String someFunction(String x) {


        return x;
    }




//      x: non-derivative

//      x: derivative

//      result: derivative from x


//    result: {args[0], "none"}
//
//  x: "none"
//
//  x: args[0]

}