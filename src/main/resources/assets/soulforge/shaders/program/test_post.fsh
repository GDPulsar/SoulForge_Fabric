#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MainDepthSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 original = texture(DiffuseSampler, texCoord);
    vec4 depth = texture(MainDepthSampler, texCoord);
    vec4 result = original * depth;

    fragColor = result;
}