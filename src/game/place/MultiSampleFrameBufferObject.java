package game.place;

import game.Settings;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBTextureMultisample;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class MultiSampleFrameBufferObject extends FrameBufferObject {

    private final int frameBufferObjectMultiSampler;
    private final int multiSampleTexture;
    private final makeMultiSampleObject[] makeMultiSamples = new makeMultiSampleObject[3];

    public MultiSampleFrameBufferObject(int width, int height) {
        super(width, height);
        multiSampleTexture = glGenTextures();
        if (version == NATIVE) {
            frameBufferObjectMultiSampler = GL30.glGenFramebuffers();
        } else if (version == ARB) {
            frameBufferObjectMultiSampler = ARBFramebufferObject.glGenFramebuffers();
        } else {
            frameBufferObjectMultiSampler = EXTFramebufferObject.glGenFramebuffersEXT();
        }
        if (multiSampleSupported) {
            activates[NATIVE] = () -> {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBufferObjectMultiSampler);
            };
            activates[ARB] = () -> {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, frameBufferObjectMultiSampler);
            };
            deactivates[NATIVE] = () -> {
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBufferObjectMultiSampler);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBufferObject);
                GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            };
            deactivates[ARB] = () -> {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, frameBufferObjectMultiSampler);
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, frameBufferObject);
                ARBFramebufferObject.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
            };
            makeMultiSamples[NATIVE] = (int samplesCount) -> {
                glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture);
                GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, width, height, false);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectMultiSampler);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture, 0);
            };
            makeMultiSamples[ARB] = (int samplesCount) -> {
                glBindTexture(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture);
                ARBTextureMultisample.glTexImage2DMultisample(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, width, height, false);
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, frameBufferObjectMultiSampler);
                ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture, 0);
            };
        } else {
            activates[NATIVE] = () -> {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBufferObject);
            };
            activates[ARB] = () -> {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, frameBufferObject);
            };
            deactivates[NATIVE] = () -> {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            };
            deactivates[ARB] = () -> {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
            };
            makeMultiSamples[NATIVE] = (int samplesCount) -> {
            };
            makeMultiSamples[ARB] = (int samplesCount) -> {
            };
        }
        activates[EXT] = () -> {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBufferObject);
        };
        deactivates[EXT] = () -> {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        };
        makeTextures[NATIVE] = () -> {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBufferObject);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
            GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
        };
        makeTextures[ARB] = () -> {
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, frameBufferObject);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
            ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
        };
        makeTextures[EXT] = () -> {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBufferObject);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
            EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture, 0);
        };
        makeMultiSamples[EXT] = (int samplesCount) -> {
        };
        makeMultiSample(Settings.samplesCount);
        makeTexture();
        if (version == NATIVE) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        } else if (version == ARB) {
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
        } else {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        }
    }

    @Override
    public void activate() {
        activates[version].activate();
    }

    @Override
    public void deactivate() {
        deactivates[version].deactivate();
    }

    private void makeTexture() {
        makeTextures[version].makeTexture();
    }

    private void makeMultiSample(int samplesCount) {
        makeMultiSamples[version].makeMultiSample(samplesCount);
    }

    private interface makeMultiSampleObject {

        void makeMultiSample(int samplesCount);
    }

}
