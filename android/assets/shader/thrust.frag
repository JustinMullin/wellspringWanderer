#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 position;
uniform sampler2D u_texture;

uniform vec2 thrustCenter;
uniform float thrust;

void main() {
  float center = 1.0 - min(1.0, length(thrustCenter-position)/5.0);
  float glow = 1.0 - min(1.0, length(thrustCenter-position)/15.0);

  gl_FragColor =
    vec4(center*0.75, 1.0, center, pow(center, 2.0)) * min(1.0, thrust * 3.0) +
    vec4(glow*0.75, 1.0, glow, pow(glow, 1.2)) * min(1.0, thrust * 3.0) * 0.1;
}