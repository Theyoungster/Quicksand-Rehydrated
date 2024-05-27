#version 330 core
in vec2 TexCoord; // Input texture coordinate

out vec4 FragColor; // Output color

uniform sampler2D textureSampler; // Sampler for the original texture
uniform float overlayAlpha; // Alpha value for the overlay

void main() {
    vec4 texColor = texture(textureSampler, TexCoord); // Sample the original texture
    FragColor = mix(texColor, vec4(1.0, 1.0, 1.0, overlayAlpha), overlayAlpha); // Blend with the overlay color
}