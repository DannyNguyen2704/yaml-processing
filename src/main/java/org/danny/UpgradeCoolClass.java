package org.danny;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class UpgradeCoolClass {
    private static void extract(Map<String, Object> input) {
        Set<String> strings = input.keySet();
        for (String key : strings) {
            boolean isString = input.get(key) instanceof String;
            if (!isString) {
                Map<String, Object> extracted = (HashMap) input.get(key);
                System.out.println("extracted :" + extracted);
                extract(extracted);
            }
        }
    }

    public static void main(String[] args) {
        ClassLoader classLoader = UpgradeCoolClass.class.getClassLoader();

        InputStream resourceAsStream = classLoader.getResourceAsStream("api/ev_api.yaml_1.org");
        Yaml yaml = new Yaml();
        Map<String, Object> returnedObj = yaml.load(resourceAsStream);
        assert returnedObj != null : "file yaml should not be empty";

        Map<String, Object> components = (HashMap<String, Object>) returnedObj.get("components");
        assert components != null : "components should not be empty";

        Map<String, Object> schemas = (HashMap<String, Object>) components.get("schemas");
        assert schemas != null : "schemas should not be empty";
        Map<String, Object> container = new HashMap<>();
        returnedObj.forEach((s, o) -> {
//            System.out.println(s + " === " + o);
            container.put(s,o);
        });
        System.out.println(container);
        Set<String> strings = container.keySet();
        for (String key :
                strings) {
            
        }
    }
}
