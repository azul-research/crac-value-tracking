package output;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReverseJson {

    List<JSONObject> results = new ArrayList<>();

    void reverseJsonStaticFields(JSONObject json) {
        var classesObject = ((JSONObject) json.get("static"));
        var classNames = classesObject.keys();
        while (classNames.hasNext()) {
            var className = classNames.next();
            var fieldsObject = ((JSONObject) classesObject.get(className));
            var fieldsNames = fieldsObject.keys();
            while (fieldsNames.hasNext()) {
                var fieldName = fieldsNames.next();
                var fieldObject = fieldsObject.get(fieldName);
                var newObject = new JSONObject();
                newObject.put("static", fieldName);
                reverseInternal(fieldObject, newObject);
            }

        }
    }

    void reverseInternal(Object json, JSONObject nextObject) {
        if (json instanceof JSONArray jsonArray) {
            for (var element : jsonArray) {
                if (element instanceof JSONObject jsonObject) {
                    if (jsonObject.has("from")) {
                        var newObject = new JSONObject();
                        newObject.put("next", nextObject);
                        newObject.put("fileName", jsonObject.get("fileName"));
                        newObject.put("line", jsonObject.get("line"));
                        reverseInternal(jsonObject.get("from"), newObject);
                    }
                    else if (jsonObject.has("root derivative")) {
                        var result = new JSONObject();
//                        result.put("from", nextObject);
                        result.put("root derivative", nextObject);
                        results.add(result);
                    }
                }
                else if (element instanceof String string) {
                    if (string.equals("nonDerivative")) {
                        return;
                    }
                    else {
                        var result = new JSONObject();
                        result.put(string, nextObject);
                        results.add(result);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ReverseJson <input-file-path> <output-file-path>");
            return;
        }
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        var reverser = new ReverseJson();
        File file = new File(inputFilePath);

        try (FileInputStream fileInputStream = new FileInputStream(file); FileWriter fileWriter = new FileWriter(outputFilePath)) {
            JSONTokener tokener = new JSONTokener(fileInputStream);
            JSONObject root = new JSONObject(tokener);

            reverser.reverseJsonStaticFields(root);

            for (var res: reverser.results) {
                System.out.println(res.toString(2));
            }

            fileWriter.write(new JSONArray(reverser.results).toString(2));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}