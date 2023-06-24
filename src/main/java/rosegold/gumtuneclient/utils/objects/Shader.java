package rosegold.gumtuneclient.utils.objects;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;
import rosegold.gumtuneclient.GumTuneClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class Shader {
    public int pointer;

    public Shader() {
        int __result = GL20.glCreateProgram();
        try {
            GL20.glAttachShader(__result, compileShader("#version 120\n" +
                    "varying float alpha;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = gl_Color;\n" +
                    "}", 35632));
            GL20.glAttachShader(__result, compileShader("#version 120\n" +
                    "#extension GL_EXT_draw_instanced : enable\n" +
                    "#extension GL_EXT_gpu_shader4 : enable\n" +
                    "uniform vec3 positions[512];\n" +
                    "void main() {\n" +
                    "    gl_FrontColor = gl_Color;\n" +
                    "    gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.xyz + positions[gl_InstanceID], 1.0);\n" +
                    "}", 35633));
            GL20.glLinkProgram(__result);

            if (GL20.glGetProgrami(__result, 35714) == 0) throw new IllegalStateException("Shader failed to link!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.pointer = __result;
    }

    private int compileShader(String code, int type) {
        try {
            int result = GL20.glCreateShader(type);

            GL20.glShaderSource(result, code);
            GL20.glCompileShader(result);

            if (GL20.glGetShaderi(result, 35713) == 0) throw new IllegalStateException("Failed to compile shader! " + GL20.glGetShaderInfoLog(result, 4096));

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to compile shader!");
        }
    }
}
