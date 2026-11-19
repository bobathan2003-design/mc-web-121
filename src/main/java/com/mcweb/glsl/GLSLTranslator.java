// src/main/java/com/mcweb/glsl/GLSLTranslator.java
package com.mcweb.glsl;

public class GLSLTranslator {

    public static String translate(String glsl) {
        if (glsl == null) return null;

        String result = glsl;

        // Already GLSL ES? Don't touch it
        if (result.contains("#version 300 es")) {
            return result;
        }

        // Replace version header
        result = result.replaceAll(
            "#version\\s+(110|120|130|140|150|330|400|410|420|430|440|450)(\\s+core)?",
            "#version 300 es\nprecision highp float;\nprecision highp int;"
        );

        // If no version at all, add one
        if (!result.contains("#version")) {
            result = "#version 300 es\nprecision highp float;\nprecision highp int;\n" + result;
        }

        // attribute -> in
        result = result.replaceAll("\\battribute\\b", "in");

        // varying -> out (vertex shader) 
        result = result.replaceAll("\\bvarying\\b", "out");

        // texture2D -> texture
        result = result.replace("texture2D(", "texture(");
        result = result.replace("texture3D(", "texture(");
        result = result.replace("textureCube(", "texture(");

        // gl_FragColor -> custom output
        if (result.contains("gl_FragColor")) {
            int insertPos = result.indexOf("precision highp int;");
            if (insertPos >= 0) {
                insertPos = result.indexOf(";", insertPos) + 1;
                result = result.substring(0, insertPos) +
                         "\nout vec4 mcweb_FragColor;\n" +
                         result.substring(insertPos);
            }
            result = result.replace("gl_FragColor", "mcweb_FragColor");
        }

        // gl_FragData[0] -> custom output
        if (result.contains("gl_FragData")) {
            result = result.replaceAll("gl_FragData\\[0\\]", "mcweb_FragColor");
        }

        return result;
    }
}
