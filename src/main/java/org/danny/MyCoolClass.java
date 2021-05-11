package org.danny;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;


public class MyCoolClass {

    public static void main(String[] args) throws IOException {

        Yaml yamlReader = new Yaml();
        Map<String, Object> returnedObj = yamlReader.load(getInputStream(args));
        assert returnedObj != null : "file yamlReader should not be empty";

        Map<String, Object> components = (HashMap<String, Object>) returnedObj.get("components");
        assert components != null : "components should not be empty";

        Map<String, Object> schemas = (HashMap<String, Object>) components.get("schemas");
        assert schemas != null : "schemas should not be empty";
        for (String key : returnedObj.keySet()) {
            if (key.equalsIgnoreCase("paths")) {
                Object o = returnedObj.get(key);
                Map<String, Object> tempMap = (HashMap<String, Object>) o;
                tempMap.forEach((key2, value) -> {
                    Map<String, Object> postObj = (HashMap<String, Object>) value;
                    Map<String, Object> post = (HashMap<String, Object>) postObj.get("post");

                    post.forEach((key1, value1) -> {
                        if (key1.equalsIgnoreCase("responses")) {
                            // TODO how to make this more abstract
                            Map<String, Object> responseResult = (HashMap) value1;
                            Map<String, Object> content200 = (HashMap) responseResult.get("200");
                            Map<String, Object> schema = (HashMap) content200.get("content");
                            Map<String, Object> contentType = (HashMap) schema.get("application/json");

                            Object schema1 = contentType.get("schema");
                            if (!schema1.toString().contains("$ref")) {
                                Map<String, Object> oneMoreTime = (HashMap) schema1;
                                Map<String, Object> properties = (HashMap) oneMoreTime.get("properties");
                                String s = properties.keySet().stream().findFirst().get();
                                schemas.putAll(properties);
                                Map<String, String> ref = new HashMap<>();
                                ref.put("$ref", "#/components/schemas/" + s);
                                contentType.put("schema", ref);
                                // TODO how to make this more abstract
                                schema.put("application/json", contentType);
                                content200.put("content", schema);
                                responseResult.put("200", content200);
                                post.put(key1, responseResult);
                            }
                        } else if (key1.equalsIgnoreCase("requestBody")) {
                            Map<String, Object> responseResult = (HashMap) value1;
                            Map<String, Object> schema = (HashMap) responseResult.get("content");
                            HashMap contentType = (HashMap) schema.get("application/json");
                            Object schema1 = contentType.get("schema");
                            if (!schema1.toString().contains("$ref")) {
                                Map<String, Object> oneMoreTime = (HashMap<String, Object>) schema1;
                                Map<String, Object> properties = (HashMap<String, Object>) oneMoreTime.get("properties");
                                String propertyName = properties.keySet().stream().findFirst().get();
                                schemas.putAll(properties);
                                Map<String, String> ref = new HashMap<>();
                                ref.put("$ref", "#/components/schemas/" + propertyName);
                                contentType.put("schema", ref);
                                schema.put("application/json", contentType);
                                responseResult.put("content", schema);
                                post.put(key1, responseResult);
                            }
                        }
                    });
                    postObj.put("post", post);
                    tempMap.put(key2, postObj);
                });

            }
        }
        String outputLocation = System.getProperty("user.dir").concat("/ev_api_out_" + Instant.now().getEpochSecond() + ".yaml");
        writeToFile(outputLocation, returnedObj);
    }

    private static InputStream getInputStream(String[] args) throws FileNotFoundException {
        String inputFile;
        InputStream inputStream;
        long count = Arrays.stream(args).count();
        if (count == 0) {
            inputFile = "api/ev_api.yaml_1.org";
            ClassLoader classLoader = MyCoolClass.class.getClassLoader();
            inputStream = classLoader.getResourceAsStream(inputFile);
        } else {
            inputFile = args[0];
            File initialFile = new File(inputFile);
            if (!initialFile.exists()) throw new RuntimeException("File " + initialFile + " not exists");
            inputStream = new FileInputStream(initialFile);
        }
        return inputStream;
    }

    private static void writeToFile(String outputLocation, Map<String, Object> returnedObj) throws IOException {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Path targetPath = Path.of(outputLocation);
        if (!Files.exists(targetPath)) {
            Path path = Files.createFile(targetPath).toAbsolutePath();
            outputLocation = path.toString();
        }
        FileWriter writer = new FileWriter(outputLocation);
        Yaml yamlWriter = new Yaml(options);
        yamlWriter.dump(returnedObj, writer);
    }
}
