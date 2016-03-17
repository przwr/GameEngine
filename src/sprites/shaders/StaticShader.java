package sprites.shaders;

/**
 * Created by przemek on 16.03.16.
 */
public class StaticShader extends ShaderProgram {


    private static final String VERTEX_FILE = "src/sprites/shaders/static.vert";
    private static final String FRAGMENT_FILE = "src/sprites/shaders/static.frag";

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
